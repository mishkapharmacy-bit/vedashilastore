package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StorefrontWebView
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen

@Composable
fun StorefrontScreen(
  targetUrl: String? = null,
  onOpenCategories: () -> Unit = {},
  onOpenAdmin: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var currentUrl by remember { mutableStateOf(targetUrl ?: "https://vedashila.com") }
  var pageTitle by remember { mutableStateOf("Vedashila Official Store") }
  var refreshTrigger by remember { mutableStateOf(0) }

  Column(modifier = modifier.fillMaxSize()) {
    // Top App Bar with SSL Security & Website URL
    Surface(
      color = VedashilaPrimaryGreen,
      tonalElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            androidx.compose.foundation.Image(
              painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_vedashila_logo),
              contentDescription = "Vedashila Logo",
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(2.dp)
                .testTag("img_vedashila_logo_storefront"),
              contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "VEDASHILA",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "SSL Secure",
                  tint = VedashilaGold,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "https://vedashila.com",
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                refreshTrigger++
              },
              modifier = Modifier.testTag("refresh_webview_button")
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Page",
                tint = Color.White
              )
            }
            IconButton(
              onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                context.startActivity(intent)
              },
              modifier = Modifier.testTag("open_external_browser_button")
            ) {
              Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "Open in Chrome",
                tint = Color.White
              )
            }
          }
        }

        // Quick Navigation Pills for the 10,000+ Catalog and Payment
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          QuickPill(
            label = "94+ Categories",
            highlight = true,
            onClick = onOpenCategories
          )
          QuickPill(
            label = "Pure Shilajit",
            highlight = false,
            onClick = { currentUrl = "https://vedashila.com/?s=shilajit" }
          )
          QuickPill(
            label = "Best Sellers",
            highlight = false,
            onClick = { currentUrl = "https://vedashila.com/shop" }
          )
          QuickPill(
            label = "Cart & Checkout",
            highlight = false,
            onClick = { currentUrl = "https://vedashila.com/cart" }
          )
          QuickPill(
            label = "Payment Gateway",
            highlight = false,
            onClick = { currentUrl = "https://vedashila.com/checkout" }
          )
          QuickPill(
            label = "Admin Console",
            highlight = false,
            onClick = onOpenAdmin
          )
        }
      }
    }

    // Security & Integrated Payment Trust Banner
    Surface(
      color = Color(0xFFF1F7F4),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = VedashilaPrimaryGreen,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Payment Gateway Verified • Razorpay & UPI Enabled",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = VedashilaPrimaryGreen
          )
        }
        Text(
          text = "Real-time Stock",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF165B46)
        )
      }
    }

    // Embedded Storefront
    StorefrontWebView(
      initialUrl = currentUrl,
      currentRequestedUrl = if (refreshTrigger > 0) "$currentUrl#refresh=$refreshTrigger" else currentUrl,
      onUrlChanged = { newUrl -> currentUrl = newUrl },
      onTitleChanged = { newTitle -> pageTitle = newTitle },
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun QuickPill(
  label: String,
  highlight: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = if (highlight) VedashilaGold else Color.White.copy(alpha = 0.15f),
    contentColor = if (highlight) Color(0xFF14201A) else Color.White,
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
  }
}
