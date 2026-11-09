package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.InventoryRepository
import com.example.model.Product
import com.example.service.AiMarketingAdvice
import com.example.service.SmartAiMarketingService
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaContainerGreen
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen
import kotlinx.coroutines.launch

enum class MarketingTemplatePlatform(
  val displayName: String,
  val iconEmoji: String,
  val aspectRatio: String,
  val description: String,
  val tag: String
) {
  INSTAGRAM(
    displayName = "Instagram",
    iconEmoji = "📸",
    aspectRatio = "1:1 / 9:16",
    description = "Story & Feed post with engaging hooks, emojis, and aesthetic hashtags",
    tag = "template_instagram"
  ),
  FACEBOOK(
    displayName = "Facebook",
    iconEmoji = "👥",
    aspectRatio = "16:9 Landscape",
    description = "High-conversion ad copy with clinical proof, price highlights & CTA link",
    tag = "template_facebook"
  ),
  WHATSAPP(
    displayName = "WhatsApp",
    iconEmoji = "💬",
    aspectRatio = "Broadcast Message",
    description = "Direct, formatted broadcast copy with bullet points & direct order link",
    tag = "template_whatsapp"
  )
}

/**
 * MarketingStudioScreen: Select a product, choose a template (Instagram/Facebook/WhatsApp),
 * and use the Gemini API via Firebase AI Logic to generate customized ad copy and hashtags.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingStudioScreen(
  initialProduct: Product? = null,
  onNavigateToPosters: (Product) -> Unit = {},
  onNavigateToVideos: (Product) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  val products by InventoryRepository.products.collectAsStateWithLifecycle()

  var selectedProduct by remember(initialProduct, products) {
    mutableStateOf(initialProduct ?: products.firstOrNull() ?: InventoryRepository.categories.let { products.first() })
  }

  var selectedPlatform by remember {
    mutableStateOf(MarketingTemplatePlatform.INSTAGRAM)
  }

  var isGenerating by remember { mutableStateOf(false) }
  var marketingAdvice by remember(selectedProduct) {
    mutableStateOf(SmartAiMarketingService.getSmartFallbackAdvice(selectedProduct))
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF9FAF8))
      .testTag("screen_marketing_studio")
  ) {
    // Header Bar
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VedashilaContainerGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = VedashilaPrimaryGreen,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Marketing Studio",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF14201A)
                )
              )
              Text(
                text = "AI Ad Copy & Hashtags powered by Gemini",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF5A6E63),
                  fontSize = 11.5.sp
                )
              )
            }
          }

          // Generate / Refresh Button
          Button(
            onClick = {
              coroutineScope.launch {
                isGenerating = true
                val result = SmartAiMarketingService.generateMarketingInsights(selectedProduct)
                marketingAdvice = result.getOrNull() ?: SmartAiMarketingService.getSmartFallbackAdvice(selectedProduct)
                isGenerating = false
                Toast.makeText(context, "AI copy generated for ${selectedPlatform.displayName}!", Toast.LENGTH_SHORT).show()
              }
            },
            enabled = !isGenerating,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = VedashilaPrimaryGreen,
              contentColor = Color.White
            ),
            modifier = Modifier.testTag("btn_generate_ai_copy")
          ) {
            if (isGenerating) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Thinking...", fontSize = 12.sp)
            } else {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Generate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(6.dp))

        // Step 1: Product Selector Row
        Text(
          text = "1. Select Product",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14201A)
          )
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          products.forEach { prod ->
            val isSelected = prod.id == selectedProduct.id
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedProduct = prod
                marketingAdvice = SmartAiMarketingService.getSmartFallbackAdvice(prod)
              },
              label = {
                Text(
                  text = prod.title.take(24) + "...",
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VedashilaPrimaryGreen,
                selectedLabelColor = Color.White,
                containerColor = Color.White,
                labelColor = Color(0xFF3B4D43)
              ),
              border = FilterChipDefaults.filterChipBorder(
                borderColor = if (isSelected) VedashilaPrimaryGreen else VedashilaBorder,
                selectedBorderColor = VedashilaPrimaryGreen,
                enabled = true,
                selected = isSelected
              )
            )
          }
        }
      }

      item {
        // Step 2: Choose Template Platform (Instagram / Facebook / WhatsApp)
        Text(
          text = "2. Choose Template Platform",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14201A)
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MarketingTemplatePlatform.values().forEach { platform ->
            val isSelected = selectedPlatform == platform
            Card(
              modifier = Modifier
                .weight(1f)
                .clickable { selectedPlatform = platform }
                .testTag(platform.tag),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) VedashilaContainerGreen else Color.White
              ),
              border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) VedashilaPrimaryGreen else VedashilaBorder
              )
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(platform.iconEmoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = platform.displayName,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                  fontSize = 12.5.sp,
                  color = if (isSelected) VedashilaPrimaryGreen else Color(0xFF14201A)
                )
                Text(
                  text = platform.aspectRatio,
                  fontSize = 10.sp,
                  color = Color.Gray
                )
              }
            }
          }
        }
      }

      // Step 3: Customized Generated Copy & Hashtags
      item {
        Text(
          text = "3. Customized Ad Copy & Hashtags",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14201A)
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Platform Headline
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(selectedPlatform.iconEmoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${selectedPlatform.displayName} Customized Copy",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = Color(0xFF14201A)
                )
              }

              IconButton(
                onClick = {
                  val contentToCopy = when (selectedPlatform) {
                    MarketingTemplatePlatform.INSTAGRAM -> marketingAdvice.instagramCaption
                    MarketingTemplatePlatform.FACEBOOK -> "${selectedProduct.title}\n\nPrice: ₹${selectedProduct.price} (Special Direct Offer)\n${selectedProduct.benefits.joinToString("\n• ", prefix = "• ")}\n\nOrder online now: ${selectedProduct.productUrl}"
                    MarketingTemplatePlatform.WHATSAPP -> marketingAdvice.whatsappBroadcastText
                  }
                  clipboardManager.setText(AnnotatedString(contentToCopy))
                  Toast.makeText(context, "Copied ad copy to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("btn_copy_ad_copy")
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy Ad Copy",
                  tint = VedashilaPrimaryGreen,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Copy based on Selected Platform
            val bodyText = when (selectedPlatform) {
              MarketingTemplatePlatform.INSTAGRAM -> marketingAdvice.instagramCaption
              MarketingTemplatePlatform.FACEBOOK -> """
                🌿 Authentic Himalayan Ayurveda — ${selectedProduct.title}
                
                Experience pure holistic wellness crafted under classical AYUSH standards.
                
                ✨ Key Formulation Benefits:
                • ${selectedProduct.benefits.joinToString("\n• ")}
                
                🏷️ Special Direct Price: ₹${selectedProduct.price} (MRP: ₹${selectedProduct.originalPrice} - ${selectedProduct.discountPercent}% OFF)
                📦 Free & Fast Shipping Across India | COD Available
                
                👉 Order directly on vedashila.com:
                ${selectedProduct.productUrl}
              """.trimIndent()
              MarketingTemplatePlatform.WHATSAPP -> marketingAdvice.whatsappBroadcastText
            }

            Surface(
              color = Color(0xFFF7F9F7),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = bodyText,
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Color(0xFF2C3E35),
                  fontSize = 12.5.sp,
                  lineHeight = 18.sp
                ),
                modifier = Modifier.padding(12.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Generated Hashtags Section
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Tag,
                  contentDescription = null,
                  tint = VedashilaGold,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "AI Suggested Hashtags",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.5.sp,
                  color = Color(0xFF14201A)
                )
              }

              IconButton(
                onClick = {
                  val tagsText = marketingAdvice.hashtags.joinToString(" ")
                  clipboardManager.setText(AnnotatedString(tagsText))
                  Toast.makeText(context, "Copied hashtags to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("btn_copy_hashtags")
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Copy Hashtags",
                  tint = VedashilaGold,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Flow of Hashtag Badges
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              marketingAdvice.hashtags.forEach { tag ->
                Surface(
                  color = Color(0xFFF1F5F2),
                  shape = RoundedCornerShape(6.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder)
                ) {
                  Text(
                    text = tag,
                    fontSize = 11.5.sp,
                    color = VedashilaPrimaryGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Studio Forward Action Buttons
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = { onNavigateToPosters(selectedProduct) },
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("btn_studio_open_poster"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = VedashilaGold,
              contentColor = Color(0xFF14201A)
            )
          ) {
            Icon(
              imageVector = Icons.Default.Campaign,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Create Poster", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
          }

          OutlinedButton(
            onClick = { onNavigateToVideos(selectedProduct) },
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("btn_studio_open_video"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = VedashilaPrimaryGreen
            )
          ) {
            Icon(
              imageVector = Icons.Default.Movie,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Create Video", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
          }
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
