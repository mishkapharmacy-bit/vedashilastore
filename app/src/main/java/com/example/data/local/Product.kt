package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing a product in the local cache for offline-first catalog access.
 * Includes fields for id, name, category, price, stockStatus, and remoteUrl for imagery,
 * along with supporting fields for Vedashila's 10,000+ catalog.
 */
@Entity(
  tableName = "products",
  indices = [
    Index(value = ["category"]),
    Index(value = ["price"]),
    Index(value = ["stockStatus"])
  ]
)
data class Product(
  @PrimaryKey
  val id: String,
  val name: String,
  val category: String,
  val price: Double,
  val stockStatus: String = "IN_STOCK", // e.g. "IN_STOCK", "OUT_OF_STOCK", "LOW_STOCK"
  val remoteUrl: String = "", // Remote URL for product imagery from https://vedashila.com
  val sku: String = "",
  val originalPrice: Double = price,
  val rating: Float = 4.8f,
  val reviewsCount: Int = 120,
  val stockQuantity: Int = 50,
  val productUrl: String = "https://vedashila.com",
  val isFavorite: Boolean = false
)
