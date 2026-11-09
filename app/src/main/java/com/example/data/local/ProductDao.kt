package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

  @Query("SELECT * FROM cached_products ORDER BY rating DESC, stockQuantity DESC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM cached_products WHERE inStock = 1 AND stockQuantity > 0 ORDER BY rating DESC")
  fun getMarketableProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM cached_products WHERE category = :category ORDER BY price ASC")
  fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

  @Query(
    """
    SELECT * FROM cached_products 
    WHERE title LIKE '%' || :query || '%' 
       OR sku LIKE '%' || :query || '%' 
       OR category LIKE '%' || :query || '%'
       OR benefitsJoined LIKE '%' || :query || '%'
       OR healthGoal LIKE '%' || :query || '%'
    ORDER BY rating DESC
    """
  )
  fun searchProducts(query: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM cached_products WHERE id = :id LIMIT 1")
  suspend fun getProductById(id: String): ProductEntity?

  @Query("SELECT * FROM cached_products WHERE sku = :sku LIMIT 1")
  suspend fun getProductBySku(sku: String): ProductEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProducts(products: List<ProductEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity)

  @Query(
    """
    UPDATE cached_products 
    SET stockQuantity = :stockQuantity, 
        inStock = :inStock, 
        tag = :tag, 
        lastSyncedTime = :lastSyncedTime 
    WHERE id = :id
    """
  )
  suspend fun updateStock(
    id: String,
    stockQuantity: Int,
    inStock: Boolean,
    tag: String,
    lastSyncedTime: String
  )

  @Query("SELECT COUNT(*) FROM cached_products")
  suspend fun getProductCount(): Int

  @Query("UPDATE cached_products SET isFavorite = NOT isFavorite WHERE id = :id")
  suspend fun toggleFavorite(id: String)

  @Query("UPDATE cached_products SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun setFavorite(id: String, isFavorite: Boolean)

  @Query("DELETE FROM cached_products")
  suspend fun clearAll()

  // Queries for 'products' table (Product entity)
  @Query("SELECT * FROM products ORDER BY name ASC")
  fun getAllProductsV2(): Flow<List<Product>>

  @Query("SELECT * FROM products WHERE stockStatus = 'IN_STOCK' ORDER BY name ASC")
  fun getInStockProductsV2(): Flow<List<Product>>

  @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
  suspend fun getProductV2ById(id: String): Product?

  @Query("UPDATE products SET isFavorite = NOT isFavorite WHERE id = :id")
  suspend fun toggleProductV2Favorite(id: String)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProductsV2(products: List<Product>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProductV2(product: Product)
}
