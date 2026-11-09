package com.example.model

enum class SocialPlatform(
  val displayName: String,
  val aspectRatioLabel: String,
  val widthRatio: Float,
  val heightRatio: Float,
  val platformTag: String
) {
  INSTAGRAM_POST("Instagram Post", "1:1 Square (1080x1080)", 1f, 1f, "Instagram"),
  INSTAGRAM_STORY("Instagram Story / Reels", "9:16 Vertical (1080x1920)", 9f, 16f, "Instagram"),
  WHATSAPP_STATUS("WhatsApp Status", "9:16 Vertical (1080x1920)", 9f, 16f, "WhatsApp"),
  FACEBOOK_FEED("Facebook Feed", "16:9 Landscape (1200x675)", 16f, 9f, "Facebook"),
  YOUTUBE_AD("YouTube Video Ad", "16:9 Landscape (1920x1080)", 16f, 9f, "YouTube"),
  YOUTUBE_SHORTS("YouTube Shorts", "9:16 Vertical (1080x1920)", 9f, 16f, "YouTube")
}

enum class TemplateThemeType(
  val title: String,
  val description: String,
  val primaryHex: Long,
  val secondaryHex: Long,
  val accentHex: Long,
  val bgGradientHexes: List<Long>
) {
  HIMALAYAN_ROYAL_GOLD(
    title = "Himalayan Royal Gold",
    description = "Luxurious gold leaf with deep forest green background",
    primaryHex = 0xFF0D3B2E,
    secondaryHex = 0xFFC59B27,
    accentHex = 0xFFF5D061,
    bgGradientHexes = listOf(0xFF0D3B2E, 0xFF08261D, 0xFF04120E)
  ),
  BOTANICAL_EMERALD(
    title = "Ayurvedic Botanical",
    description = "Lush botanical greens with fresh mint accents",
    primaryHex = 0xFF14532D,
    secondaryHex = 0xFF22C55E,
    accentHex = 0xFF86EFAC,
    bgGradientHexes = listOf(0xFF14532D, 0xFF0F3A20, 0xFF092012)
  ),
  FESTIVE_SAFFRON(
    title = "Festive Saffron Sale",
    description = "High-energy festival vibes with saffron & amber glow",
    primaryHex = 0xFF7C2D12,
    secondaryHex = 0xFFEA580C,
    accentHex = 0xFFFBBF24,
    bgGradientHexes = listOf(0xFF7C2D12, 0xFF4A1A0A, 0xFF280E05)
  ),
  CLINICAL_TRUST(
    title = "Clinical & AYUSH Verified",
    description = "Clean, authoritative layout highlighting lab purity",
    primaryHex = 0xFF0F172A,
    secondaryHex = 0xFF38BDF8,
    accentHex = 0xFF93C5FD,
    bgGradientHexes = listOf(0xFF0F172A, 0xFF1E293B, 0xFF0B1120)
  ),
  CUSTOMER_REVIEW(
    title = "5-Star Review Spotlight",
    description = "Social proof template with glowing star ratings",
    primaryHex = 0xFF18181B,
    secondaryHex = 0xFFF59E0B,
    accentHex = 0xFFFDE68A,
    bgGradientHexes = listOf(0xFF18181B, 0xFF27272A, 0xFF09090B)
  )
}

enum class BrandLogoStyle(
  val displayName: String,
  val badgeText: String,
  val subtitle: String
) {
  VEDASHILA_SEAL("Vedashila Gold Seal", "VEDASHILA", "AUTHENTIC AYURVEDA"),
  VS_MONOGRAM("VS Monogram", "VS", "EST. 1978"),
  AYUSH_APPROVED("AYUSH Certified", "AYUSH", "MINISTRY VERIFIED"),
  ORGANIC_HERB("100% Himalayan Pure", "100%", "ORGANIC & RAW")
}

data class PosterCustomization(
  val headerTagline: String = "AUTHENTIC HIMALAYAN AYURVEDA",
  val headline: String = "Pure Himalayan Shilajit Resin (50g)",
  val subtext: String = "Harvested at 16,000+ ft • 84+ Minerals & Fulvic Acid",
  val offerBadge: String = "FLAT 45% OFF • LIMITED TIME",
  val displayPrice: String = "₹1,299",
  val displayOriginalPrice: String = "₹2,499",
  val ctaText: String = "Order Now at vedashila.com",
  val whatsappContact: String = "WhatsApp Orders: +91 98765 43210",
  val logoStyle: BrandLogoStyle = BrandLogoStyle.VEDASHILA_SEAL,
  val templateType: TemplateThemeType = TemplateThemeType.HIMALAYAN_ROYAL_GOLD,
  val showTrustBadges: Boolean = true
)

data class VideoScene(
  val sceneNumber: Int,
  val title: String,
  val headline: String,
  val subtext: String,
  val durationSeconds: Int,
  val voiceoverScript: String,
  val badge: String
)

data class VideoAdProject(
  val id: String,
  val title: String,
  val targetPlatform: SocialPlatform,
  val totalDurationSeconds: Int,
  val scenes: List<VideoScene>,
  val logoStyle: BrandLogoStyle = BrandLogoStyle.VEDASHILA_SEAL,
  val backgroundMusic: String = "Himalayan Flute & Sitar Ambient BGM",
  val callToAction: String = "Shop Now at vedashila.com"
)
