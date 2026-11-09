package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.InventoryRepository
import com.example.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Service managing real-time inventory monitoring and local push notifications
 * when product stock falls below the user-defined threshold.
 */
object InventoryAlertService {

  private const val CHANNEL_ID = "vedashila_low_stock_channel"
  private const val CHANNEL_NAME = "Vedashila Low Stock Alerts"
  private const val PREFS_NAME = "vedashila_inventory_alerts"
  private const val KEY_THRESHOLD = "low_stock_threshold"
  private const val KEY_ALERTS_ENABLED = "low_stock_alerts_enabled"

  const val DEFAULT_THRESHOLD = 20

  private val serviceScope = CoroutineScope(Dispatchers.Default)
  private var monitorJob: Job? = null
  private val alertedProductIds = mutableSetOf<String>()

  private val _threshold = MutableStateFlow(DEFAULT_THRESHOLD)
  val threshold: StateFlow<Int> = _threshold.asStateFlow()

  private val _alertsEnabled = MutableStateFlow(true)
  val alertsEnabled: StateFlow<Boolean> = _alertsEnabled.asStateFlow()

  private val _lowStockCount = MutableStateFlow(0)
  val lowStockCount: StateFlow<Int> = _lowStockCount.asStateFlow()

  fun init(context: Context) {
    createNotificationChannel(context)
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    _threshold.value = prefs.getInt(KEY_THRESHOLD, DEFAULT_THRESHOLD)
    _alertsEnabled.value = prefs.getBoolean(KEY_ALERTS_ENABLED, true)

    startMonitoring(context)
  }

  fun updateThreshold(context: Context, newThreshold: Int) {
    _threshold.value = newThreshold.coerceIn(1, 200)
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .putInt(KEY_THRESHOLD, _threshold.value)
      .apply()
    
    // Check current products against new threshold
    checkInventoryLevels(context, InventoryRepository.products.value)
  }

  fun setAlertsEnabled(context: Context, enabled: Boolean) {
    _alertsEnabled.value = enabled
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      .edit()
      .putBoolean(KEY_ALERTS_ENABLED, enabled)
      .apply()
  }

  fun isProductLowStock(product: Product): Boolean {
    val currentThreshold = _threshold.value
    return product.inStock && product.stockQuantity in 1..currentThreshold
  }

  fun checkInventoryLevels(context: Context, products: List<Product>) {
    val currentThreshold = _threshold.value
    val lowStockItems = products.filter { it.inStock && it.stockQuantity in 1..currentThreshold }
    _lowStockCount.value = lowStockItems.size

    if (!_alertsEnabled.value) return

    lowStockItems.forEach { product ->
      // Trigger notification if not already alerted for this stock level
      val alertKey = "${product.id}_${product.stockQuantity}"
      if (!alertedProductIds.contains(alertKey)) {
        alertedProductIds.add(alertKey)
        sendLowStockNotification(context, product, currentThreshold)
      }
    }
  }

  private fun startMonitoring(context: Context) {
    monitorJob?.cancel()
    monitorJob = serviceScope.launch {
      // Listen to repository product updates continuously
      InventoryRepository.products.collect { productList ->
        checkInventoryLevels(context, productList)
      }
    }
  }

  private fun sendLowStockNotification(context: Context, product: Product, threshold: Int) {
    try {
      val notificationManager = NotificationManagerCompat.from(context)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
          ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
          return
        }
      }

      val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      }

      val pendingIntent = PendingIntent.getActivity(
        context,
        product.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("⚠️ Low Stock Alert: ${product.title.take(28)}...")
        .setContentText("Only ${product.stockQuantity} units remaining (Threshold: $threshold). Tap to view and restock.")
        .setStyle(
          NotificationCompat.BigTextStyle()
            .bigText("Urgent inventory notice for SKU ${product.sku}:\n${product.title} has dropped to ${product.stockQuantity} units left. Restock or pause marketing campaigns to prevent out-of-stock orders.")
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

      notificationManager.notify(product.id.hashCode(), notification)
    } catch (_: Exception) {
      // Graceful fallback if notifications are blocked by system or permissions
    }
  }

  private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = "Alerts when Vedashila Ayurvedic product inventory falls below user threshold"
        enableVibration(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      notificationManager?.createNotificationChannel(channel)
    }
  }
}
