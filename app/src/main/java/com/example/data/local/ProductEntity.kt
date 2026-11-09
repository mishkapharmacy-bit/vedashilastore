package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.model.Product

@Entity(
  tableName = "cached_products",
  indices = [
    Index(value = ["sku"], unique = true),
    Index(value = ["category"]),
    Index(value = ["price"]),
    Index(value = ["inStock"])
  ]
)
data class ProductEntity(
  @PrimaryKey val id: String,
  val sku: String,
  val title: String,
  val category: String,
  val price: Int,
  val originalPrice: Int,
  val rating: Float,
  val reviewsCount: Int,
  val stockQuantity: Int,
  val inStock: Boolean,
  val benefitsJoined: String,
  val tag: String,
  val productUrl: String,
  val localDrawableRes: Int? = null,
  val dosageForm: String = "Resin",
  val healthGoal: String = "Vitality & Ojas",
  val certificationsJoined: String = "100% Pure Himalayan,AYUSH Approved,GMP Certified",
  val lastSyncedTime: String = "Live Local Cache",
  val isFavorite: Boolean = false,
  val cachedAtTimestamp: Long = System.currentTimeMillis()
)

fun ProductEntity.toDomainModel(): Product {
  return Product(
    id = id,
    sku = sku,
    title = title,
    category = category,
    price = price,
    originalPrice = originalPrice,
    rating = rating,
    reviewsCount = reviewsCount,
    stockQuantity = stockQuantity,
    inStock = inStock,
    benefits = benefitsJoined.split("||").filter { it.isNotBlank() },
    tag = tag,
    productUrl = productUrl,
    localDrawableRes = localDrawableRes,
    dosageForm = dosageForm,
    healthGoal = healthGoal,
    certifications = certificationsJoined.split(",").map { it.trim() },
    lastSyncedTime = lastSyncedTime,
    isFavorite = isFavorite
  )
}

fun Product.toEntity(): ProductEntity {
  return ProductEntity(
    id = id,
    sku = sku,
    title = title,
    category = category,
    price = price,
    originalPrice = originalPrice,
    rating = rating,
    reviewsCount = reviewsCount,
    stockQuantity = stockQuantity,
    inStock = inStock,
    benefitsJoined = benefits.joinToString("||"),
    tag = tag,
    productUrl = productUrl,
    localDrawableRes = localDrawableRes,
    dosageForm = dosageForm,
    healthGoal = healthGoal,
    certificationsJoined = certifications.joinToString(","),
    lastSyncedTime = lastSyncedTime,
    isFavorite = isFavorite
  )
}
