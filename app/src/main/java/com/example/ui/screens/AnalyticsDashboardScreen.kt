package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.AnalyticsRepository
import com.example.model.CampaignMetric
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaContainerGreen
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen

@Composable
fun AnalyticsDashboardScreen(
  onNavigateToPosters: () -> Unit = {},
  onNavigateToVideos: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var timeRange by remember { mutableStateOf("7d") }
  val summary = AnalyticsRepository.summary
  val campaigns = AnalyticsRepository.topCampaigns

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
            text = "Marketing Analytics Dashboard",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
          Text(
            text = "Sales Trends • Campaign ROAS • Traffic Attribution",
            color = VedashilaGold,
            fontSize = 11.sp
          )
        }
        Icon(
          imageVector = Icons.Default.Insights,
          contentDescription = null,
          tint = VedashilaGold,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Time Range Selector
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Time Window:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedashilaPrimaryGreen)
          listOf("7d" to "Last 7 Days", "30d" to "Last 30 Days", "90d" to "Quarterly").forEach { (key, label) ->
            FilterChip(
              selected = timeRange == key,
              onClick = { timeRange = key },
              label = { Text(label, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VedashilaPrimaryGreen,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      // 2. Executive KPI Cards Grid
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KpiCard(
            title = "Campaign Revenue",
            value = summary.campaignRevenue,
            subtext = "+34.8% vs last month",
            color = VedashilaPrimaryGreen,
            modifier = Modifier.weight(1f)
          )
          KpiCard(
            title = "Average ROAS",
            value = summary.averageRoas,
            subtext = "High profitability",
            color = Color(0xFFC59B27),
            modifier = Modifier.weight(1f)
          )
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KpiCard(
            title = "Website Visitors",
            value = summary.totalVisitors,
            subtext = "Traffic to vedashila.com",
            color = Color(0xFF1B6B4F),
            modifier = Modifier.weight(1f)
          )
          KpiCard(
            title = "Conversion Rate",
            value = summary.conversionRate,
            subtext = "1,894 total orders",
            color = Color(0xFFB34A26),
            modifier = Modifier.weight(1f)
          )
        }
      }

      // 3. Embedded Recharts Visualization (AreaChart, BarChart & PieChart)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, VedashilaBorder, RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(VedashilaGold),
                  contentAlignment = Alignment.Center
                ) {
                  Text("📊", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Recharts Live Visualization",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = VedashilaPrimaryGreen
                  )
                  Text(
                    text = "Interactive Area & Bar Charts with Tooltips",
                    fontSize = 11.sp,
                    color = Color.Gray
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFE8F5E9)
              ) {
                Text(
                  text = "LIVE DATA",
                  color = Color(0xFF2E7D32),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recharts Web Engine
            RechartsWebEngine(
              timeRange = timeRange,
              modifier = Modifier
                .fillMaxWidth()
                .height(640.dp)
            )
          }
        }
      }

      // 4. Top Performing Marketing Campaigns List
      item {
        Text(
          text = "Top-Performing Marketing Campaigns",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = VedashilaPrimaryGreen
        )
      }

      items(campaigns, key = { it.id }) { cmp ->
        CampaignCard(campaign = cmp)
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }
}

@Composable
private fun KpiCard(
  title: String,
  value: String,
  subtext: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.border(1.dp, VedashilaBorder, RoundedCornerShape(12.dp))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtext, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RechartsWebEngine(
  timeRange: String,
  modifier: Modifier = Modifier
) {
  val htmlContent = remember(timeRange) {
    AnalyticsRepository.generateRechartsHtml(timeRange)
  }

  AndroidView(
    modifier = modifier,
    factory = { ctx ->
      WebView(ctx).apply {
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          loadWithOverviewMode = true
          useWideViewPort = true
          cacheMode = WebSettings.LOAD_DEFAULT
        }
        webViewClient = WebViewClient()
        loadDataWithBaseURL("https://vedashila.com", htmlContent, "text/html", "UTF-8", null)
      }
    },
    update = { webView ->
      webView.loadDataWithBaseURL("https://vedashila.com", htmlContent, "text/html", "UTF-8", null)
    }
  )
}

@Composable
private fun CampaignCard(campaign: CampaignMetric) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, VedashilaBorder, RoundedCornerShape(12.dp))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = campaign.name,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = VedashilaPrimaryGreen
          )
          Text(
            text = "${campaign.platform} • ${campaign.format}",
            fontSize = 11.sp,
            color = Color.Gray
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = VedashilaGold
        ) {
          Text(
            text = "${campaign.roas}x ROAS",
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = Color(0xFF14201A),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Revenue", fontSize = 10.sp, color = Color.Gray)
          Text("₹${campaign.revenue}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VedashilaPrimaryGreen)
        }
        Column {
          Text("Impressions", fontSize = 10.sp, color = Color.Gray)
          Text("${campaign.impressions}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Column {
          Text("Clicks", fontSize = 10.sp, color = Color.Gray)
          Text("${campaign.clicks}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Column {
          Text("Orders", fontSize = 10.sp, color = Color.Gray)
          Text("${campaign.conversions}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB34A26))
        }
      }
    }
  }
}
