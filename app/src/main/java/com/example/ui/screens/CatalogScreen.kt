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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.InventoryRepository
import com.example.model.Product
import com.example.model.ProductFilter
import com.example.model.SortOption
import com.example.ui.theme.VedashilaBorder
import com.example.ui.theme.VedashilaContainerGreen
import com.example.ui.theme.VedashilaGold
import com.example.ui.theme.VedashilaPrimaryGreen

@Composable
fun CatalogScreen(
  onSelectProductForStore: (String) -> Unit,
  onSelectProductForPoster: (Product) -> Unit,
  onSelectProductForVideo: (Product) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val products by InventoryRepository.products.collectAsStateWithLifecycle()
  val inventoryStatus by InventoryRepository.inventoryStatus.collectAsStateWithLifecycle()

  var filter by remember { mutableStateOf(ProductFilter()) }
  var isFilterExpanded by remember { mutableStateOf(false) }

  // Advanced Filtering and Sorting Logic
  val filteredProducts = remember(products, filter) {
    products
      .filter { product ->
        val matchesQuery = filter.query.isBlank() ||
            product.title.contains(filter.query, ignoreCase = true) ||
            product.sku.contains(filter.query, ignoreCase = true) ||
            product.category.contains(filter.query, ignoreCase = true) ||
            product.dosageForm.contains(filter.query, ignoreCase = true) ||
            product.healthGoal.contains(filter.query, ignoreCase = true) ||
            product.benefits.any { it.contains(filter.query, ignoreCase = true) }

        val matchesCategory = filter.category == null || product.category == filter.category
        val matchesPrice = product.price in filter.minPrice..filter.maxPrice
        val matchesInStock = !filter.inStockOnly || product.inStock
        val matchesMarketable = !filter.marketableOnly || product.isMarketable
        val matchesDosage = filter.dosageForm == null || product.dosageForm == filter.dosageForm
        val matchesHealthGoal = filter.healthGoal == null || product.healthGoal == filter.healthGoal

        matchesQuery && matchesCategory && matchesPrice && matchesInStock && matchesMarketable && matchesDosage && matchesHealthGoal
      }
      .let { list ->
        when (filter.sortBy) {
          SortOption.RELEVANCE -> list
          SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
          SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
          SortOption.STOCK_HIGH_LOW -> list.sortedByDescending { it.stockQuantity }
          SortOption.RATING -> list.sortedByDescending { it.rating }
        }
      }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar with Real-time Sync Status
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
              painter = painterResource(id = com.example.R.drawable.ic_vedashila_logo),
              contentDescription = "Vedashila Logo",
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(2.dp)
                .testTag("img_vedashila_logo_catalog"),
              contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Catalog & Real-Time Stock",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF34D399))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = inventoryStatus.lastSyncTimestamp,
                  color = VedashilaGold,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          IconButton(
            onClick = {
              InventoryRepository.triggerFullSync()
              Toast.makeText(context, "Instant stock sync triggered with vedashila.com!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.testTag("sync_catalog_icon_button")
          ) {
            Icon(
              imageVector = Icons.Default.Sync,
              contentDescription = "Sync",
              tint = VedashilaGold
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar with Filter Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = filter.query,
            onValueChange = { filter = filter.copy(query = it) },
            placeholder = {
              Text(
                "Search Shilajit, herbs, SKU, benefits...",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.65f)
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.8f)
              )
            },
            trailingIcon = {
              if (filter.query.isNotBlank()) {
                IconButton(onClick = { filter = filter.copy(query = "") }) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color.White
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedContainerColor = Color.White.copy(alpha = 0.12f),
              unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
              focusedBorderColor = VedashilaGold,
              unfocusedBorderColor = Color.White.copy(alpha = 0.25f)
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("advanced_search_input")
          )

          // Filter Button with Active Indicator
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isFilterExpanded || filter.isFiltered) VedashilaGold else Color.White.copy(alpha = 0.15f),
            modifier = Modifier
              .size(50.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable { isFilterExpanded = !isFilterExpanded }
              .testTag("toggle_filters_button"),
            contentColor = if (isFilterExpanded || filter.isFiltered) Color(0xFF14201A) else Color.White
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }
    }

    // Expandable Advanced Filter Panel
    AnimatedVisibility(visible = isFilterExpanded) {
      AdvancedFilterSection(
        filter = filter,
        onFilterChange = { filter = it },
        onReset = { filter = ProductFilter() }
      )
    }

    // Active Filters Strip / Quick Category Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF1F6F3))
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (filter.isFiltered) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFFE2E8F0),
          modifier = Modifier.clickable { filter = ProductFilter() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Clear All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      FilterChip(
        selected = filter.category == null,
        onClick = { filter = filter.copy(category = null) },
        label = { Text("All 94+ Categories") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = VedashilaPrimaryGreen,
          selectedLabelColor = Color.White
        )
      )

      InventoryRepository.categories.forEach { cat ->
        FilterChip(
          selected = filter.category == cat.name,
          onClick = {
            filter = filter.copy(category = if (filter.category == cat.name) null else cat.name)
          },
          label = { Text("${cat.icon} ${cat.name} (${cat.productCount})") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = VedashilaPrimaryGreen,
            selectedLabelColor = Color.White
          )
        )
      }
    }

    // Products List
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Showing ${filteredProducts.size} Products",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF33463E)
          )
          if (filter.marketableOnly) {
            Text(
              text = "🛡️ Out-of-Stock Filtered Out",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF165B46)
            )
          }
        }
      }

      if (filteredProducts.isEmpty()) {
        item {
          EmptySearchResults(onResetFilters = { filter = ProductFilter() })
        }
      } else {
        items(filteredProducts, key = { it.id }) { product ->
          ProductCard(
            product = product,
            onBuyNow = { onSelectProductForStore(product.productUrl) },
            onCreatePoster = {
              if (product.isMarketable) {
                onSelectProductForPoster(product)
              } else {
                Toast.makeText(
                  context,
                  "⚠️ Cannot market out-of-stock item (${product.title}) to prevent advertising unavailable stock.",
                  Toast.LENGTH_LONG
                ).show()
              }
            },
            onCreateVideoAd = {
              if (product.isMarketable) {
                onSelectProductForVideo(product)
              } else {
                Toast.makeText(
                  context,
                  "⚠️ Product is currently out-of-stock. Restock on website before running video ads.",
                  Toast.LENGTH_LONG
                ).show()
              }
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }
}

@Composable
private fun AdvancedFilterSection(
  filter: ProductFilter,
  onFilterChange: (ProductFilter) -> Unit,
  onReset: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .border(1.dp, VedashilaBorder, RoundedCornerShape(14.dp))
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Advanced Search & Attribute Filters",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = VedashilaPrimaryGreen
        )
        Text(
          text = "Reset All",
          color = Color(0xFFB34A26),
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          modifier = Modifier.clickable(onClick = onReset)
        )
      }

      // Out-of-Stock Marketing Prevention Switch
      Surface(
        color = Color(0xFFF1F7F4),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Prevent Marketing of Out-of-Stock Items",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = VedashilaPrimaryGreen
            )
            Text(
              text = "Hides zero-stock items from marketing creator",
              fontSize = 10.sp,
              color = Color(0xFF5D6B63)
            )
          }
          Switch(
            checked = filter.marketableOnly,
            onCheckedChange = { onFilterChange(filter.copy(marketableOnly = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = VedashilaGold, checkedTrackColor = VedashilaPrimaryGreen)
          )
        }
      }

      // Price Range Filter
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Price Range:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          Text(
            text = "₹${filter.minPrice} - ₹${filter.maxPrice}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = VedashilaPrimaryGreen
          )
        }
        var sliderRange by remember(filter.minPrice, filter.maxPrice) {
          mutableStateOf(filter.minPrice.toFloat()..filter.maxPrice.toFloat())
        }
        RangeSlider(
          value = sliderRange,
          onValueChange = { range ->
            sliderRange = range
            onFilterChange(filter.copy(minPrice = range.start.toInt(), maxPrice = range.endInclusive.toInt()))
          },
          valueRange = 0f..3000f,
          steps = 15,
          colors = SliderDefaults.colors(thumbColor = VedashilaGold, activeTrackColor = VedashilaPrimaryGreen)
        )
      }

      // Dosage Form Filter
      Column {
        Text(text = "Dosage / Ayurvedic Form:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Resin", "Capsules", "Churna", "Oil", "Elixir").forEach { form ->
            FilterChip(
              selected = filter.dosageForm == form,
              onClick = {
                onFilterChange(filter.copy(dosageForm = if (filter.dosageForm == form) null else form))
              },
              label = { Text(form, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VedashilaPrimaryGreen,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      // Health Goal / Benefit Filter
      Column {
        Text(text = "Ayurvedic Health Goal:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Vitality & Ojas", "Skin Glow", "Immunity", "Stress & Sleep", "Digestion").forEach { goal ->
            FilterChip(
              selected = filter.healthGoal == goal,
              onClick = {
                onFilterChange(filter.copy(healthGoal = if (filter.healthGoal == goal) null else goal))
              },
              label = { Text(goal, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VedashilaPrimaryGreen,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      // Sort Order
      Column {
        Text(text = "Sort Results By:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          SortOption.values().forEach { sort ->
            FilterChip(
              selected = filter.sortBy == sort,
              onClick = { onFilterChange(filter.copy(sortBy = sort)) },
              label = { Text(sort.displayName, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VedashilaGold,
                selectedLabelColor = Color(0xFF14201A)
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProductCard(
  product: Product,
  onBuyNow: () -> Unit,
  onCreatePoster: () -> Unit,
  onCreateVideoAd: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, if (!product.inStock) Color(0xFFFFCDD2) else VedashilaBorder, RoundedCornerShape(16.dp))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Product Image
        Box(
          modifier = Modifier
            .size(105.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F7F7)),
          contentAlignment = Alignment.Center
        ) {
          if (product.localDrawableRes != null) {
            Image(
              painter = painterResource(id = product.localDrawableRes),
              contentDescription = product.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Icon(
              imageVector = Icons.Default.LocalOffer,
              contentDescription = null,
              tint = VedashilaGold,
              modifier = Modifier.size(32.dp)
            )
          }

          // Stock / Sale Tag
          Surface(
            shape = RoundedCornerShape(bottomEnd = 8.dp),
            color = if (!product.inStock) Color(0xFFD32F2F) else VedashilaGold,
            modifier = Modifier.align(Alignment.TopStart)
          ) {
            Text(
              text = product.tag,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (!product.inStock) Color.White else Color(0xFF14201A),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        // Product Details
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${product.category} • ${product.dosageForm}",
            fontSize = 11.sp,
            color = VedashilaPrimaryGreen,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = product.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Color(0xFF1A2621)
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Price & Discount
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "₹${product.price}",
              fontSize = 17.sp,
              fontWeight = FontWeight.ExtraBold,
              color = VedashilaPrimaryGreen
            )
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
              color = Color(0xFFB34A26)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Real-time Stock Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = when {
                !product.inStock -> Color(0xFFFFEBEE)
                product.stockQuantity < 20 -> Color(0xFFFFF3E0)
                else -> Color(0xFFE8F5E9)
              }
            ) {
              Text(
                text = when {
                  !product.inStock -> "Out of Stock"
                  product.stockQuantity < 20 -> "Low Stock: ${product.stockQuantity} left"
                  else -> "In Stock: ${product.stockQuantity} units"
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = when {
                  !product.inStock -> Color(0xFFC62828)
                  product.stockQuantity < 20 -> Color(0xFFE65100)
                  else -> Color(0xFF2E7D32)
                },
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "SKU: ${product.sku}",
              fontSize = 10.sp,
              color = Color.Gray
            )
          }
        }
      }

      // Benefits preview
      Spacer(modifier = Modifier.height(8.dp))
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF9FBF9), RoundedCornerShape(8.dp))
          .padding(8.dp)
      ) {
        product.benefits.take(2).forEach { benefit ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 1.dp)
          ) {
            Text("✓ ", color = VedashilaGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
              text = benefit,
              fontSize = 11.sp,
              color = Color(0xFF33463E),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onBuyNow,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
          modifier = Modifier
            .weight(1.2f)
            .height(38.dp)
            .testTag("buy_on_website_${product.id}")
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = VedashilaGold,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Buy on Website", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onCreatePoster,
          enabled = product.isMarketable,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (product.isMarketable) VedashilaPrimaryGreen else Color.Gray
          ),
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("make_poster_${product.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(if (product.isMarketable) "Poster" else "No Stock", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
          onClick = onCreateVideoAd,
          enabled = product.isMarketable,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (product.isMarketable) Color(0xFFB34A26) else Color.Gray
          ),
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("make_video_${product.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(if (product.isMarketable) "Video Ad" else "Disabled", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
private fun EmptySearchResults(onResetFilters: () -> Unit) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = "🔍", fontSize = 36.sp)
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "No matching products found",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = VedashilaPrimaryGreen
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Try adjusting your keywords, price slider, or reset filters to explore all 10,000+ items.",
        fontSize = 12.sp,
        color = Color.Gray,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onResetFilters,
        colors = ButtonDefaults.buttonColors(containerColor = VedashilaPrimaryGreen),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Reset All Filters")
      }
    }
  }
}
