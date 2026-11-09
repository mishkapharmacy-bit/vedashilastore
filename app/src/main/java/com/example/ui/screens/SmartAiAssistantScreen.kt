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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@Composable
fun SmartAiAssistantScreen(
  onSendToPosters: (Product) -> Unit,
  onSendToVideos: (Product) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val scope = rememberCoroutineScope()
  val liveProducts by InventoryRepository.products.collectAsStateWithLifecycle()

  var selectedProduct by remember(liveProducts) {
    mutableStateOf(liveProducts.first())
  }

  var isGenerating by remember { mutableStateOf(false) }
  var marketingAdvice by remember(selectedProduct) {
    mutableStateOf(SmartAiMarketingService.getSmartFallbackAdvice(selectedProduct))
  }
  var selectedTab by remember { mutableIntStateOf(0) }

  // Refresh advice on product selection
  LaunchedEffect(selectedProduct) {
    marketingAdvice = SmartAiMarketingService.getSmartFallbackAdvice(selectedProduct)
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
            text = "AI Smart Marketing Assistant",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
          Text(
            text = "Inventory Intelligence • Optimal Timing • Gemini AI",
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

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Product Selector Strip
      item {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "1. Select Product to Analyze:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = VedashilaPrimaryGreen
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
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) VedashilaPrimaryGreen else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) VedashilaGold else VedashilaBorder
                ),
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { selectedProduct = prod }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${prod.title.take(18)}...",
                    color = if (isSelected) Color.White else Color(0xFF14201A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when {
                      !prod.inStock -> Color(0xFFFFCDD2)
                      prod.stockQuantity < 20 -> Color(0xFFFFE0B2)
                      else -> Color(0xFFC8E6C9)
                    }
                  ) {
                    Text(
                      text = "${prod.stockQuantity} in stock",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = when {
                        !prod.inStock -> Color(0xFFC62828)
                        prod.stockQuantity < 20 -> Color(0xFFE65100)
                        else -> Color(0xFF2E7D32)
                      },
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 2. Inventory Intelligence & Recommendation Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VedashilaBorder, RoundedCornerShape(14.dp))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(VedashilaContainerGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = VedashilaPrimaryGreen,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Inventory Level Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = VedashilaPrimaryGreen
                  )
                  Text(
                    text = "${selectedProduct.category} • SKU: ${selectedProduct.sku}",
                    fontSize = 11.sp,
                    color = Color.Gray
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (selectedProduct.isMarketable) VedashilaGold else Color(0xFFFFCDD2)
              ) {
                Text(
                  text = if (selectedProduct.isMarketable) "READY TO MARKET" else "OUT OF STOCK",
                  color = if (selectedProduct.isMarketable) Color(0xFF14201A) else Color(0xFFB71C1C),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF7FAF8),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "AI Inventory Strategy:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = VedashilaPrimaryGreen
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = marketingAdvice.inventoryActionRecommendation,
                  fontSize = 12.sp,
                  color = Color(0xFF2C3E35),
                  lineHeight = 17.sp
                )
              }
            }
          }
        }
      }

      // 3. AI-Suggested Optimal Posting Times
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VedashilaBorder, RoundedCornerShape(14.dp))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(VedashilaGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Schedule,
                  contentDescription = null,
                  tint = VedashilaGold,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Optimal Social Media Posting Times",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = VedashilaPrimaryGreen
                )
                Text(
                  text = "Calibrated for Indian Ayurvedic & Health Shoppers",
                  fontSize = 11.sp,
                  color = Color.Gray
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              TimingBadge(
                label = "Peak Time Slot",
                value = marketingAdvice.optimalTimeSlot,
                sub = "Highest Link Click-Through",
                modifier = Modifier.weight(1.2f)
              )
              TimingBadge(
                label = "Peak Days",
                value = marketingAdvice.peakDays,
                sub = "Maximum Weekend Orders",
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Target Audience: ${marketingAdvice.targetAudience}",
              fontSize = 11.sp,
              color = Color(0xFF5D6B63)
            )
          }
        }
      }

      // 4. AI Caption & Hashtag Generator
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VedashilaBorder, RoundedCornerShape(14.dp))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Campaign,
                  contentDescription = null,
                  tint = VedashilaPrimaryGreen,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "AI Generated Copy & Hashtags",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = VedashilaPrimaryGreen
                )
              }

              Button(
                onClick = {
                  scope.launch {
                    isGenerating = true
                    val result = SmartAiMarketingService.generateMarketingInsights(selectedProduct)
                    isGenerating = false
                    result.onSuccess { advice ->
                      marketingAdvice = advice
                      Toast.makeText(context, "Gemini generated fresh marketing copy!", Toast.LENGTH_SHORT).show()
                    }
                  }
                },
                enabled = !isGenerating,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
                modifier = Modifier.height(34.dp).testTag("gemini_generate_button")
              ) {
                if (isGenerating) {
                  CircularProgressIndicator(color = VedashilaGold, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Generating...", fontSize = 11.sp)
                } else {
                  Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VedashilaGold, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Gemini AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Platform Tab Switcher
            TabRow(
              selectedTabIndex = selectedTab,
              containerColor = Color(0xFFF1F6F3),
              indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                  Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                  color = VedashilaPrimaryGreen
                )
              }
            ) {
              listOf("Instagram Post", "WhatsApp Status", "YouTube Ad Script").forEachIndexed { idx, title ->
                Tab(
                  selected = selectedTab == idx,
                  onClick = { selectedTab = idx },
                  text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal) }
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Copy Content
            val activeText = when (selectedTab) {
              0 -> marketingAdvice.instagramCaption
              1 -> marketingAdvice.whatsappBroadcastText
              else -> marketingAdvice.youtubeAdScriptBrief
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF9FBF9),
              border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = activeText,
                  fontSize = 12.sp,
                  lineHeight = 18.sp,
                  color = Color(0xFF14201A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  OutlinedButton(
                    onClick = {
                      clipboardManager.setText(AnnotatedString(activeText))
                      Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp)
                  ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 10.sp)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Hashtags Cloud
            Text(
              text = "Category-Targeted Hashtags (${marketingAdvice.hashtags.size}):",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = VedashilaPrimaryGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              marketingAdvice.hashtags.forEach { tag ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = VedashilaContainerGreen,
                  modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(tag))
                    Toast.makeText(context, "$tag copied!", Toast.LENGTH_SHORT).show()
                  }
                ) {
                  Text(
                    text = tag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VedashilaPrimaryGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }

      // 5. Transfer to Poster / Video Studio Actions
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { onSendToPosters(selectedProduct) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("send_to_poster_studio_button")
          ) {
            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = VedashilaGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create Poster", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { onSendToVideos(selectedProduct) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB34A26)),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("send_to_video_studio_button")
          ) {
            Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create Video Ad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }
}

@Composable
private fun TimingBadge(
  label: String,
  value: String,
  sub: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFFF7FBF8),
    border = androidx.compose.foundation.BorderStroke(1.dp, VedashilaBorder),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VedashilaPrimaryGreen)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = sub, fontSize = 9.sp, color = Color(0xFF5D6B63))
    }
  }
}
