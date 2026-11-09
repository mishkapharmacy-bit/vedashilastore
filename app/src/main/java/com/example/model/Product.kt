package com.example.model

data class Product(
  val id: String,
  val sku: String,
  val title: String,
  val category: String,
  val price: Int,
  val originalPrice: Int,
  val rating: Float,
  val reviewsCount: Int,
  val stockQuantity: Int,
  val inStock: Boolean,
  val benefits: List<String>,
  val tag: String,
  val productUrl: String,
  val localDrawableRes: Int? = null,
  val dosageForm: String = "Resin", // Resin, Capsules, Churna, Oil, Elixir
  val healthGoal: String = "Vitality & Ojas", // Vitality & Ojas, Skin Glow, Immunity, Stress & Sleep, Digestion
  val certifications: List<String> = listOf("100% Pure Himalayan", "AYUSH Approved", "GMP Certified"),
  val lastSyncedTime: String = "Live",
  val isFavorite: Boolean = false
) {
  val isMarketable: Boolean
    get() = inStock && stockQuantity > 0

  val discountPercent: Int
    get() = if (originalPrice > price) ((originalPrice - price) * 100) / originalPrice else 0
}

data class ProductCategory(
  val id: String,
  val name: String,
  val icon: String,
  val productCount: Int,
  val group: String = "Ayurvedic Classical"
)

enum class SortOption(val displayName: String) {
  RELEVANCE("Best Match"),
  PRICE_LOW_HIGH("Price: Low to High"),
  PRICE_HIGH_LOW("Price: High to Low"),
  STOCK_HIGH_LOW("Stock: Highest First"),
  RATING("Highest Rated")
}

data class ProductFilter(
  val query: String = "",
  val category: String? = null,
  val minPrice: Int = 0,
  val maxPrice: Int = 3000,
  val inStockOnly: Boolean = false,
  val marketableOnly: Boolean = true, // Excludes out of stock to prevent marketing unavailable items
  val dosageForm: String? = null,
  val healthGoal: String? = null,
  val sortBy: SortOption = SortOption.RELEVANCE
) {
  val isFiltered: Boolean
    get() = query.isNotBlank() || category != null || minPrice > 0 || maxPrice < 3000 || inStockOnly || dosageForm != null || healthGoal != null || sortBy != SortOption.RELEVANCE
}

data class InventoryStatus(
  val totalProducts: Int = 10480,
  val totalCategories: Int = 94,
  val inStockCount: Int = 10458,
  val lowStockCount: Int = 18,
  val outOfStockCount: Int = 4,
  val lastSyncTimestamp: String = "Live Sync Active",
  val isSyncing: Boolean = false,
  val syncServerStatus: String = "Connected (https://vedashila.com)",
  val activeWebhooks: Int = 3
)
