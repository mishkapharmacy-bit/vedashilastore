package com.example.data

import com.example.model.BrandLogoStyle
import com.example.model.InventoryStatus
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.SocialPlatform
import com.example.model.TemplateThemeType
import com.example.model.VideoAdProject
import com.example.model.VideoScene

object SampleCatalog {

  val categories: List<ProductCategory>
    get() = InventoryRepository.categories

  val products: List<Product>
    get() = InventoryRepository.products.value

  val templateThemes = TemplateThemeType.values().toList()
  val posterThemes = templateThemes

  val brandLogos = BrandLogoStyle.values().toList()

  val sampleVideoAdProject = VideoAdProject(
    id = "vid_shilajit_ad_01",
    title = "Pure Himalayan Shilajit - YouTube & Reel Ad",
    targetPlatform = SocialPlatform.YOUTUBE_AD,
    totalDurationSeconds = 25,
    logoStyle = BrandLogoStyle.VEDASHILA_SEAL,
    scenes = listOf(
      VideoScene(
        sceneNumber = 1,
        title = "Hook (Problem)",
        headline = "Feeling Constantly Tired & Drained?",
        subtext = "Modern stress depletes vital Ojas and natural bodily energy.",
        durationSeconds = 5,
        voiceoverScript = "Are you tired of feeling sluggish and fatigued every single day? Discover the ancient Himalayan secret of boundless energy.",
        badge = "⚠️ LOW ENERGY ALERT"
      ),
      VideoScene(
        sceneNumber = 2,
        title = "Solution (Vedashila)",
        headline = "Vedashila Pure Himalayan Shilajit",
        subtext = "Harvested from 16,000+ ft altitude in the pristine Himalayas.",
        durationSeconds = 6,
        voiceoverScript = "Introducing Vedashila 100% Pure Himalayan Shilajit Resin. Sourced directly from pristine 16,000-foot Himalayan peaks.",
        badge = "🏔️ 16,000 FT ALTITUDE"
      ),
      VideoScene(
        sceneNumber = 3,
        title = "Potency & Minerals",
        headline = "84+ Minerals & 75%+ Fulvic Acid",
        subtext = "Ayurvedic potency certified by AYUSH and third-party laboratory tests.",
        durationSeconds = 5,
        voiceoverScript = "Packed with over 84 ionic minerals and 75% Fulvic Acid, it revitalizes your cells and strengthens immunity naturally.",
        badge = "🔬 LAB TESTED PURITY"
      ),
      VideoScene(
        sceneNumber = 4,
        title = "Customer Trust",
        headline = "50,000+ Satisfied Indian Customers",
        subtext = "Rated 4.9/5 stars for authentic potency and rapid vitality results.",
        durationSeconds = 4,
        voiceoverScript = "Over 50,000 satisfied wellness seekers trust Vedashila for genuine, unadulterated Ayurvedic power.",
        badge = "⭐ 4.9/5 RATED"
      ),
      VideoScene(
        sceneNumber = 5,
        title = "Call To Action",
        headline = "Order Today at vedashila.com",
        subtext = "Free Pan-India Delivery • Cash on Delivery • 100% Money Back Guarantee",
        durationSeconds = 5,
        voiceoverScript = "Order today at vedashila.com. Use code WELLNESS20 for 20% off. Free Pan-India Delivery and Cash on Delivery available!",
        badge = "🛒 SHOP NOW"
      )
    )
  )

  val currentInventoryStatus: InventoryStatus
    get() = InventoryRepository.inventoryStatus.value
}
