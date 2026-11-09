package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.Product as RoomProduct
import com.example.data.local.ProductDao
import com.example.model.Product as DomainProduct
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ProductRepository abstracts data access, providing a clean API for the UI
 * to interact with both the Room database and remote API for real-time inventory updates.
 * Implements offline-first caching with background synchronization.
 */
class ProductRepository(
  private val productDao: ProductDao,
  private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

  /**
   * Observe all marketable products from the local Room database cache.
   * Emits updates automatically whenever the database is updated.
   */
  fun observeMarketableProducts(): Flow<List<DomainProduct>> {
    return InventoryRepository.products
  }

  /**
   * Observe raw RoomProduct entities from the local database.
   */
  fun observeLocalProducts(): Flow<List<RoomProduct>> {
    return productDao.getAllProductsV2()
  }

  /**
   * Observe in-stock RoomProduct entities from the local database.
   */
  fun observeInStockLocalProducts(): Flow<List<RoomProduct>> {
    return productDao.getInStockProductsV2()
  }

  /**
   * Get single product by ID from Room database.
   */
  suspend fun getProductById(id: String): DomainProduct? = withContext(ioDispatcher) {
    InventoryRepository.products.value.find { it.id == id }
  }

  /**
   * Update real-time inventory for a product locally and notify backend/listeners.
   * If stock reaches 0, the product is immediately marked out-of-stock and non-marketable.
   */
  suspend fun updateInventory(productId: String, newStockQuantity: Int): Result<Unit> = withContext(ioDispatcher) {
    try {
      InventoryRepository.updateStock(productId, newStockQuantity)
      val inStock = newStockQuantity > 0
      val status = if (inStock) "IN_STOCK" else "OUT_OF_STOCK"
      
      // Also update Room Product table if present
      val existingV2 = productDao.getProductV2ById(productId)
      if (existingV2 != null) {
        productDao.insertProductV2(
          existingV2.copy(
            stockQuantity = newStockQuantity,
            stockStatus = status
          )
        )
      }
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Toggles the user's favorite status for a product and persists to Room database.
   */
  suspend fun toggleFavorite(productId: String): Result<Unit> = withContext(ioDispatcher) {
    try {
      InventoryRepository.toggleFavorite(productId)
      productDao.toggleFavorite(productId)
      productDao.toggleProductV2Favorite(productId)
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Fetch and sync real-time inventory updates from https://vedashila.com remote API.
   * Stores fresh data in the local Room database to maintain an up-to-date offline cache.
   */
  suspend fun syncRemoteInventory(): Result<Int> = withContext(ioDispatcher) {
    try {
      InventoryRepository.triggerFullSync()
      val currentList = InventoryRepository.products.value
      
      // Update Room V2 products cache
      val roomProducts = currentList.map { p ->
        RoomProduct(
          id = p.id,
          name = p.title,
          category = p.category,
          price = p.price.toDouble(),
          stockStatus = if (p.inStock && p.stockQuantity > 0) "IN_STOCK" else "OUT_OF_STOCK",
          remoteUrl = p.productUrl,
          sku = p.sku,
          originalPrice = p.originalPrice.toDouble(),
          rating = p.rating,
          reviewsCount = p.reviewsCount,
          stockQuantity = p.stockQuantity,
          productUrl = p.productUrl,
          isFavorite = p.isFavorite
        )
      }
      productDao.insertProductsV2(roomProducts)
      Result.success(roomProducts.size)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Search cached products by keyword query across title, SKU, category, and benefits.
   */
  fun searchProducts(query: String): Flow<List<DomainProduct>> {
    return InventoryRepository.products.map { list ->
      if (query.isBlank()) {
        list
      } else {
        list.filter { product ->
          product.title.contains(query, ignoreCase = true) ||
            product.sku.contains(query, ignoreCase = true) ||
            product.category.contains(query, ignoreCase = true) ||
            product.benefits.any { it.contains(query, ignoreCase = true) }
        }
      }
    }
  }

  companion object {
    @Volatile
    private var INSTANCE: ProductRepository? = null

    /**
     * Singleton factory providing ProductRepository configured with AppDatabase.
     */
    fun getInstance(context: Context): ProductRepository {
      return INSTANCE ?: synchronized(this) {
        val database = AppDatabase.getDatabase(context)
        val instance = ProductRepository(database.productDao())
        INSTANCE = instance
        instance
      }
    }
  }
}
