package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.InventoryRepository
import com.example.data.SampleCatalog
import com.example.model.BrandLogoStyle
import com.example.model.Product
import com.example.model.SocialPlatform
import com.example.model.VideoAdProject
import com.example.model.VideoScene
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen
import kotlinx.coroutines.delay

enum class VideoAdPreset(
  val title: String,
  val subtitle: String,
  val totalDurationSeconds: Int,
  val scenesCount: Int
) {
  YOUTUBE_15S_BUMPER("YouTube 15s Bumper Ad", "High-impact unskippable brand hook", 15, 3),
  YOUTUBE_30S_CONVERSION("YouTube 30s Conversion Ad", "5-scene full narrative & trust proof", 30, 5),
  SHORTS_916_VIRAL("YouTube Shorts / Reels (9:16)", "Vertical viral hook & problem-solution", 20, 4),
  WHATSAPP_STATUS_VIDEO("WhatsApp Video Status", "Fast deal offer with direct link", 15, 3)
}

@Composable
fun VideoStudioScreen(
  initialProduct: Product? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val liveProducts by InventoryRepository.products.collectAsStateWithLifecycle()

  var selectedProduct by remember(initialProduct, liveProducts) {
    val initialId = initialProduct?.id ?: liveProducts.first().id
    mutableStateOf(liveProducts.find { it.id == initialId } ?: liveProducts.first())
  }

  var selectedPreset by remember { mutableStateOf(VideoAdPreset.YOUTUBE_30S_CONVERSION) }
  var selectedPlatform by remember { mutableStateOf(SocialPlatform.YOUTUBE_AD) }
  var selectedLogoStyle by remember { mutableStateOf(BrandLogoStyle.VEDASHILA_SEAL) }
  var selectedBgm by remember { mutableStateOf("Himalayan Sitar & Flute Ambient BGM") }

  var isPlaying by remember { mutableStateOf(true) }
  var currentSceneIndex by remember { mutableIntStateOf(0) }
  var isExporting by remember { mutableStateOf(false) }
  var exportProgress by remember { mutableIntStateOf(0) }

  val isProductMarketable = selectedProduct.isMarketable

  // Video Scenes customized based on selected preset & product
  var scenes by remember(selectedProduct, selectedPreset) {
    mutableStateOf(buildScenesForPreset(selectedPreset, selectedProduct))
  }

  // Playback timer
  LaunchedEffect(isPlaying, currentSceneIndex, scenes) {
    if (isPlaying && scenes.isNotEmpty()) {
      val activeScene = scenes.getOrNull(currentSceneIndex)
      val durationMs = (activeScene?.durationSeconds ?: 5) * 1000L
      delay(durationMs)
      if (currentSceneIndex < scenes.size - 1) {
        currentSceneIndex++
      } else {
        currentSceneIndex = 0
      }
    }
  }

  // Export render simulation
  LaunchedEffect(isExporting) {
    if (isExporting) {
      exportProgress = 0
      for (i in 1..10) {
        delay(200)
        exportProgress = i * 10
      }
      isExporting = false
      Toast.makeText(context, "Video ad successfully rendered in 1080p 60fps for ${selectedPlatform.displayName}!", Toast.LENGTH_LONG).show()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Bar
    Surface(
      color = VedashilaPrimaryGreen,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "Video & YouTube Ad Creator",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
          Text(
            text = "YouTube Ads • Shorts • Reels • WhatsApp Video",
            color = VedashilaGold,
            fontSize = 11.sp
          )
        }
        Icon(
          imageVector = Icons.Default.SlowMotionVideo,
          contentDescription = null,
          tint = VedashilaGold,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Out of Stock Real-Time Alert
      if (!isProductMarketable) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = null,
              tint = Color(0xFFC62828),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Out-of-Stock Protection Active",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFB71C1C)
              )
              Text(
                text = "'${selectedProduct.title}' is out of stock on vedashila.com. Video ad export is disabled until stock is replenished.",
                fontSize = 11.sp,
                color = Color(0xFFC62828)
              )
            }
          }
        }
      }

      // 1. Video Ad Template Preset
      Text(
        text = "1. Select Video Ad Template Preset",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = VedashilaPrimaryGreen
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        VideoAdPreset.values().forEach { preset ->
          val isSelected = selectedPreset == preset
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) VedashilaPrimaryGreen else Color.White,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) VedashilaGold else VedashilaBorder
            ),
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                selectedPreset = preset
                selectedPlatform = when (preset) {
                  VideoAdPreset.YOUTUBE_15S_BUMPER, VideoAdPreset.YOUTUBE_30S_CONVERSION -> SocialPlatform.YOUTUBE_AD
                  VideoAdPreset.SHORTS_916_VIRAL -> SocialPlatform.YOUTUBE_SHORTS
                  VideoAdPreset.WHATSAPP_STATUS_VIDEO -> SocialPlatform.WHATSAPP_STATUS
                }
                scenes = buildScenesForPreset(preset, selectedProduct)
                currentSceneIndex = 0
              }
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = preset.title,
                color = if (isSelected) Color.White else Color(0xFF14201A),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
              Text(
                text = "${preset.totalDurationSeconds}s • ${preset.scenesCount} Scenes",
                color = if (isSelected) VedashilaGold else Color.Gray,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // 2. Interactive Video Storyboard Player
      val activeScene = scenes.getOrElse(currentSceneIndex) { scenes.first() }

      Text(
        text = "2. Dynamic Video Storyboard Player",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = VedashilaPrimaryGreen
      )

      VideoCanvasPreview(
        platform = selectedPlatform,
        product = selectedProduct,
        scene = activeScene,
        logoStyle = selectedLogoStyle,
        isPlaying = isPlaying,
        currentSceneIndex = currentSceneIndex,
        totalScenes = scenes.size
      )

      // Player Controls
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, VedashilaBorder, RoundedCornerShape(12.dp))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(onClick = { isPlaying = !isPlaying }) {
                Icon(
                  imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = "Play/Pause",
                  tint = VedashilaPrimaryGreen
                )
              }
              IconButton(onClick = {
                currentSceneIndex = 0
                isPlaying = true
              }) {
                Icon(imageVector = Icons.Default.Replay, contentDescription = "Replay", tint = Color.Gray)
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Scene ${currentSceneIndex + 1}/${scenes.size}: ${activeScene.title}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = VedashilaPrimaryGreen
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = VedashilaGold, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Audio Synced", fontSize = 11.sp, color = Color(0xFF5D6B63))
            }
          }

          // Scene Step Indicator Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            scenes.forEachIndexed { idx, sc ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(
                    if (idx == currentSceneIndex) VedashilaGold
                    else if (idx < currentSceneIndex) VedashilaPrimaryGreen
                    else Color(0xFFD6E2DB)
                  )
                  .clickable { currentSceneIndex = idx }
              )
            }
          }
        }
      }

      // 3. Export & Share Video Ad
      if (isExporting) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F4EE), RoundedCornerShape(10.dp))
            .padding(12.dp)
        ) {
          Text(
            text = "Rendering Video Ad in Full 1080p... $exportProgress%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = VedashilaPrimaryGreen
          )
          Spacer(modifier = Modifier.height(6.dp))
          LinearProgressIndicator(
            progress = { exportProgress / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = VedashilaGold
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            if (isProductMarketable) {
              isExporting = true
            } else {
              Toast.makeText(context, "Cannot export video ad for out-of-stock product.", Toast.LENGTH_SHORT).show()
            }
          },
          enabled = isProductMarketable,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB34A26)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1.2f)
            .height(46.dp)
            .testTag("render_video_ad_button")
        ) {
          Icon(
            imageVector = if (isProductMarketable) Icons.Default.Movie else Icons.Default.Block,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (isProductMarketable) "Render Video Ad" else "Disabled (No Stock)", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = {
            shareVideoScript(context, selectedPlatform, selectedProduct, scenes)
          },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = VedashilaPrimaryGreen),
          modifier = Modifier
            .weight(1f)
            .height(46.dp)
            .testTag("share_video_ad_script_button")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Share Script", fontWeight = FontWeight.SemiBold)
        }
      }

      // 4. Customization Inputs: Brand Logo, Product, Scene Overlays
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, VedashilaBorder, RoundedCornerShape(16.dp))
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "Video Ad Customization & Overlays",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = VedashilaPrimaryGreen
          )

          // Brand Logo Selector
          Text(text = "Brand Watermark Overlay:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            SampleCatalog.brandLogos.forEach { logo ->
              FilterChip(
                selected = selectedLogoStyle == logo,
                onClick = { selectedLogoStyle = logo },
                label = { Text(logo.displayName, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = VedashilaGold,
                  selectedLabelColor = Color(0xFF14201A)
                )
              )
            }
          }

          // Featured Product Selector
          Text(text = "Product Featured in Video:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            liveProducts.forEach { prod ->
              val isSelected = selectedProduct.id == prod.id
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = when {
                  isSelected -> VedashilaPrimaryGreen
                  !prod.inStock -> Color(0xFFFFEBEE)
                  else -> Color(0xFFF1F6F3)
                },
                contentColor = if (isSelected) Color.White else Color(0xFF14201A),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    selectedProduct = prod
                    scenes = buildScenesForPreset(selectedPreset, prod)
                  }
              ) {
                Text(
                  text = "${if (!prod.inStock) "❌ " else ""}${prod.title.take(20)}...",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
              }
            }
          }

          // Active Scene Editor
          Text(
            text = "Edit Active Scene (${currentSceneIndex + 1} of ${scenes.size}):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = VedashilaPrimaryGreen
          )

          OutlinedTextField(
            value = activeScene.headline,
            onValueChange = { newHead ->
              val updated = scenes.toMutableList()
              updated[currentSceneIndex] = activeScene.copy(headline = newHead)
              scenes = updated
            },
            label = { Text("Scene Headline Overlay") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = activeScene.subtext,
            onValueChange = { newSub ->
              val updated = scenes.toMutableList()
              updated[currentSceneIndex] = activeScene.copy(subtext = newSub)
              scenes = updated
            },
            label = { Text("Scene Subtext Overlay") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = activeScene.voiceoverScript,
            onValueChange = { newVoice ->
              val updated = scenes.toMutableList()
              updated[currentSceneIndex] = activeScene.copy(voiceoverScript = newVoice)
              scenes = updated
            },
            label = { Text("Voiceover Narration Script") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun VideoCanvasPreview(
  platform: SocialPlatform,
  product: Product,
  scene: VideoScene,
  logoStyle: BrandLogoStyle,
  isPlaying: Boolean,
  currentSceneIndex: Int,
  totalScenes: Int
) {
  val isLandscape = platform == SocialPlatform.YOUTUBE_AD
  val aspectRatio = if (isLandscape) 16f / 9f else 9f / 14f

  val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
  val zoomScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isPlaying) 1.08f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "zoom"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(aspectRatio)
      .clip(RoundedCornerShape(16.dp))
      .shadow(8.dp, RoundedCornerShape(16.dp))
      .background(Color(0xFF07120D))
      .border(2.dp, VedashilaGold, RoundedCornerShape(16.dp))
  ) {
    if (product.localDrawableRes != null) {
      Image(
        painter = painterResource(id = product.localDrawableRes),
        contentDescription = product.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxSize()
          .scale(zoomScale)
      )
    }

    // Cinematic vignette
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.55f),
              Color.Black.copy(alpha = 0.2f),
              Color.Black.copy(alpha = 0.88f)
            )
          )
        )
    )

    AnimatedContent(
      targetState = scene,
      transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(350)) },
      label = "scene_anim",
      modifier = Modifier.fillMaxSize()
    ) { currentScene ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Brand Logo Overlay
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaGold)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (isPlaying) Color.Red else Color.Gray)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = logoStyle.badgeText,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = VedashilaGold
          ) {
            Text(
              text = currentScene.badge,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF14201A),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        // Bottom Scene Overlays & Call to Action
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
            .padding(10.dp)
        ) {
          Text(
            text = currentScene.headline,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (isLandscape) 15.sp else 13.sp,
            lineHeight = 19.sp
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = currentScene.subtext,
            color = VedashilaGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFB34A26),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Shop Online at vedashila.com | Free Pan-India Shipping",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

private fun buildScenesForPreset(preset: VideoAdPreset, product: Product): List<VideoScene> {
  return when (preset) {
    VideoAdPreset.YOUTUBE_15S_BUMPER -> listOf(
      VideoScene(1, "Hook", "Unleash Pure Himalayan Stamina", "Ancient Ayurvedic potency certified by AYUSH.", 5, "Are you ready to unlock boundless energy with authentic Himalayan herbs?", "⚡ 15s BUMPER"),
      VideoScene(2, "Product", product.title, "₹${product.price} (${product.discountPercent}% OFF) • 100% Raw & Natural.", 5, "Discover ${product.title}, tested for heavy metal purity.", "⭐ BEST SELLER"),
      VideoScene(3, "CTA", "Order Now at vedashila.com", "Free Pan-India Delivery & Cash on Delivery Available.", 5, "Order today at vedashila.com before stock runs out!", "🛒 SHOP NOW")
    )
    VideoAdPreset.YOUTUBE_30S_CONVERSION -> listOf(
      VideoScene(1, "Hook", "Struggling With Low Energy Daily?", "Modern stress depletes vital Ojas and strength.", 6, "Feeling constantly tired and drained? Ancient Ayurveda holds the real answer.", "⚠️ LOW ENERGY ALERT"),
      VideoScene(2, "Solution", "Vedashila Pure Himalayan Potency", "Harvested from 16,000+ ft altitude in the Himalayas.", 6, "Vedashila brings you authentic, unadulterated Himalayan Shilajit & herbs.", "🏔️ 16,000 FT ALTITUDE"),
      VideoScene(3, "Potency", "84+ Minerals & Fulvic Acid", "Laboratory certified for biological absorption & safety.", 6, "Enriched with 84 minerals and over 75% Fulvic Acid for cellular vitality.", "🔬 LAB TESTED PURITY"),
      VideoScene(4, "Trust", "50,000+ Verified Indian Customers", "Rated 4.9/5 stars across India for rapid strength.", 6, "Trusted by over 50,000 wellness enthusiasts across India.", "⭐ 4.9/5 RATED"),
      VideoScene(5, "CTA", "Order Today at vedashila.com", "Use code VEDA20 for special savings • COD Available.", 6, "Order today at vedashila.com. Tap the link to shop now!", "🛒 ORDER NOW")
    )
    VideoAdPreset.SHORTS_916_VIRAL -> listOf(
      VideoScene(1, "Viral Hook", "Stop Buying Fake Shilajit!", "Most market resins fail purity and heavy metal tests.", 5, "Stop buying adulterated resins! Here is how to spot genuine Shilajit.", "🔥 VIRAL SHORT"),
      VideoScene(2, "Test & Proof", "The Vedashila Water Dissolve Test", "Leaves zero sediment and glows with pure amber hue.", 5, "Watch Vedashila dissolve completely in warm water without any residue.", "💧 PURITY TEST"),
      VideoScene(3, "Benefits", "Strength • Stamina • Deep Sleep", "Formulated according to classical Charaka Samhita.", 5, "Feel the immediate surge in natural vitality and morning focus.", "✨ REAL RESULTS"),
      VideoScene(4, "Link In Bio", "Tap Link to Buy: vedashila.com", "Limited stock available from the fresh Himalayan batch.", 5, "Click the link in bio or visit vedashila.com to secure your jar now!", "👇 LINK BELOW")
    )
    VideoAdPreset.WHATSAPP_STATUS_VIDEO -> listOf(
      VideoScene(1, "Exclusive Deal", "Exclusive WhatsApp Offer!", "Special discount on Vedashila Himalayan Wellness.", 5, "Special member deal for our WhatsApp community!", "💬 WHATSAPP ONLY"),
      VideoScene(2, "Product Spotlight", product.title, "Special Price: ₹${product.price} (Save ${product.discountPercent}%)", 5, "Get ${product.title} at an unbeatable member discount today.", "🎁 SAVE BIG"),
      VideoScene(3, "Reply To Order", "Reply to this Status to Order", "Direct delivery to your doorstep • Free Shipping.", 5, "Simply reply to this WhatsApp message to place your order with free delivery!", "🚚 FREE SHIPPING")
    )
  }
}

private fun shareVideoScript(
  context: Context,
  platform: SocialPlatform,
  product: Product,
  scenes: List<VideoScene>
) {
  val script = """
    🎬 Vedashila Video Ad Script (${platform.displayName})
    Product: ${product.title}
    Price: ₹${product.price}
    URL: ${product.productUrl}
    
    ${scenes.joinToString("\n\n") { "▶ Scene ${it.sceneNumber} (${it.durationSeconds}s) [${it.title}]\nHeadline: ${it.headline}\nVoiceover: \"${it.voiceoverScript}\"" }}
    
    Target: YouTube Ads, Instagram Reels, WhatsApp Video Status
  """.trimIndent()

  val intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, script)
    type = "text/plain"
  }
  context.startActivity(Intent.createChooser(intent, "Share Video Ad Blueprint to:"))
}
