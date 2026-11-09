package com.example

import com.example.data.AnalyticsRepository
import com.example.data.InventoryRepository
import com.example.data.SampleCatalog
import com.example.model.ProductFilter
import com.example.model.SocialPlatform
import com.example.data.local.toEntity
import com.example.data.local.toDomainModel
import com.example.service.SmartAiMarketingService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testSampleCatalogIntegrity() {
    assertTrue(SampleCatalog.products.isNotEmpty())
    assertTrue(SampleCatalog.categories.isNotEmpty())
    assertTrue(SampleCatalog.templateThemes.isNotEmpty())

    val firstProduct = SampleCatalog.products.first()
    assertEquals("VEDA-SHL-001", firstProduct.sku)
    assertTrue(firstProduct.inStock)
    assertTrue(firstProduct.isMarketable)
    assertTrue(firstProduct.price > 0)
    assertTrue(firstProduct.discountPercent > 0)
    assertTrue(firstProduct.productUrl.startsWith("https://vedashila.com"))
  }

  @Test
  fun testOutOfStockMarketingPrevention() {
    // prod_7 has stockQuantity = 0 and inStock = false
    val outOfStockProduct = SampleCatalog.products.find { it.id == "prod_7" }
    assertNotNull(outOfStockProduct)
    assertFalse(outOfStockProduct!!.inStock)
    assertFalse(outOfStockProduct.isMarketable) // Must be non-marketable!
  }

  @Test
  fun testRealTimeInventoryUpdate() {
    val initialStock = SampleCatalog.products.find { it.id == "prod_1" }!!.stockQuantity
    InventoryRepository.updateStock("prod_1", 10)
    val updatedProduct = SampleCatalog.products.find { it.id == "prod_1" }!!
    assertEquals(10, updatedProduct.stockQuantity)
    assertTrue(updatedProduct.tag.contains("10 LEFT"))

    // Set to zero stock to test marketing lockout
    InventoryRepository.updateStock("prod_1", 0)
    val zeroStockProd = SampleCatalog.products.find { it.id == "prod_1" }!!
    assertFalse(zeroStockProd.isMarketable)
    assertEquals("❌ OUT OF STOCK", zeroStockProd.tag)

    // Restore stock
    InventoryRepository.updateStock("prod_1", initialStock)
  }

  @Test
  fun testProductFilterFunctionality() {
    val filter = ProductFilter(minPrice = 500, maxPrice = 1000, marketableOnly = true)
    assertTrue(filter.isFiltered)
  }

  @Test
  fun testVideoAdProjectConfiguration() {
    val project = SampleCatalog.sampleVideoAdProject
    assertNotNull(project)
    assertEquals(5, project.scenes.size)
    assertEquals(SocialPlatform.YOUTUBE_AD, project.targetPlatform)
  }

  @Test
  fun testAnalyticsRepositoryAndRechartsGeneration() {
    val summary = AnalyticsRepository.summary
    assertNotNull(summary.totalRevenue)
    assertNotNull(summary.averageRoas)

    val topCampaigns = AnalyticsRepository.topCampaigns
    assertTrue(topCampaigns.isNotEmpty())
    assertTrue(topCampaigns.first().roas > 0)

    val traffic = AnalyticsRepository.trafficSources
    assertTrue(traffic.isNotEmpty())

    // Verify Recharts HTML generation
    val rechartsHtml = AnalyticsRepository.generateRechartsHtml("7d")
    assertTrue(rechartsHtml.contains("Recharts"))
    assertTrue(rechartsHtml.contains("AreaChart"))
    assertTrue(rechartsHtml.contains("BarChart"))
    assertTrue(rechartsHtml.contains("PieChart"))
  }

  @Test
  fun testSmartAiMarketingService() {
    val shilajit = SampleCatalog.products.first()
    val advice = SmartAiMarketingService.getSmartFallbackAdvice(shilajit)

    assertNotNull(advice.optimalTimeSlot)
    assertTrue(advice.optimalTimeSlot.contains("07:00") || advice.optimalTimeSlot.contains("AM"))
    assertTrue(advice.peakDays.isNotEmpty())
    assertTrue(advice.instagramCaption.contains("Shilajit"))
    assertTrue(advice.whatsappBroadcastText.contains("VEDASHILA"))
    assertTrue(advice.hashtags.isNotEmpty())
    assertTrue(advice.hashtags.any { it.contains("Shilajit") || it.contains("Vedashila") })
  }

  @Test
  fun testRoomEntityMappingFidelity() {
    val sample = SampleCatalog.products.first()
    val entity = sample.toEntity()

    // Verify entity fields
    assertEquals(sample.id, entity.id)
    assertEquals(sample.sku, entity.sku)
    assertEquals(sample.title, entity.title)
    assertEquals(sample.category, entity.category)
    assertEquals(sample.price, entity.price)
    assertEquals(sample.originalPrice, entity.originalPrice)
    assertEquals(sample.stockQuantity, entity.stockQuantity)
    assertEquals(sample.inStock, entity.inStock)
    assertEquals(sample.dosageForm, entity.dosageForm)
    assertEquals(sample.healthGoal, entity.healthGoal)
    assertEquals(sample.productUrl, entity.productUrl)
    assertEquals(sample.benefits.joinToString("||"), entity.benefitsJoined)

    // Test round-trip conversion back to domain Product
    val restoredProduct = entity.toDomainModel()
    assertEquals(sample.id, restoredProduct.id)
    assertEquals(sample.sku, restoredProduct.sku)
    assertEquals(sample.title, restoredProduct.title)
    assertEquals(sample.category, restoredProduct.category)
    assertEquals(sample.price, restoredProduct.price)
    assertEquals(sample.stockQuantity, restoredProduct.stockQuantity)
    assertEquals(sample.inStock, restoredProduct.inStock)
    assertEquals(sample.isMarketable, restoredProduct.isMarketable)
    assertEquals(sample.benefits.size, restoredProduct.benefits.size)
    assertEquals(sample.dosageForm, restoredProduct.dosageForm)
    assertEquals(sample.healthGoal, restoredProduct.healthGoal)
    assertEquals(sample.isFavorite, restoredProduct.isFavorite)
  }

  @Test
  fun testFavoriteToggleBehavior() {
    val sample = SampleCatalog.products.first()
    assertFalse(sample.isFavorite)
    val favorited = sample.copy(isFavorite = true)
    assertTrue(favorited.isFavorite)
    val entity = favorited.toEntity()
    assertTrue(entity.isFavorite)
    val restored = entity.toDomainModel()
    assertTrue(restored.isFavorite)
  }

  @Test
  fun testInventoryAlertServiceLowStockDetection() {
    val sampleHigh = SampleCatalog.products.first().copy(stockQuantity = 50, inStock = true)
    val sampleLow = SampleCatalog.products.first().copy(stockQuantity = 12, inStock = true)
    val sampleOut = SampleCatalog.products.first().copy(stockQuantity = 0, inStock = false)

    // Default threshold is 20
    assertFalse(com.example.service.InventoryAlertService.isProductLowStock(sampleHigh))
    assertTrue(com.example.service.InventoryAlertService.isProductLowStock(sampleLow))
    assertFalse(com.example.service.InventoryAlertService.isProductLowStock(sampleOut)) // Out of stock, not low stock
  }
}
