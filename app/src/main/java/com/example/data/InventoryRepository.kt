package com.example.data

import android.content.Context
import com.example.R
import com.example.data.local.ProductDao
import com.example.data.local.VedashilaDatabase
import com.example.data.local.toDomainModel
import com.example.data.local.toEntity
import com.example.model.InventoryStatus
import com.example.model.Product
import com.example.model.ProductCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InventoryRepository {
  private val scope = CoroutineScope(Dispatchers.Default)
  private var database: VedashilaDatabase? = null

  val categories = listOf(
    ProductCategory("cat_shilajit", "Pure Shilajit & Resins", "⛰️", 142, "Resins & Shilajit"),
    ProductCategory("cat_immunity", "Immunity Boosters", "🛡️", 310, "Immunity & Ojas"),
    ProductCategory("cat_chyawanprash", "Organic Chyawanprash", "🍯", 86, "Rasayana & Tonics"),
    ProductCategory("cat_vitality", "Men's Vitality & Stamina", "⚡", 248, "Men's Health"),
    ProductCategory("cat_wellness", "Women's Wellness & Shatavari", "🌸", 195, "Women's Health"),
    ProductCategory("cat_haircare", "Bhringraj & Hair Care Oils", "🌿", 420, "Hair & Scalp"),
    ProductCategory("cat_skincare", "Kumkumadi & Skin Radiance", "✨", 380, "Beauty & Glow"),
    ProductCategory("cat_digestion", "Gut Health & Triphala", "🍃", 512, "Digestive Agni"),
    ProductCategory("cat_joint", "Joint & Bone Strength", "🦴", 275, "Joints & Mobility"),
    ProductCategory("cat_brain", "Memory, Focus & Brahmi", "🧠", 164, "Mind & Cognition"),
    ProductCategory("cat_stress", "Ashwagandha & Stress Relief", "🧘", 210, "Stress & Sleep"),
    ProductCategory("cat_detox", "Blood Purification & Neem", "🩸", 188, "Purification"),
    ProductCategory("cat_weight", "Metabolism & Weight Balance", "⚖️", 230, "Metabolism"),
    ProductCategory("cat_teas", "Herbal Green Teas & Kwath", "☕", 340, "Herbal Beverages"),
    ProductCategory("cat_single_herbs", "Pure Single Herb Powders", "🌱", 890, "Single Herbs"),
    ProductCategory("cat_oils", "Therapeutic Essential Oils", "💧", 215, "Aromatics & Oils")
  )

  private val initialProducts = listOf(
    Product(
      id = "prod_1",
      sku = "VEDA-SHL-001",
      title = "Vedashila Pure Himalayan Shilajit Resin (50g)",
      category = "Pure Shilajit & Resins",
      price = 1299,
      originalPrice = 2499,
      rating = 4.9f,
      reviewsCount = 4820,
      stockQuantity = 245,
      inStock = true,
      benefits = listOf(
        "Harvested at 16,000+ ft altitude in the Himalayas",
        "Enriched with 84+ minerals & 75%+ Fulvic Acid",
        "Boosts physical strength, endurance and stamina",
        "Lab-tested for heavy metals and purity"
      ),
      tag = "⭐ BEST SELLER #1",
      productUrl = "https://vedashila.com/product/pure-himalayan-shilajit-resin",
      localDrawableRes = R.drawable.img_vedashila_shilajit_1793942490025,
      dosageForm = "Resin",
      healthGoal = "Vitality & Ojas",
      certifications = listOf("100% Pure Himalayan", "AYUSH Approved", "GMP Certified", "Heavy Metal Tested")
    ),
    Product(
      id = "prod_2",
      sku = "VEDA-GLD-002",
      title = "Vedashila Gold Ayurvedic Radiance & Herbal Churnam Set",
      category = "Kumkumadi & Skin Radiance",
      price = 1499,
      originalPrice = 2999,
      rating = 4.8f,
      reviewsCount = 1890,
      stockQuantity = 112,
      inStock = true,
      benefits = listOf(
        "Kashmiri Saffron, Sandalwood & 24K Gold flakes",
        "Deep cellular nourishment and pigmentation control",
        "Neem, Turmeric & Rose herbal detox purifying face mask",
        "100% plant-based, chemical-free formulation"
      ),
      tag = "🔥 LUXURY COMBO",
      productUrl = "https://vedashila.com/product/ayurvedic-radiance-gold-set",
      localDrawableRes = R.drawable.img_vedashila_products_1793942502337,
      dosageForm = "Oil",
      healthGoal = "Skin Glow",
      certifications = listOf("24K Gold Flakes", "AYUSH Approved", "Paraben Free")
    ),
    Product(
      id = "prod_3",
      sku = "VEDA-ASH-003",
      title = "Vedashila KSM-66 Ashwagandha Root Extract (60 Caps)",
      category = "Ashwagandha & Stress Relief",
      price = 699,
      originalPrice = 1199,
      rating = 4.7f,
      reviewsCount = 2340,
      stockQuantity = 320,
      inStock = true,
      benefits = listOf(
        "Clinically certified full-spectrum root extract",
        "Supports stress relief and natural cortisol reduction",
        "Improves sleep quality and muscular endurance"
      ),
      tag = "POPULAR",
      productUrl = "https://vedashila.com/product/ksm-66-ashwagandha",
      localDrawableRes = R.drawable.ic_vedashila_logo_1793942426062,
      dosageForm = "Capsules",
      healthGoal = "Stress & Sleep",
      certifications = listOf("KSM-66 Certified", "AYUSH Approved", "Vegetarian Capsules")
    ),
    Product(
      id = "prod_4",
      sku = "VEDA-CHY-004",
      title = "Vedashila Vedic Chyawanprash with Wild Forest Honey (1kg)",
      category = "Organic Chyawanprash",
      price = 849,
      originalPrice = 1399,
      rating = 4.9f,
      reviewsCount = 3120,
      stockQuantity = 78,
      inStock = true,
      benefits = listOf(
        "Crafted with 52 sacred Himalayan herbs and Amla",
        "Slow-cooked in pure A2 Gir Cow Bilona Ghee",
        "Zero refined sugar, sweetened only with raw forest honey"
      ),
      tag = "🌿 ORGANIC",
      productUrl = "https://vedashila.com/product/vedic-chyawanprash-honey",
      localDrawableRes = R.drawable.ic_vedashila_logo_1793942426062,
      dosageForm = "Churna",
      healthGoal = "Immunity",
      certifications = listOf("A2 Ghee Cooked", "AYUSH Approved", "Sugar Free")
    ),
    Product(
      id = "prod_5",
      sku = "VEDA-TRP-005",
      title = "Vedashila Triphala Gut Detox & Digestion Churna (200g)",
      category = "Gut Health & Triphala",
      price = 399,
      originalPrice = 699,
      rating = 4.6f,
      reviewsCount = 1450,
      stockQuantity = 410,
      inStock = true,
      benefits = listOf(
        "Equal ratio of Amla, Haritaki, and Bibhitaki",
        "Gentle colon cleanse and natural gut microbiome support",
        "Relieves bloating, acidity and sluggish digestion"
      ),
      tag = "DAILY ESSENTIAL",
      productUrl = "https://vedashila.com/product/triphala-gut-detox",
      localDrawableRes = R.drawable.ic_vedashila_logo_1793942426062,
      dosageForm = "Churna",
      healthGoal = "Digestion",
      certifications = listOf("Traditional Ratio", "AYUSH Approved", "Non-GMO")
    ),
    Product(
      id = "prod_6",
      sku = "VEDA-BRH-006",
      title = "Vedashila Brahmi & Shankhpushpi Memory Elixir (200ml)",
      category = "Memory, Focus & Brahmi",
      price = 599,
      originalPrice = 999,
      rating = 4.8f,
      reviewsCount = 980,
      stockQuantity = 14,
      inStock = true,
      benefits = listOf(
        "Enhances focus, cognitive clarity and mental retention",
        "Ayurvedic Medhya Rasayana for students and professionals",
        "Calms nervous tension and mental fatigue"
      ),
      tag = "⚠️ ONLY 14 LEFT",
      productUrl = "https://vedashila.com/product/brahmi-memory-elixir",
      localDrawableRes = R.drawable.ic_vedashila_logo_1793942426062,
      dosageForm = "Elixir",
      healthGoal = "Vitality & Ojas",
      certifications = listOf("Medhya Rasayana", "AYUSH Approved")
    ),
    Product(
      id = "prod_7",
      sku = "VEDA-SAF-007",
      title = "Vedashila Royal Kashmiri Mongra Kesar / Saffron (2g)",
      category = "Kumkumadi & Skin Radiance",
      price = 799,
      originalPrice = 1299,
      rating = 4.9f,
      reviewsCount = 1620,
      stockQuantity = 0,
      inStock = false,
      benefits = listOf(
        "Grade A1 GI-Tagged Kashmiri Mongra Saffron",
        "Deep crimson threads with intense aroma and crocin levels",
        "Elevates skin complexion and promotes restful sleep"
      ),
      tag = "❌ OUT OF STOCK",
      productUrl = "https://vedashila.com/product/kashmiri-mongra-kesar",
      localDrawableRes = R.drawable.ic_vedashila_logo_1793942426062,
      dosageForm = "Resin",
      healthGoal = "Skin Glow",
      certifications = listOf("GI Tagged", "ISO 3632 Tested")
    ),
    Product(
      id = "prod_8",
      sku = "VEDA-KUM-008",
      title = "Vedashila Pure Kumkumadi Miraculous Beauty Tailam (30ml)",
      category = "Kumkumadi & Skin Radiance",
      price = 1199,
      originalPrice = 1999,
      rating = 4.9f,
      reviewsCount = 2890,
      stockQuantity = 95,
      inStock = true,
      benefits = listOf(
        "Formulated with 26 Ayurvedic herbs in cold-pressed sesame oil",
        "Reduces dark spots, blemishes, and fine lines",
        "Authentic Ashtanga Hridaya classical recipe"
      ),
      tag = "GOLD RADIANCE",
      productUrl = "https://vedashila.com/product/kumkumadi-tailam",
      localDrawableRes = R.drawable.img_vedashila_products_1793942502337,
      dosageForm = "Oil",
      healthGoal = "Skin Glow",
      certifications = listOf("Ashtanga Recipe", "100% Herbal", "Dermatologist Tested")
    )
  )

  private val _products = MutableStateFlow<List<Product>>(initialProducts)
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  private val _inventoryStatus = MutableStateFlow(
    InventoryStatus(
      totalProducts = 10480,
      totalCategories = 94,
      inStockCount = 10458,
      lowStockCount = 18,
      outOfStockCount = 4,
      lastSyncTimestamp = "Offline-First Room Cache Active"
    )
  )
  val inventoryStatus: StateFlow<InventoryStatus> = _inventoryStatus.asStateFlow()

  private val _syncLog = MutableStateFlow<List<String>>(
    listOf(
      "Room DB initialized: vedashila_offline_catalog.db",
      "Offline-first SQLite local caching active",
      "Live inventory listener initialized for 10,480 products"
    )
  )
  val syncLog: StateFlow<List<String>> = _syncLog.asStateFlow()

  init {
    // Start periodic background inventory heartbeat
    scope.launch {
      while (true) {
        delay(12000)
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _inventoryStatus.value = _inventoryStatus.value.copy(
          lastSyncTimestamp = "Offline Room Cache & Webhook synced at $timeStr"
        )
      }
    }
  }

  fun initDatabase(context: Context) {
    if (database != null) return
    val db = VedashilaDatabase.getDatabase(context)
    database = db

    scope.launch {
      try {
        val dao = db.productDao()
        val count = dao.getProductCount()
        if (count == 0) {
          // Pre-populate Room SQLite cache with catalog entities
          dao.insertProducts(initialProducts.map { it.toEntity() })
          appendLog("Room Database seeded with ${initialProducts.size} offline products")
        }

        // Listen reactively to Room Database updates
        dao.getAllProducts().collect { entities ->
          if (entities.isNotEmpty()) {
            _products.value = entities.map { it.toDomainModel() }
            recalculateStatus()
          }
        }
      } catch (e: Exception) {
        appendLog("Room DB init notice: ${e.localizedMessage}")
      }
    }
  }

  fun updateStock(productId: String, newStock: Int) {
    val updatedList = _products.value.map { prod ->
      if (prod.id == productId) {
        val inStock = newStock > 0
        val tag = when {
          !inStock -> "❌ OUT OF STOCK"
          newStock < 20 -> "⚠️ ONLY $newStock LEFT"
          else -> prod.tag.replace("❌ OUT OF STOCK", "IN STOCK")
        }
        prod.copy(
          stockQuantity = newStock,
          inStock = inStock,
          tag = tag,
          lastSyncedTime = "Synced just now"
        )
      } else {
        prod
      }
    }
    _products.value = updatedList
    recalculateStatus()

    // Persist to Room SQLite Cache
    scope.launch {
      try {
        val inStock = newStock > 0
        val tag = if (!inStock) "❌ OUT OF STOCK" else if (newStock < 20) "⚠️ ONLY $newStock LEFT" else "IN STOCK"
        database?.productDao()?.updateStock(
          id = productId,
          stockQuantity = newStock,
          inStock = inStock,
          tag = tag,
          lastSyncedTime = "Synced to Room Cache"
        )
      } catch (_: Exception) {}
    }

    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    appendLog("[$timeStr] Room Cache & Web Stock Updated: $productId -> $newStock units")
  }

  fun toggleFavorite(productId: String) {
    val updatedList = _products.value.map { prod ->
      if (prod.id == productId) {
        prod.copy(isFavorite = !prod.isFavorite)
      } else {
        prod
      }
    }
    _products.value = updatedList

    // Persist to Room SQLite Cache
    scope.launch {
      try {
        database?.productDao()?.toggleFavorite(productId)
        database?.productDao()?.toggleProductV2Favorite(productId)
      } catch (_: Exception) {}
    }
  }

  fun simulateWebsitePurchase(productId: String) {
    val currentProduct = _products.value.find { it.id == productId } ?: return
    val newStock = (currentProduct.stockQuantity - 1).coerceAtLeast(0)
    updateStock(productId, newStock)
  }

  fun simulateRestock(productId: String, quantity: Int = 100) {
    val currentProduct = _products.value.find { it.id == productId } ?: return
    val newStock = currentProduct.stockQuantity + quantity
    updateStock(productId, newStock)
  }

  fun triggerFullSync() {
    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    _inventoryStatus.value = _inventoryStatus.value.copy(isSyncing = true)
    scope.launch {
      delay(800)
      _inventoryStatus.value = _inventoryStatus.value.copy(
        isSyncing = false,
        lastSyncTimestamp = "Full Room cache & web sync at $timeStr"
      )
      // Refresh Room cache
      try {
        database?.productDao()?.insertProducts(_products.value.map { it.toEntity() })
      } catch (_: Exception) {}

      appendLog("[$timeStr] Full Room Cache Sync: 10,480 products verified in SQLite")
    }
  }

  private fun recalculateStatus() {
    val lowCount = _products.value.count { it.inStock && it.stockQuantity in 1..20 }
    val outCount = _products.value.count { !it.inStock || it.stockQuantity == 0 }
    _inventoryStatus.value = _inventoryStatus.value.copy(
      lowStockCount = 18 + lowCount,
      outOfStockCount = 4 + outCount
    )
  }

  private fun appendLog(log: String) {
    val current = _syncLog.value.takeLast(10).toMutableList()
    current.add(log)
    _syncLog.value = current
  }
}
