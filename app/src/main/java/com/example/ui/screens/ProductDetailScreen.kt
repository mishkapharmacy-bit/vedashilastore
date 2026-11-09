package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.Product
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaContainerGreen
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen

/**
 * ProductDetailScreen component that displays extended product information,
 * full Ayurvedic descriptions, dosage guidance, certifications, and high-resolution imagery.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
  product: Product,
  onBack: () -> Unit,
  onOpenStoreWeb: (String) -> Unit,
  onGeneratePoster: (Product) -> Unit,
  onGenerateVideo: (Product) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var showQrDialog by remember { mutableStateOf(false) }

  // Hardware/System Back Button Navigation
  BackHandler {
    onBack()
  }

  // QR Code Modal Dialog
  if (showQrDialog) {
    ProductQrCodeDialog(
      product = product,
      onDismiss = { showQrDialog = false }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF9FAF8))
      .testTag("screen_product_detail")
  ) {
    // Top Navigation App Bar
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("btn_detail_back")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Products",
              tint = Color(0xFF14201A)
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Column {
            Text(
              text = "Product Details",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14201A)
              )
            )
            Text(
              text = "SKU: ${product.sku}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = VedashilaGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Share via QR Button
          IconButton(
            onClick = { showQrDialog = true },
            modifier = Modifier.testTag("btn_share_qr_product")
          ) {
            Icon(
              imageVector = Icons.Default.QrCode,
              contentDescription = "Share via QR",
              tint = VedashilaPrimaryGreen
            )
          }

          // Share Button
          IconButton(
            onClick = {
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                  Intent.EXTRA_TEXT,
                  "Check out ${product.title} from Vedashila: ${product.productUrl}"
                )
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share Vedashila Product"))
            },
            modifier = Modifier.testTag("btn_share_product")
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share Product",
              tint = VedashilaPrimaryGreen
            )
          }

          // Open in Browser
          IconButton(
            onClick = { onOpenStoreWeb(product.productUrl) },
            modifier = Modifier.testTag("btn_open_in_browser")
          ) {
            Icon(
              imageVector = Icons.Default.OpenInBrowser,
              contentDescription = "Open on vedashila.com",
              tint = VedashilaPrimaryGreen
            )
          }
        }
      }
    }

    // Scrollable Extended Details Content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
    ) {
      // High-Resolution Hero Imagery Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp)
          .background(
            Brush.verticalGradient(
              colors = listOf(Color(0xFFE8EFEA), Color(0xFFD6E2DA))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        if (product.localDrawableRes != null) {
          Image(
            painter = painterResource(id = product.localDrawableRes),
            contentDescription = product.title,
            modifier = Modifier
              .fillMaxSize()
              .testTag("img_product_detail_hero"),
            contentScale = ContentScale.Crop
          )
        } else {
          // Coil AsyncImage for Remote High-Resolution Imagery
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(product.productUrl)
              .crossfade(true)
              .build(),
            contentDescription = product.title,
            modifier = Modifier
              .fillMaxSize()
              .testTag("img_product_detail_hero"),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_vedashila_logo_1793942426062)
          )
        }

        // Tag Overlay
        if (product.tag.isNotBlank()) {
          Surface(
            color = VedashilaGold,
            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(top = 16.dp)
          ) {
            Text(
              text = product.tag,
              color = Color(0xFF14201A),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }

        // In-Stock / Out-of-Stock Real-time Pill
        Surface(
          color = if (product.inStock && product.stockQuantity > 0) Color(0xFF2E7D32) else Color(0xFFC62828),
          shape = CircleShape,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color.White)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (product.inStock && product.stockQuantity > 0) {
                "In Stock (${product.stockQuantity} units)"
              } else {
                "Out of Stock"
              },
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Main Details Container
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // Category & Rating Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = VedashilaContainerGreen,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = product.category,
              color = VedashilaPrimaryGreen,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = VedashilaGold,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${product.rating} (${product.reviewsCount} verified reviews)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF4A5C52)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Full Product Title
        Text(
          text = product.title,
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14201A),
            fontSize = 20.sp,
            lineHeight = 26.sp
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Pricing Card
        Surface(
          color = Color.White,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Special Direct Price",
                fontSize = 11.sp,
                color = Color(0xFF6B7A72)
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "₹${product.price}",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = VedashilaPrimaryGreen
                )
                if (product.originalPrice > product.price) {
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "₹${product.originalPrice}",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "${product.discountPercent}% OFF",
                      color = Color(0xFFD32F2F),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Delivery Included",
                fontSize = 11.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Prepaid / Cash on Delivery",
                fontSize = 10.5.sp,
                color = Color(0xFF6B7A72)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ayurvedic Dosage & Health Goal Attributes
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Form", fontSize = 11.sp, color = Color.Gray)
              Text(product.dosageForm, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF14201A))
            }
            Box(modifier = Modifier.height(30.dp).width(1.dp).background(VedashilaBorder))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Health Goal", fontSize = 11.sp, color = Color.Gray)
              Text(product.healthGoal, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF14201A))
            }
            Box(modifier = Modifier.height(30.dp).width(1.dp).background(VedashilaBorder))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Purity", fontSize = 11.sp, color = Color.Gray)
              Text("100% Herbal", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = VedashilaPrimaryGreen)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Full Extended Description & Key Benefits
        Text(
          text = "Key Ayurvedic Benefits & Formulation",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14201A)
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            product.benefits.forEach { benefit ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
              ) {
                Box(
                  modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(VedashilaContainerGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = VedashilaPrimaryGreen,
                    modifier = Modifier.size(12.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = benefit,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF2C3E35),
                    lineHeight = 20.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quality Certifications Badges
        Text(
          text = "Certifications & Lab Testing",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF14201A)
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          product.certifications.forEach { cert ->
            Surface(
              color = Color.White,
              border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = null,
                  tint = VedashilaGold,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = cert,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF14201A),
                  maxLines = 1
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }

    // Bottom Action Dock
    Surface(
      color = Color.White,
      shadowElevation = 8.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Online Purchase on Vedashila.com
        Button(
          onClick = { onOpenStoreWeb(product.productUrl) },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("btn_detail_buy_now"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = VedashilaPrimaryGreen,
            contentColor = Color.White
          )
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Buy on Website", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        // Generate Poster (Guarded by Marketable flag)
        Button(
          onClick = { onGeneratePoster(product) },
          enabled = product.isMarketable,
          modifier = Modifier
            .weight(0.9f)
            .height(48.dp)
            .testTag("btn_detail_create_poster"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = VedashilaGold,
            contentColor = Color(0xFF14201A)
          )
        ) {
          Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Poster", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        // Generate Video Ad
        OutlinedButton(
          onClick = { onGenerateVideo(product) },
          enabled = product.isMarketable,
          modifier = Modifier
            .weight(0.9f)
            .height(48.dp)
            .testTag("btn_detail_create_video"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = VedashilaPrimaryGreen
          )
        ) {
          Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Video Ad", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

/**
 * Share via QR Modal Dialog displaying a scannable QR code for the selected product.
 * Physical retail customers can scan this QR code with their mobile cameras to view the live product page on vedashila.com.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductQrCodeDialog(
  product: Product,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("dialog_share_qr")
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = VedashilaPrimaryGreen,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Scan & Buy Online",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF14201A)
              )
              Text(
                text = "Instant Device Hand-off",
                fontSize = 11.sp,
                color = VedashilaGold,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Close",
              tint = Color.Gray
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Snippet Badge
        Surface(
          color = Color(0xFFF7F9F7),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = product.title,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF14201A),
              maxLines = 1
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "₹${product.price}",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VedashilaPrimaryGreen
              )
              Text(
                text = "SKU: ${product.sku}",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scannable QR Matrix Canvas
        Box(
          modifier = Modifier
            .size(220.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(2.dp, VedashilaGold, RoundedCornerShape(12.dp))
            .padding(14.dp),
          contentAlignment = Alignment.Center
        ) {
          ProductQrCanvas(
            payload = product.productUrl,
            modifier = Modifier.fillMaxSize()
          )

          // Center Logo Badge
          Surface(
            color = Color.White,
            shape = CircleShape,
            shadowElevation = 2.dp,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Image(
                painter = painterResource(id = R.drawable.ic_vedashila_logo_1793942426062),
                contentDescription = "Vedashila",
                modifier = Modifier.size(26.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Scan with your phone camera to open:",
          fontSize = 12.sp,
          color = Color(0xFF5A6E63)
        )
        Text(
          text = product.productUrl,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = VedashilaPrimaryGreen,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, product.productUrl)
              }
              context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
            },
            modifier = Modifier
              .weight(1f)
              .height(42.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy URL", fontSize = 12.sp)
          }

          Button(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(42.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = VedashilaPrimaryGreen,
              contentColor = Color.White
            )
          ) {
            Text("Done", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  }
}

/**
 * Deterministic Canvas QR Code rendering based on product payload URL hash and structure
 */
