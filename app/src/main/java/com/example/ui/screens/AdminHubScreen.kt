package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaContainerGreen
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen

@Composable
fun AdminHubScreen(
  onOpenStorefrontUrl: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val scope = rememberCoroutineScope()

  val inventoryStatus by InventoryRepository.inventoryStatus.collectAsStateWithLifecycle()
  val syncLogs by InventoryRepository.syncLog.collectAsStateWithLifecycle()
  val products by InventoryRepository.products.collectAsStateWithLifecycle()

  var expandedStepIndex by remember { mutableStateOf<Int?>(0) }
  var selectedProductForSim by remember(products) { mutableStateOf(products.first()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
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
            text = "Admin Console & Real-Time Sync",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
          Text(
            text = "vedashila.com Backend Control & Webhook Hub",
            color = VedashilaGold,
            fontSize = 11.sp
          )
        }
        Icon(
          imageVector = Icons.Default.AdminPanelSettings,
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
      // 1. One-Click Launch Admin Portal Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VedashilaContainerGreen),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(VedashilaPrimaryGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = VedashilaGold,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Website Admin Console Portal",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VedashilaPrimaryGreen
              )
              Text(
                text = "https://vedashila.com/wp-admin",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B6B4F)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { onOpenStorefrontUrl("https://vedashila.com/wp-admin") },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
              modifier = Modifier
                .weight(1.2f)
                .height(44.dp)
                .testTag("open_admin_in_app_button")
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = VedashilaGold,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Open in App", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
              onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vedashila.com/wp-admin"))
                context.startActivity(intent)
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = VedashilaPrimaryGreen),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("open_admin_browser_button")
            ) {
              Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("In Chrome", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
          }
        }
      }

      // 2. Real-Time Inventory Sync Status & Stats
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, VedashilaBorder, RoundedCornerShape(16.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = VedashilaPrimaryGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Live Inventory Sync Engine",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VedashilaPrimaryGreen
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFE8F5E9)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "CONNECTED",
                  color = Color(0xFF2E7D32),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            InventoryStatTile(
              label = "Total SKUs",
              value = "${inventoryStatus.totalProducts}",
              sub = "94 Categories",
              color = VedashilaPrimaryGreen,
              modifier = Modifier.weight(1f)
            )
            InventoryStatTile(
              label = "In Stock",
              value = "${inventoryStatus.inStockCount}",
              sub = "Ready to Ship",
              color = Color(0xFF1B6B4F),
              modifier = Modifier.weight(1f)
            )
            InventoryStatTile(
              label = "Low Stock",
              value = "${inventoryStatus.lowStockCount}",
              sub = "Alert Triggered",
              color = Color(0xFFE65100),
              modifier = Modifier.weight(1f)
            )
            InventoryStatTile(
              label = "Out of Stock",
              value = "${inventoryStatus.outOfStockCount}",
              sub = "Marketing Locked",
              color = Color(0xFFC62828),
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = inventoryStatus.lastSyncTimestamp,
            fontSize = 12.sp,
            color = Color(0xFF5D6B63)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              InventoryRepository.triggerFullSync()
              Toast.makeText(context, "Full catalog sync triggered with vedashila.com!", Toast.LENGTH_SHORT).show()
            },
            enabled = !inventoryStatus.isSyncing,
            colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("trigger_full_sync_button")
          ) {
            if (inventoryStatus.isSyncing) {
              CircularProgressIndicator(color = VedashilaGold, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Syncing with vedashila.com...")
            } else {
              Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = VedashilaGold, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Trigger Full Real-Time Sync", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 3. Live Inventory Simulation Test Bench (Tests Out-of-Stock Marketing Prevention!)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, VedashilaBorder, RoundedCornerShape(16.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Sensors,
              contentDescription = null,
              tint = Color(0xFFB34A26),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Live Website Inventory Test Bench",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = VedashilaPrimaryGreen
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Test how changes on vedashila.com immediately reflect in the app and dynamically disable marketing when stock hits zero.",
            fontSize = 11.sp,
            color = Color(0xFF5D6B63)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Product Selector for simulation
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            products.forEach { prod ->
              val isSelected = selectedProductForSim.id == prod.id
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) VedashilaPrimaryGreen else Color(0xFFF1F6F3),
                contentColor = if (isSelected) Color.White else Color(0xFF14201A),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedProductForSim = prod }
              ) {
                Text(
                  text = "${prod.title.take(18)}... (Stock: ${prod.stockQuantity})",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Simulation Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                InventoryRepository.simulateWebsitePurchase(selectedProductForSim.id)
                Toast.makeText(context, "Simulated 1 purchase! Stock decreased.", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Simulate Sale (-1)", fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = {
                InventoryRepository.updateStock(selectedProductForSim.id, 0)
                Toast.makeText(context, "${selectedProductForSim.title} set to Out of Stock. Marketing locked!", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
              modifier = Modifier.weight(1f)
            ) {
              Text("Set Zero Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                InventoryRepository.simulateRestock(selectedProductForSim.id, 50)
                Toast.makeText(context, "${selectedProductForSim.title} restocked (+50)! Marketing unlocked.", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
              modifier = Modifier.weight(1f)
            ) {
              Text("Restock (+50)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Live Activity Log
          Text(text = "Real-Time Activity Webhook Log:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              syncLogs.takeLast(4).forEach { log ->
                Text(
                  text = "> $log",
                  color = Color(0xFF38BDF8),
                  fontSize = 10.sp,
                  fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
              }
            }
          }
        }
      }

      // 4. Interactive Step-by-Step Login Guide
      Text(
        text = "How to Login in Admin Console on Website",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = VedashilaPrimaryGreen
      )

      val loginSteps = listOf(
        LoginGuideStep(
          stepNumber = 1,
          title = "Navigate to Admin URL",
          summary = "Go to https://vedashila.com/wp-admin in your browser.",
          details = "Enter 'https://vedashila.com/wp-admin' or 'https://vedashila.com/wp-login.php'. You will see the official Vedashila WordPress / WooCommerce administrative login page."
        ),
        LoginGuideStep(
          stepNumber = 2,
          title = "Enter Administrator Credentials",
          summary = "Use your registered admin email or username.",
          details = "Enter your username or email address (e.g. mishkapharmacy@gmail.com) and the secure master password. Click 'Remember Me' to stay signed in during product updates."
        ),
        LoginGuideStep(
          stepNumber = 3,
          title = "Two-Factor Verification (2FA)",
          summary = "If 2FA is enabled, confirm via SMS or Authenticator App.",
          details = "If Wordfence or Google Authenticator security plugin is active, open your Authenticator app (or check your email/SMS) to enter the 6-digit one-time code."
        ),
        LoginGuideStep(
          stepNumber = 4,
          title = "Access Products & 94+ Categories",
          summary = "Navigate to WooCommerce > Products on the left sidebar.",
          details = "Inside the dashboard, click 'Products > All Products' to view, search, and bulk edit the 10,000+ items. Click 'Products > Categories' to manage the 94+ Ayurvedic category taxonomy."
        ),
        LoginGuideStep(
          stepNumber = 5,
          title = "Real-Time Stock Management",
          summary = "Update inventory numbers under the 'Inventory' tab.",
          details = "In each product's edit page, scroll to 'Product Data > Inventory'. Check 'Manage stock?' and enter the current stock quantity. Changes reflect instantly on the website and inside this app."
        ),
        LoginGuideStep(
          stepNumber = 6,
          title = "Payment Gateway Verification",
          summary = "Configure Razorpay / UPI in WooCommerce > Settings > Payments.",
          details = "Verify your payment gateway status under WooCommerce > Settings > Payments. Ensure Razorpay, Paytm, or UPI Key ID and Key Secret are in 'Live' mode for checkout processing."
        )
      )

      loginSteps.forEachIndexed { index, step ->
        val isExpanded = expandedStepIndex == index
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VedashilaBorder, RoundedCornerShape(12.dp))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { expandedStepIndex = if (isExpanded) null else index }
              .padding(14.dp)
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
                Box(
                  modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isExpanded) VedashilaGold else VedashilaPrimaryGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${step.stepNumber}",
                    color = if (isExpanded) Color(0xFF14201A) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = step.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = VedashilaPrimaryGreen
                  )
                  Text(
                    text = step.summary,
                    fontSize = 11.sp,
                    color = Color(0xFF5D6B63)
                  )
                }
              }

              Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color.Gray
              )
            }

            AnimatedVisibility(visible = isExpanded) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 10.dp)
              ) {
                Surface(
                  color = Color(0xFFF7FAF8),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = step.details,
                    fontSize = 12.sp,
                    color = Color(0xFF2C3E35),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(10.dp)
                  )
                }

                if (step.stepNumber == 1) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                      onClick = { onOpenStorefrontUrl("https://vedashila.com/wp-admin") },
                      colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.height(36.dp)
                    ) {
                      Text("Launch Login Page", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                      onClick = {
                        clipboardManager.setText(AnnotatedString("https://vedashila.com/wp-admin"))
                        Toast.makeText(context, "Admin URL copied!", Toast.LENGTH_SHORT).show()
                      },
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.height(36.dp)
                    ) {
                      Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Copy URL", fontSize = 11.sp)
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

@Composable
private fun InventoryStatTile(
  label: String,
  value: String,
  sub: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFFF7FBF8),
    border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = sub, fontSize = 8.sp, color = color, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
  }
}

data class LoginGuideStep(
  val stepNumber: Int,
  val title: String,
  val summary: String,
  val details: String
)
