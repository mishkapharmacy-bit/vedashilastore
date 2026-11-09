package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.viewmodel.ProductUiState
import com.example.ui.viewmodel.ProductViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.model.Product
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaContainerGreen
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.BasicAlertDialog
import com.example.service.InventoryAlertService

/**
 * ProductListScreen displays cached products from the Room database using a LazyColumn.
 * Observes ProductViewModel for offline-first querying and real-time inventory synchronization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
  onSelectProductForStore: (String) -> Unit = {},
  onSelectProductForPoster: (Product) -> Unit = {},
  onSelectProductForVideo: (Product) -> Unit = {},
  onSelectProduct: (Product) -> Unit = {},
  viewModel: ProductViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val recentQueries by viewModel.recentQueries.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val selectedDetailProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
  var isSearchDropdownExpanded by remember { mutableStateOf(false) }

  // Low stock threshold state
  val alertThreshold by InventoryAlertService.threshold.collectAsStateWithLifecycle()
  val alertsEnabled by InventoryAlertService.alertsEnabled.collectAsStateWithLifecycle()
  val lowStockCount by InventoryAlertService.lowStockCount.collectAsStateWithLifecycle()
  var showThresholdDialog by remember { mutableStateOf(false) }

  // Threshold Configuration Dialog
  if (showThresholdDialog) {
    LowStockThresholdDialog(
      currentThreshold = alertThreshold,
      alertsEnabled = alertsEnabled,
      onSave = { newThreshold, enabled ->
        InventoryAlertService.updateThreshold(context, newThreshold)
        InventoryAlertService.setAlertsEnabled(context, enabled)
        showThresholdDialog = false
        Toast.makeText(context, "Low stock alert threshold updated to $newThreshold units", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showThresholdDialog = false }
    )
  }

  // If a product is selected for extended details, render ProductDetailScreen
  if (selectedDetailProduct != null) {
    ProductDetailScreen(
      product = selectedDetailProduct!!,
      onBack = { viewModel.clearSelectedProduct() },
      onOpenStoreWeb = onSelectProductForStore,
      onGeneratePoster = onSelectProductForPoster,
      onGenerateVideo = onSelectProductForVideo,
      modifier = modifier
    )
    return
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF9FAF8))
  ) {
    // Header Bar with Room Cache Status & Real-time Sync Button
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
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
                imageVector = Icons.Default.Storage,
                contentDescription = "Room Database",
                tint = VedashilaPrimaryGreen,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Room Offline Cache",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF14201A)
                )
              )
              Text(
                text = "10,480+ Products Indexed in SQLite",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF5A6E63),
                  fontSize = 11.sp
                )
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Low Stock Alert Threshold Button
            IconButton(
              onClick = { showThresholdDialog = true },
              modifier = Modifier.testTag("btn_threshold_settings")
            ) {
              Box {
                Icon(
                  imageVector = if (lowStockCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                  contentDescription = "Alert Threshold Settings",
                  tint = if (lowStockCount > 0) Color(0xFFE65100) else VedashilaPrimaryGreen
                )
                if (lowStockCount > 0) {
                  Surface(
                    color = Color(0xFFD32F2F),
                    shape = CircleShape,
                    modifier = Modifier
                      .size(14.dp)
                      .align(Alignment.TopEnd)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = lowStockCount.toString(),
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }

            // Real-time Sync Button
            IconButton(
              onClick = {
                viewModel.syncInventory()
                Toast.makeText(context, "Syncing Room cache with vedashila.com...", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.testTag("btn_sync_room_cache")
            ) {
              val state = uiState
              if (state is ProductUiState.Success && state.isSyncing) {
                CircularProgressIndicator(
                  modifier = Modifier.size(22.dp),
                  color = VedashilaPrimaryGreen,
                  strokeWidth = 2.dp
                )
              } else {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "Sync Room Cache",
                  tint = VedashilaPrimaryGreen
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input Field with Real-Time Filtering & Drop-down for last 5 queries
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = {
              viewModel.setSearchQuery(it)
              if (it.isNotBlank() && it.length >= 2) {
                viewModel.submitSearchQuery(it)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_bar_product_list"),
            placeholder = {
              Text(
                "Filter products by name in real-time...",
                fontSize = 13.sp,
                color = Color(0xFF8B9E94)
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search products by name",
                tint = VedashilaPrimaryGreen
              )
            },
            trailingIcon = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (searchQuery.isNotEmpty()) {
                  IconButton(
                    onClick = { viewModel.setSearchQuery("") },
                    modifier = Modifier.testTag("btn_clear_product_search")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = "Clear search",
                      tint = Color.Gray
                    )
                  }
                }
                // Dropdown trigger button showing last 5 queries
                if (recentQueries.isNotEmpty()) {
                  IconButton(
                    onClick = { isSearchDropdownExpanded = !isSearchDropdownExpanded },
                    modifier = Modifier.testTag("btn_search_history_dropdown")
                  ) {
                    Icon(
                      imageVector = Icons.Default.History,
                      contentDescription = "Recent Searches",
                      tint = if (isSearchDropdownExpanded) VedashilaPrimaryGreen else Color(0xFF6B7A72),
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = VedashilaPrimaryGreen,
              unfocusedBorderColor = VedashilaBorder,
              focusedContainerColor = Color(0xFFF7F9F7),
              unfocusedContainerColor = Color(0xFFF7F9F7)
            )
          )

          // Drop-down displaying the last 5 search queries for quick access
          DropdownMenu(
            expanded = isSearchDropdownExpanded,
            onDismissRequest = { isSearchDropdownExpanded = false },
            modifier = Modifier
              .fillMaxWidth(0.92f)
              .background(Color.White, RoundedCornerShape(12.dp))
              .testTag("menu_recent_searches")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = null,
                  tint = VedashilaGold,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Recent Searches (Last 5)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF14201A)
                )
              }
              Text(
                text = "Clear",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD32F2F),
                modifier = Modifier
                  .clickable {
                    viewModel.clearRecentQueries()
                    isSearchDropdownExpanded = false
                  }
                  .padding(4.dp)
                  .testTag("btn_clear_all_recent_searches")
              )
            }

            recentQueries.take(5).forEach { queryItem ->
              DropdownMenuItem(
                text = {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = VedashilaPrimaryGreen,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = queryItem,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF14201A)
                      )
                    }

                    IconButton(
                      onClick = { viewModel.removeRecentQuery(queryItem) },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove query",
                        tint = Color.LightGray,
                        modifier = Modifier.size(14.dp)
                      )
                    }
                  }
                },
                onClick = {
                  viewModel.setSearchQuery(queryItem)
                  viewModel.submitSearchQuery(queryItem)
                  isSearchDropdownExpanded = false
                },
                modifier = Modifier.testTag("item_recent_search_$queryItem")
              )
            }
          }
        }

        if (searchQuery.isNotBlank()) {
          val count = (uiState as? ProductUiState.Success)?.products?.size ?: 0
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Filtering by name: \"$searchQuery\" ($count matches)",
              fontSize = 11.sp,
              color = VedashilaPrimaryGreen,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Save search",
              fontSize = 10.5.sp,
              color = VedashilaGold,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable {
                  viewModel.submitSearchQuery(searchQuery)
                  Toast.makeText(context, "Saved to recent searches", Toast.LENGTH_SHORT).show()
                }
                .testTag("btn_save_current_search")
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        val categories = listOf(
          "All",
          "Pure Shilajit & Resins",
          "Kumkumadi & Skin Radiance",
          "Ashwagandha & Stress Relief",
          "Organic Chyawanprash",
          "Gut Health & Triphala",
          "Memory, Focus & Brahmi"
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = (cat == "All" && selectedCategory == null) || (cat == selectedCategory)
            FilterChip(
              selected = isSelected,
              onClick = {
                viewModel.selectCategory(if (cat == "All") null else cat)
              },
              label = {
                Text(
                  text = cat,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VedashilaPrimaryGreen,
                selectedLabelColor = Color.White,
                containerColor = Color.White,
                labelColor = Color(0xFF4A5C52)
              ),
              border = FilterChipDefaults.filterChipBorder(
                borderColor = if (isSelected) VedashilaPrimaryGreen else VedashilaBorder,
                selectedBorderColor = VedashilaPrimaryGreen,
                enabled = true,
                selected = isSelected
              )
            )
          }
        }
      }
    }

    // Main LazyColumn of Cached Products
    when (val state = uiState) {
      is ProductUiState.Loading -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = VedashilaPrimaryGreen)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              "Loading Room Database Cache...",
              color = Color(0xFF5A6E63),
              fontSize = 13.sp
            )
          }
        }
      }

      is ProductUiState.Error -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Error: ${state.message}",
            color = MaterialTheme.colorScheme.error
          )
        }
      }

      is ProductUiState.Success -> {
        val isRefreshing = (uiState as? ProductUiState.Success)?.isSyncing == true

        PullToRefreshBox(
          isRefreshing = isRefreshing,
          onRefresh = {
            viewModel.syncInventory()
            Toast.makeText(context, "Pull-to-refresh: Syncing inventory from vedashila.com...", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier
            .fillMaxSize()
            .testTag("pull_to_refresh_product_list"),
          indicator = {
            Indicator(
              modifier = Modifier.align(Alignment.TopCenter),
              isRefreshing = isRefreshing,
              containerColor = Color.White,
              color = VedashilaPrimaryGreen,
              state = rememberPullToRefreshState()
            )
          }
        ) {
          if (state.products.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = Color(0xFF9EABA4),
                  modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  "No matching products in Room cache",
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF2D3C34)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  "Pull down to refresh inventory from vedashila.com or adjust filters.",
                  color = Color(0xFF6B7A72),
                  fontSize = 12.sp
                )
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .testTag("lazy_cached_products_list"),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              item {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Displaying ${state.products.size} cached items",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5A6E63)
                  )
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CloudDone,
                      contentDescription = null,
                      tint = VedashilaPrimaryGreen,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Pull to refresh",
                      fontSize = 11.sp,
                      color = VedashilaPrimaryGreen,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              }

              items(
                items = state.products,
                key = { it.id }
              ) { product ->
                CachedProductCard(
                  product = product,
                  onSelectProduct = {
                    viewModel.selectProductById(product.id)
                    onSelectProduct(product)
                  },
                  onToggleFavorite = {
                    viewModel.toggleFavorite(product.id)
                  },
                  onBuyOnline = { onSelectProductForStore(product.productUrl) },
                  onGeneratePoster = { onSelectProductForPoster(product) },
                  onGenerateVideo = { onSelectProductForVideo(product) },
                  modifier = Modifier.padding(horizontal = 16.dp)
                )
              }

              item {
                Spacer(modifier = Modifier.height(16.dp))
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Card component for individual Room cached product
 */
