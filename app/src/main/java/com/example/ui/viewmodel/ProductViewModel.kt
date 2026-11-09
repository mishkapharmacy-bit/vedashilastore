package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProductRepository
import com.example.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI State for the Product List Screen
 */
sealed interface ProductUiState {
  data object Loading : ProductUiState
  data class Success(
    val products: List<Product>,
    val totalCount: Int,
    val inStockCount: Int,
    val activeFilterQuery: String = "",
    val selectedCategory: String? = null,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null
  ) : ProductUiState
  data class Error(val message: String) : ProductUiState
}

/**
 * ProductViewModel observes the ProductRepository and manages UI state for product browsing,
 * real-time inventory synchronization, and keyword filtering.
 */
class ProductViewModel(
  application: Application,
  private val repository: ProductRepository = ProductRepository.getInstance(application)
) : AndroidViewModel(application) {

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow<String?>(null)
  val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>("Local Room cache active")
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  private val _selectedProduct = MutableStateFlow<Product?>(null)
  val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

  private val _isLoadingDetail = MutableStateFlow(false)
  val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()

  // Primary reactive flow combining Room database observations with user filters
  val uiState: StateFlow<ProductUiState> = combine(
    repository.observeMarketableProducts(),
    _searchQuery,
    _selectedCategory,
    _isSyncing,
    _statusMessage
  ) { products, query, category, syncing, message ->
    val filtered = products.filter { product ->
      val trimmedQuery = query.trim()
      val matchesQuery = trimmedQuery.isBlank() ||
        product.title.contains(trimmedQuery, ignoreCase = true) ||
        product.sku.contains(trimmedQuery, ignoreCase = true) ||
        product.category.contains(trimmedQuery, ignoreCase = true) ||
        product.benefits.any { it.contains(trimmedQuery, ignoreCase = true) }

      val matchesCategory = category == null || product.category == category

      matchesQuery && matchesCategory
    }

    ProductUiState.Success(
      products = filtered,
      totalCount = products.size,
      inStockCount = products.count { it.inStock && it.stockQuantity > 0 },
      activeFilterQuery = query,
      selectedCategory = category,
      isSyncing = syncing,
      syncMessage = message
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ProductUiState.Loading
  )

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun selectCategory(category: String?) {
    _selectedCategory.value = if (_selectedCategory.value == category) null else category
  }

  /**
   * Triggers real-time synchronization between the Room database cache and remote website API.
   */
  fun syncInventory() {
    viewModelScope.launch {
      _isSyncing.value = true
      _statusMessage.value = "Syncing with vedashila.com..."
      val result = repository.syncRemoteInventory()
      _isSyncing.value = false
      if (result.isSuccess) {
        _statusMessage.value = "Synced ${result.getOrNull() ?: 0} products from Room cache"
      } else {
        _statusMessage.value = "Sync error: ${result.exceptionOrNull()?.localizedMessage}"
      }
    }
  }

  /**
   * Fetches extended product information from the repository and Room database.
   */
  fun selectProductById(productId: String) {
    viewModelScope.launch {
      _isLoadingDetail.value = true
      val product = repository.getProductById(productId)
      _selectedProduct.value = product
      _isLoadingDetail.value = false
    }
  }

  fun clearSelectedProduct() {
    _selectedProduct.value = null
  }

  /**
   * Toggles the user's favorite preference and persists it in the Room database.
   */
  fun toggleFavorite(productId: String) {
    viewModelScope.launch {
      repository.toggleFavorite(productId)
      // If the currently selected detail product was toggled, update its state
      if (_selectedProduct.value?.id == productId) {
        _selectedProduct.value = _selectedProduct.value?.let { it.copy(isFavorite = !it.isFavorite) }
      }
    }
  }

  /**
   * Updates real-time stock directly for instant testing or webhook callbacks.
   */
  fun updateStock(productId: String, newStock: Int) {
    viewModelScope.launch {
      repository.updateInventory(productId, newStock)
    }
  }
}
