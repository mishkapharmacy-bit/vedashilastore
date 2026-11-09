package com.example.service

import android.util.Log
import com.example.model.Product
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "SmartAiMarketing"

data class AiMarketingAdvice(
  val optimalTimeSlot: String,
  val peakDays: String,
  val targetAudience: String,
  val inventoryActionRecommendation: String,
  val instagramCaption: String,
  val whatsappBroadcastText: String,
  val youtubeAdScriptBrief: String,
  val hashtags: List<String>,
  val isAiGenerated: Boolean = true
)

object SmartAiMarketingService {

  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      generationConfig = generationConfig {
        temperature = 0.7f
      }
    )
  }

  suspend fun generateMarketingInsights(product: Product): Result<AiMarketingAdvice> = withContext(Dispatchers.IO) {
    val stockUrgency = when {
      !product.inStock -> "OUT OF STOCK - Recommend restock urgency messaging."
      product.stockQuantity < 20 -> "LOW STOCK (${product.stockQuantity} units left) - Recommend FOMO, scarcity, and limited-stock alert angle."
      else -> "HIGH INVENTORY (${product.stockQuantity} units) - Recommend volume scaling, combo deals, and aggressive social reach."
    }

    val prompt = """
      You are the Lead Ayurvedic E-Commerce Marketing Director for Vedashila (vedashila.com).
      Analyze this product and inventory data to generate strategic social media marketing assets:
      
      Product Title: ${product.title}
      Category: ${product.category}
      Dosage Form: ${product.dosageForm}
      Health Goal: ${product.healthGoal}
      Price: ₹${product.price} (Original: ₹${product.originalPrice}, Discount: ${product.discountPercent}%)
      Stock Status: $stockUrgency
      Key Benefits: ${product.benefits.joinToString(", ")}
      
      Please provide:
      1. OPTIMAL POSTING TIME: Exact hours and why (e.g. Morning 06:30 - 08:30 AM for wellness routines).
      2. PEAK ENGAGEMENT DAYS: Best days of the week.
      3. TARGET AUDIENCE: Who should be targeted.
      4. INVENTORY STRATEGY: Action based on current stock of ${product.stockQuantity} units.
      5. INSTAGRAM CAPTION: Engaging hook + Ayurvedic insight + CTA to vedashila.com + 10-15 trending hashtags.
      6. WHATSAPP BROADCAST: Direct, personal, bulleted message with WhatsApp ordering call-to-action.
      7. YOUTUBE AD SCRIPT BRIEF: 15-30s hook and value prop.
      8. HASHTAGS: Space or comma separated tags.
    """.trimIndent()

    try {
      val response = withTimeout(45_000L) {
        model.generateContent(prompt)
      }

      val text = response.text
      if (!text.isNullOrBlank()) {
        val parsedAdvice = parseAiResponse(text, product)
        Result.success(parsedAdvice)
      } else {
        Result.success(getSmartFallbackAdvice(product))
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Gemini generation timed out", e)
      Result.success(getSmartFallbackAdvice(product, "Generated using Vedashila Smart Heuristic Engine (Timeout fallback)."))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Gemini API error, falling back to smart local heuristics", e)
      Result.success(getSmartFallbackAdvice(product, "Generated using Vedashila Smart Heuristic Engine (${e.localizedMessage ?: "Network mode"})."))
    }
  }

  private fun parseAiResponse(text: String, product: Product): AiMarketingAdvice {
    val hashtags = Regex("#\\w+").findAll(text).map { it.value }.toList()
      .ifEmpty { getDefaultHashtags(product.category) }

    val optimalTime = when {
      product.healthGoal.contains("Sleep", ignoreCase = true) -> "Night 08:30 PM - 10:00 PM (Bedtime routine)"
      product.category.contains("Shilajit", ignoreCase = true) || product.healthGoal.contains("Vitality", ignoreCase = true) -> "Morning 07:00 AM - 09:00 AM (Early energy peak)"
      product.category.contains("Glow", ignoreCase = true) || product.category.contains("Skin", ignoreCase = true) -> "Evening 06:00 PM - 08:00 PM (Post-work self-care)"
      else -> "Morning 08:00 AM - 10:00 AM & Evening 07:00 PM - 09:00 PM"
    }

    return AiMarketingAdvice(
      optimalTimeSlot = optimalTime,
      peakDays = "Wednesday, Friday & Sunday",
      targetAudience = "Fitness & wellness seekers aged 24-52 interested in authentic Ayurveda and natural stamina.",
      inventoryActionRecommendation = if (product.stockQuantity < 20) "⚡ Low Stock Alert: Run urgent limited-batch flash stories on WhatsApp & Instagram." else "🚀 Scale ad campaigns with focus on YouTube bumper ads & Instagram Reels.",
      instagramCaption = extractSection(text, "INSTAGRAM", text.take(500)),
      whatsappBroadcastText = extractSection(text, "WHATSAPP", "🌿 Special wellness update from Vedashila: ${product.title} now available at ₹${product.price} (Save ${product.discountPercent}%). Order at vedashila.com!"),
      youtubeAdScriptBrief = extractSection(text, "YOUTUBE", "Hook: Feeling low on natural stamina? Discover pure Himalayan potency with ${product.title}."),
      hashtags = hashtags.take(15),
      isAiGenerated = true
    )
  }

  private fun extractSection(fullText: String, keyword: String, fallback: String): String {
    val lines = fullText.lines()
    val startIndex = lines.indexOfFirst { it.contains(keyword, ignoreCase = true) }
    if (startIndex != -1) {
      val relevantLines = lines.drop(startIndex + 1).take(8)
        .takeWhile { !it.startsWith("#") && !it.matches(Regex("^[0-9]+\\..*")) }
      val result = relevantLines.joinToString("\n").trim()
      if (result.isNotBlank()) return result
    }
    return fallback
  }

  fun getSmartFallbackAdvice(product: Product, note: String? = null): AiMarketingAdvice {
    val (timeSlot, peakDays) = when {
      product.category.contains("Shilajit", ignoreCase = true) -> "07:00 AM - 09:30 AM" to "Monday, Wednesday & Saturday"
      product.category.contains("Chyawanprash", ignoreCase = true) || product.healthGoal.contains("Immunity", ignoreCase = true) -> "06:30 AM - 08:30 AM" to "Sunday & Tuesday"
      product.category.contains("Skin", ignoreCase = true) || product.dosageForm == "Oil" -> "06:30 PM - 09:00 PM" to "Friday & Saturday"
      product.healthGoal.contains("Stress", ignoreCase = true) -> "08:30 PM - 10:30 PM" to "Thursday & Sunday"
      else -> "08:00 AM - 10:00 AM" to "Wednesday & Weekend"
    }

    val action = when {
      !product.inStock -> "❌ OUT OF STOCK: Halt all active paid social ads immediately. Schedule restock email alerts."
      product.stockQuantity < 20 -> "🔥 FLASH SALE / FOMO: Push 'Only ${product.stockQuantity} units left in stock' urgency badges across WhatsApp & Instagram Stories."
      else -> "📈 GROWTH VELOCITY: Strong warehouse inventory (${product.stockQuantity} units). Maximize reach with YouTube in-stream video ads."
    }

    val instaCaption = """
      🌿 Awaken your true vitality with ${product.title}!
      
      Did you know that authentic Himalayan wellness begins with raw, unadulterated botanical potency?
      
      ✨ Sourced directly from 16,000+ ft Himalayan altitudes
      🔬 Certified by AYUSH & tested for 100% heavy-metal purity
      💰 Special limited deal: Only ₹${product.price} (Save ${product.discountPercent}% today)
      
      👉 Tap the link in bio or visit vedashila.com to experience authentic Ayurvedic rejuvenation.
      
      ${getDefaultHashtags(product.category).joinToString(" ")}
    """.trimIndent()

    val whatsappText = """
      *🌿 VEDASHILA EXCLUSIVE WELLNESS UPDATE*
      
      Namaste! Elevate your daily health routine with our best-selling *${product.title}*.
      
      • 100% Pure Himalayan Sourced & Lab Certified
      • Normal Price: ~₹${product.originalPrice}~ | *Member Price: ₹${product.price}*
      • Stock status: ${if (product.stockQuantity < 20) "⚠️ Only ${product.stockQuantity} units remaining" else "✅ Ready for immediate dispatch"}
      
      🚚 Free Pan-India Delivery & Cash on Delivery Available.
      
      📲 Reply *ORDER* to this message or tap to order:
      ${product.productUrl}
    """.trimIndent()

    return AiMarketingAdvice(
      optimalTimeSlot = timeSlot,
      peakDays = peakDays,
      targetAudience = "Men & women (22-55 yrs) seeking natural vigor, stress relief, and chemical-free wellness.",
      inventoryActionRecommendation = action,
      instagramCaption = instaCaption,
      whatsappBroadcastText = whatsappText,
      youtubeAdScriptBrief = "15-sec Hook: 'Feeling tired by 2 PM? Watch how 100% pure Himalayan Shilajit restores your daily Ojas in 14 days.'",
      hashtags = getDefaultHashtags(product.category),
      isAiGenerated = false
    )
  }

  private fun getDefaultHashtags(category: String): List<String> {
    val base = mutableListOf("#Vedashila", "#Ayurveda", "#NaturalWellness", "#PureHerbal", "#HolisticHealth")
    when {
      category.contains("Shilajit", ignoreCase = true) -> {
        base.addAll(listOf("#ShilajitResin", "#HimalayanShilajit", "#FulvicAcid", "#VitalityBoost", "#MenHealth"))
      }
      category.contains("Skin", ignoreCase = true) || category.contains("Glow", ignoreCase = true) -> {
        base.addAll(listOf("#KumkumadiOil", "#AyurvedicBeauty", "#GlowingSkin", "#SaffronGlow", "#CleanBeauty"))
      }
      category.contains("Immunity", ignoreCase = true) || category.contains("Chyawanprash", ignoreCase = true) -> {
        base.addAll(listOf("#Chyawanprash", "#ImmunityBooster", "#VedicHealth", "#A2Ghee", "#HerbalRemedy"))
      }
      else -> {
        base.addAll(listOf("#HerbalMedicine", "#AyurvedicLife", "#WellnessJourney", "#IndianAyurveda", "#OrganicHerbs"))
      }
    }
    return base
  }
}
