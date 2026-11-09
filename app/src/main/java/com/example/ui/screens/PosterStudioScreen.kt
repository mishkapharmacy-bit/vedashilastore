package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.InventoryRepository
import com.example.data.SampleCatalog
import com.example.model.BrandLogoStyle
import com.example.model.PosterCustomization
import com.example.model.Product
import com.example.model.SocialPlatform
import com.example.model.TemplateThemeType
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen

@Composable
fun PosterStudioScreen(
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

  var selectedPlatform by remember { mutableStateOf(SocialPlatform.INSTAGRAM_POST) }
  var customization by remember(selectedProduct) {
    mutableStateOf(
      PosterCustomization(
        headline = selectedProduct.title,
        subtext = selectedProduct.benefits.firstOrNull() ?: "Pure Himalayan Ayurvedic Potency",
        displayPrice = "₹${selectedProduct.price}",
        displayOriginalPrice = "₹${selectedProduct.originalPrice}",
        offerBadge = "FLAT ${selectedProduct.discountPercent}% OFF • LIMITED TIME"
      )
    )
  }

  val isProductMarketable = selectedProduct.isMarketable

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
            text = "Custom Marketing Poster Studio",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
          Text(
            text = "Instagram • Facebook • WhatsApp Status",
            color = VedashilaGold,
            fontSize = 11.sp
          )
        }
        Icon(
          imageVector = Icons.Default.AutoAwesome,
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
      // Out of Stock Real-Time Protection Banner
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
                text = "'${selectedProduct.title}' is currently unavailable on vedashila.com. Marketing export is disabled to prevent advertising zero-stock items.",
                fontSize = 11.sp,
                color = Color(0xFFC62828)
              )
            }
          }
        }
      }

      // 1. Social Media Platform Dimension Preset
      Text(
        text = "1. Select Platform & Canvas Dimensions",
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
        listOf(
          SocialPlatform.INSTAGRAM_POST,
          SocialPlatform.INSTAGRAM_STORY,
          SocialPlatform.WHATSAPP_STATUS,
          SocialPlatform.FACEBOOK_FEED
        ).forEach { platform ->
          FilterChip(
            selected = selectedPlatform == platform,
            onClick = { selectedPlatform = platform },
            label = { Text(platform.displayName) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = VedashilaPrimaryGreen,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      // 2. Poster Template Theme Selector
      Text(
        text = "2. Select Design Template Theme",
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
        SampleCatalog.templateThemes.forEach { theme ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(theme.primaryHex),
            border = androidx.compose.foundation.BorderStroke(
              width = if (customization.templateType == theme) 2.dp else 1.dp,
              color = if (customization.templateType == theme) VedashilaGold else Color.Gray.copy(alpha = 0.3f)
            ),
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { customization = customization.copy(templateType = theme) }
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(theme.secondaryHex))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = theme.title,
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = theme.description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                maxLines = 1
              )
            }
          }
        }
      }

      // 3. Live Canvas Preview
      Text(
        text = "3. Live Poster Preview (${selectedPlatform.aspectRatioLabel})",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = VedashilaPrimaryGreen
      )

      CustomPosterCanvas(
        platform = selectedPlatform,
        product = selectedProduct,
        customization = customization
      )

      // 4. Share & Export Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            if (isProductMarketable) {
              sharePoster(context, selectedPlatform, selectedProduct, customization)
            } else {
              Toast.makeText(context, "Cannot share poster for out-of-stock product.", Toast.LENGTH_SHORT).show()
            }
          },
          enabled = isProductMarketable,
          colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1.2f)
            .height(46.dp)
            .testTag("share_marketing_poster_button")
        ) {
          Icon(
            imageVector = if (isProductMarketable) Icons.Default.Share else Icons.Default.Block,
            contentDescription = null,
            tint = VedashilaGold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            if (isProductMarketable) "Share to ${selectedPlatform.platformTag}" else "Marketing Disabled",
            fontWeight = FontWeight.Bold
          )
        }

        OutlinedButton(
          onClick = {
            val caption = buildCaption(selectedProduct, customization)
            clipboardManager.setText(AnnotatedString(caption))
            Toast.makeText(context, "Marketing copy & hashtags copied!", Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = VedashilaPrimaryGreen),
          modifier = Modifier
            .weight(1f)
            .height(46.dp)
            .testTag("copy_marketing_caption_button")
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Copy Caption", fontWeight = FontWeight.SemiBold)
        }
      }

      // 5. Input Customization Form
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
            text = "Customize Overlays, Logo & Content",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = VedashilaPrimaryGreen
          )

          // Brand Logo Style Selector
          Text(
            text = "Brand Logo / Seal Style:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            SampleCatalog.brandLogos.forEach { logo ->
              FilterChip(
                selected = customization.logoStyle == logo,
                onClick = { customization = customization.copy(logoStyle = logo) },
                label = { Text(logo.displayName, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = VedashilaGold,
                  selectedLabelColor = Color(0xFF14201A)
                )
              )
            }
          }

          // Featured Product Selector
          Text(
            text = "Select Product to Feature:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
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
                  .clickable { selectedProduct = prod }
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

          // Editable Headline
          OutlinedTextField(
            value = customization.headline,
            onValueChange = { customization = customization.copy(headline = it) },
            label = { Text("Product Headline Overlay") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
          )

          // Editable Subtext / Benefit
          OutlinedTextField(
            value = customization.subtext,
            onValueChange = { customization = customization.copy(subtext = it) },
            label = { Text("Secondary Subtext / Herb Benefits") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
          )

          // Editable Offer Badge
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = customization.offerBadge,
              onValueChange = { customization = customization.copy(offerBadge = it) },
              label = { Text("Discount Badge") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = customization.displayPrice,
              onValueChange = { customization = customization.copy(displayPrice = it) },
              label = { Text("Price Display") },
              modifier = Modifier.weight(1f)
            )
          }

          // Editable Call to Action & WhatsApp Number
          OutlinedTextField(
            value = customization.ctaText,
            onValueChange = { customization = customization.copy(ctaText = it) },
            label = { Text("Call to Action Link") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = customization.whatsappContact,
            onValueChange = { customization = customization.copy(whatsappContact = it) },
            label = { Text("WhatsApp Contact Overlay") },
            modifier = Modifier.fillMaxWidth()
          )

          // Trust Badges Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Show AYUSH & Purity Trust Badges",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Switch(
              checked = customization.showTrustBadges,
              onCheckedChange = { customization = customization.copy(showTrustBadges = it) },
              colors = SwitchDefaults.colors(checkedThumbColor = VedashilaGold, checkedTrackColor = VedashilaPrimaryGreen)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun CustomPosterCanvas(
  platform: SocialPlatform,
  product: Product,
  customization: PosterCustomization
) {
  val theme = customization.templateType
  val ratio = when (platform) {
    SocialPlatform.INSTAGRAM_POST -> 1f / 1f
    SocialPlatform.INSTAGRAM_STORY, SocialPlatform.WHATSAPP_STATUS -> 9f / 15f
    SocialPlatform.FACEBOOK_FEED, SocialPlatform.YOUTUBE_AD -> 16f / 10f
    SocialPlatform.YOUTUBE_SHORTS -> 9f / 15f
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(ratio)
      .clip(RoundedCornerShape(16.dp))
      .shadow(8.dp, RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          colors = theme.bgGradientHexes.map { Color(it) }
        )
      )
      .border(2.dp, Color(theme.secondaryHex), RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Header: Logo Style + Offer Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo Style Render
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(Color(theme.secondaryHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = customization.logoStyle.badgeText.take(2),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF14201A)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = customization.logoStyle.badgeText,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp
            )
            Text(
              text = customization.logoStyle.subtitle,
              color = Color(theme.accentHex),
              fontSize = 8.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Promotional Offer Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(theme.secondaryHex)
        ) {
          Text(
            text = customization.offerBadge,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14201A),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Middle: Product Image & Overlays
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1.1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(theme.secondaryHex).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
          if (product.localDrawableRes != null) {
            Image(
              painter = painterResource(id = product.localDrawableRes),
              contentDescription = product.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        Column(modifier = Modifier.weight(1.2f)) {
          Text(
            text = customization.headerTagline,
            color = Color(theme.accentHex),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = customization.headline,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 17.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = customization.subtext,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp,
            lineHeight = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Price Display
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = customization.displayPrice,
              color = Color(theme.accentHex),
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = customization.displayOriginalPrice,
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 11.sp,
              textDecoration = TextDecoration.LineThrough
            )
          }

          if (customization.showTrustBadges) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color(theme.secondaryHex), modifier = Modifier.size(10.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("AYUSH & GMP Verified", color = Color(theme.secondaryHex), fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }

      // Bottom Call to Action and Contact Watermark
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(theme.secondaryHex),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = customization.ctaText,
            color = Color(0xFF14201A),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp)
          )
        }

        Text(
          text = customization.whatsappContact,
          color = Color.White.copy(alpha = 0.75f),
          fontSize = 8.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

private fun buildCaption(product: Product, c: PosterCustomization): String {
  return """
    🌿 ${c.headline}
    
    ${c.subtext}
    
    💰 Special Price: ${c.displayPrice} (${c.offerBadge})
    
    ✅ 100% Pure Himalayan Sourced
    ✅ AYUSH Approved & GMP Certified
    ✅ Free Pan-India Delivery & Cash on Delivery Available
    
    🛒 Order online at: ${product.productUrl}
    📲 ${c.whatsappContact}
    
    #Vedashila #Ayurveda #Shilajit #PureHerbs #NaturalHealth #Immunity #WellnessIndia #VedicHealing
  """.trimIndent()
}

private fun sharePoster(
  context: Context,
  platform: SocialPlatform,
  product: Product,
  c: PosterCustomization
) {
  val caption = buildCaption(product, c)
  val intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, caption)
    type = "text/plain"
  }
  context.startActivity(Intent.createChooser(intent, "Share ${platform.displayName} to:"))
}