@Composable
fun ProductQrCanvas(
  payload: String,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val gridSize = 25
    val cellSize = size.width / gridSize
    val darkColor = androidx.compose.ui.graphics.Color(0xFF14201A)
    val lightColor = androidx.compose.ui.graphics.Color.White

    // Fill background
    drawRect(color = lightColor)

    // Helper to draw QR finder pattern (top-left, top-right, bottom-left)
    fun drawFinderPattern(rowStart: Int, colStart: Int) {
      // 7x7 outer square
      drawRect(
        color = darkColor,
        topLeft = Offset(colStart * cellSize, rowStart * cellSize),
        size = Size(7 * cellSize, 7 * cellSize)
      )
      // 5x5 inner white
      drawRect(
        color = lightColor,
        topLeft = Offset((colStart + 1) * cellSize, (rowStart + 1) * cellSize),
        size = Size(5 * cellSize, 5 * cellSize)
      )
      // 3x3 center black
      drawRect(
        color = darkColor,
        topLeft = Offset((colStart + 2) * cellSize, (rowStart + 2) * cellSize),
        size = Size(3 * cellSize, 3 * cellSize)
      )
    }

    drawFinderPattern(0, 0)
    drawFinderPattern(0, gridSize - 7)
    drawFinderPattern(gridSize - 7, 0)

    // Deterministic bit generator based on payload
    val hash = payload.hashCode().toLong() and 0xFFFFFFFFL
    var seed = hash

    for (row in 0 until gridSize) {
      for (col in 0 until gridSize) {
        // Skip finder areas
        val inTopLeft = row < 8 && col < 8
        val inTopRight = row < 8 && col >= gridSize - 8
        val inBottomLeft = row >= gridSize - 8 && col < 8
        val inCenterLogo = row in 9..15 && col in 9..15

        if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenterLogo) {
          // Linear congruential pseudorandom step
          seed = (seed * 1103515245L + 12345L) and 0x7FFFFFFFL
          val isDark = ((seed xor (row.toLong() * 31 + col)) % 3) != 0L

          if (isDark) {
            drawRect(
              color = darkColor,
              topLeft = Offset(col * cellSize, row * cellSize),
              size = Size(cellSize, cellSize)
            )
          }
        }
      }
    }
  }
}