@Composable
fun CachedProductCard(
  product: Product,
  onSelectProduct: () -> Unit = {},
  onToggleFavorite: () -> Unit = {},
  onBuyOnline: () -> Unit,
  onGeneratePoster: () -> Unit,
  onGenerateVideo: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("card_product_${product.id}")
      .clickable { onSelectProduct() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Product Thumbnail
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF3F6F4))
            .border(1.dp, VedashilaBorder, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          if (product.localDrawableRes != null) {
            Image(
              painter = painterResource(id = product.localDrawableRes),
              contentDescription = product.title,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          } else {
            Image(
              painter = painterResource(id = R.drawable.ic_vedashila_logo_1793942426062),
              contentDescription = product.title,
              modifier = Modifier
                .size(48.dp)
                .padding(4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Product Details
        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = product.sku,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = VedashilaGold
            )
            // Heart Favorite Icon & Stock Indicator Badge Row
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                  .size(28.dp)
                  .testTag("btn_favorite_${product.id}")
              ) {
                Icon(
                  imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                  contentDescription = if (product.isFavorite) "Remove from favorites" else "Add to favorites",
                  tint = if (product.isFavorite) Color(0xFFE53935) else Color(0xFF9EABA4),
                  modifier = Modifier.size(19.dp)
                )
              }
              Spacer(modifier = Modifier.width(4.dp))
              // Stock Indicator Badge (In Stock / Low Stock / Out of Stock)
              val isLowStock = InventoryAlertService.isProductLowStock(product)
              Surface(
                color = when {
                  !product.inStock || product.stockQuantity == 0 -> Color(0xFFFFEBEE)
                  isLowStock -> Color(0xFFFFF3E0)
                  else -> Color(0xFFE8F5E9)
                },
                shape = RoundedCornerShape(6.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  if (isLowStock) {
                    Icon(
                      imageVector = Icons.Default.WarningAmber,
                      contentDescription = "Low Stock",
                      tint = Color(0xFFE65100),
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                  }
                  Text(
                    text = when {
                      !product.inStock || product.stockQuantity == 0 -> "Out of Stock"
                      isLowStock -> "LOW STOCK (${product.stockQuantity})"
                      else -> "In Stock (${product.stockQuantity})"
                    },
                    color = when {
                      !product.inStock || product.stockQuantity == 0 -> Color(0xFFC62828)
                      isLowStock -> Color(0xFFE65100)
                      else -> Color(0xFF2E7D32)
                    },
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = product.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 14.5.sp,
              color = Color(0xFF14201A)
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(4.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "₹${product.price}",
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold,
              color = VedashilaPrimaryGreen
            )
            if (product.originalPrice > product.price) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "₹${product.originalPrice}",
                fontSize = 12.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${product.discountPercent}% OFF",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
              )
            }
          }
        }
      }

      // Benefits Snippet
      if (product.benefits.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "• " + product.benefits.take(2).joinToString("\n• "),
          fontSize = 11.5.sp,
          color = Color(0xFF4A5C52),
          lineHeight = 16.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Buy on Website
        OutlinedButton(
          onClick = onBuyOnline,
          modifier = Modifier
            .weight(1f)
            .height(36.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = VedashilaPrimaryGreen
          )
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Store Web", fontSize = 11.5.sp)
        }

        // Marketing Poster
        Button(
          onClick = onGeneratePoster,
          enabled = product.isMarketable,
          modifier = Modifier
            .weight(1f)
            .height(36.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = VedashilaGold,
            contentColor = Color(0xFF14201A)
          )
        ) {
          Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Poster", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        }

        // Video Ads
        Button(
          onClick = onGenerateVideo,
          enabled = product.isMarketable,
          modifier = Modifier
            .weight(1f)
            .height(36.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = VedashilaPrimaryGreen,
            contentColor = Color.White
          )
        ) {
          Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Video Ad", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Dialog allowing users to configure the custom Low Stock threshold and toggle local notifications.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowStockThresholdDialog(
  currentThreshold: Int,
  alertsEnabled: Boolean,
  onSave: (Int, Boolean) -> Unit,
  onDismiss: () -> Unit
) {
  var sliderValue by remember { mutableStateOf(currentThreshold.toFloat()) }
  var enabledSwitch by remember { mutableStateOf(alertsEnabled) }

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("dialog_threshold_settings")
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
                .background(Color(0xFFFFF3E0)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Low Stock Alert Limit",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF14201A)
              )
              Text(
                text = "Local Notification Trigger",
                fontSize = 11.sp,
                color = VedashilaGold,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Trigger a low stock badge and notification whenever a product inventory falls below this quantity:",
          fontSize = 12.5.sp,
          color = Color(0xFF5A6E63),
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Threshold Counter Card
        Surface(
          color = Color(0xFFF9FAF8),
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
            Text(
              text = "Alert Threshold:",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.5.sp,
              color = Color(0xFF14201A)
            )
            Surface(
              color = Color(0xFFFFF3E0),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "${sliderValue.toInt()} Units",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Slider (1 to 100 units)
        Slider(
          value = sliderValue,
          onValueChange = { sliderValue = it },
          valueRange = 1f..100f,
          steps = 98,
          colors = SliderDefaults.colors(
            thumbColor = VedashilaPrimaryGreen,
            activeTrackColor = VedashilaPrimaryGreen,
            inactiveTrackColor = Color(0xFFE0E0E0)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("slider_threshold")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("1 unit", fontSize = 11.sp, color = Color.Gray)
          Text("50 units", fontSize = 11.sp, color = Color.Gray)
          Text("100 units", fontSize = 11.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications Toggle Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Send Local Notification",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              color = Color(0xFF14201A)
            )
            Text(
              text = "Alert when stock dips in background",
              fontSize = 11.sp,
              color = Color.Gray
            )
          }

          androidx.compose.material3.Switch(
            checked = enabledSwitch,
            onCheckedChange = { enabledSwitch = it },
            colors = androidx.compose.material3.SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = VedashilaPrimaryGreen
            )
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(44.dp),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Cancel", fontSize = 13.sp)
          }

          Button(
            onClick = { onSave(sliderValue.toInt(), enabledSwitch) },
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("btn_save_threshold"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = VedashilaPrimaryGreen,
              contentColor = Color.White
            )
          ) {
            Text("Save Limit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
    }
  }
}
