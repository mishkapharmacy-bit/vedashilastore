package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Product
import com.example.ui.screens.AdminHubScreen
import com.example.ui.screens.AnalyticsDashboardScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.MarketingStudioScreen
import com.example.ui.screens.PosterStudioScreen
import com.example.ui.screens.SmartAiAssistantScreen
import com.example.ui.screens.StorefrontScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen
import com.example.util.configureAppCheck

enum class AppTab(
  val title: String,
  val icon: ImageVector,
  val tag: String
) {
  STORE("Store", Icons.Default.Storefront, "tab_store"),
  CATALOG("Catalog", Icons.Default.Category, "tab_catalog"),
  AI_STUDIO("AI Studio", Icons.Default.AutoAwesome, "tab_marketing_studio"),
  POSTERS("Posters", Icons.Default.Campaign, "tab_posters"),
  VIDEOS("Videos", Icons.Default.Movie, "tab_videos"),
  ANALYTICS("Dashboard", Icons.Default.Insights, "tab_analytics"),
  ADMIN("Admin", Icons.Default.AdminPanelSettings, "tab_admin")
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Initialize Room Database local cache for offline-first catalog performance
    com.example.data.InventoryRepository.initDatabase(this)
    // Initialize Inventory Low Stock Alert Service
    com.example.service.InventoryAlertService.init(this)
    // Mandatory App Check configuration with launch intent before UI/Firebase calls
    configureAppCheck(this, intent)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent()
      }
    }
  }
}

@Composable
fun MainAppContent() {
  var currentTab by remember { mutableStateOf(AppTab.STORE) }
  var activeStoreUrl by remember { mutableStateOf<String?>("https://vedashila.com") }
  var selectedProductForMarketing by remember { mutableStateOf<Product?>(null) }

  // Back handling: If on secondary tab, return to Storefront
  BackHandler(enabled = currentTab != AppTab.STORE) {
    currentTab = AppTab.STORE
  }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    bottomBar = {
      NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
      ) {
        AppTab.values().forEach { tab ->
          val isSelected = currentTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentTab = tab },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.title
              )
            },
            label = {
              Text(
                text = tab.title,
                fontSize = 9.5.sp,
                maxLines = 1
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color(0xFF14201A),
              selectedTextColor = VedashilaPrimaryGreen,
              indicatorColor = VedashilaGold,
              unselectedIconColor = Color(0xFF6B7A72),
              unselectedTextColor = Color(0xFF6B7A72)
            ),
            modifier = Modifier.testTag(tab.tag)
          )
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    val contentModifier = Modifier.padding(innerPadding)

    when (currentTab) {
      AppTab.STORE -> {
        StorefrontScreen(
          targetUrl = activeStoreUrl,
          onOpenCategories = { currentTab = AppTab.CATALOG },
          onOpenAdmin = { currentTab = AppTab.ADMIN },
          modifier = contentModifier
        )
      }

      AppTab.CATALOG -> {
        CatalogScreen(
          onSelectProductForStore = { url ->
            activeStoreUrl = url
            currentTab = AppTab.STORE
          },
          onSelectProductForPoster = { product ->
            selectedProductForMarketing = product
            currentTab = AppTab.POSTERS
          },
          onSelectProductForVideo = { product ->
            selectedProductForMarketing = product
            currentTab = AppTab.VIDEOS
          },
          modifier = contentModifier
        )
      }

      AppTab.AI_STUDIO -> {
        MarketingStudioScreen(
          initialProduct = selectedProductForMarketing,
          onNavigateToPosters = { product ->
            selectedProductForMarketing = product
            currentTab = AppTab.POSTERS
          },
          onNavigateToVideos = { product ->
            selectedProductForMarketing = product
            currentTab = AppTab.VIDEOS
          },
          modifier = contentModifier
        )
      }

      AppTab.POSTERS -> {
        PosterStudioScreen(
          initialProduct = selectedProductForMarketing,
          modifier = contentModifier
        )
      }

      AppTab.VIDEOS -> {
        VideoStudioScreen(
          initialProduct = selectedProductForMarketing,
          modifier = contentModifier
        )
      }

      AppTab.ANALYTICS -> {
        AnalyticsDashboardScreen(
          onNavigateToPosters = { currentTab = AppTab.POSTERS },
          onNavigateToVideos = { currentTab = AppTab.VIDEOS },
          modifier = contentModifier
        )
      }

      AppTab.ADMIN -> {
        AdminHubScreen(
          onOpenStorefrontUrl = { url ->
            activeStoreUrl = url
            currentTab = AppTab.STORE
          },
          modifier = contentModifier
        )
      }
    }
  }
}
