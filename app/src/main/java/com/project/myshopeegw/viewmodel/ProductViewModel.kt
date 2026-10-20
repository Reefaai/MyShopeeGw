package com.project.myshopeegw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.myshopeegw.data.api.MockDataProvider
import com.project.myshopeegw.data.model.Category
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.data.model.Review
import com.project.myshopeegw.data.repository.ProductRepository
import com.project.myshopeegw.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products

    private val _selectedProduct = MutableLiveData<Resource<Product>>()
    val selectedProduct: LiveData<Resource<Product>> = _selectedProduct

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    var currentCategory: String = "all"
        private set
    var currentSearchQuery: String = ""
        private set

    private var loadJob: Job? = null

    init {
        _categories.value = MockDataProvider.categories
        _reviews.value = MockDataProvider.reviews
        loadProducts()
    }

    fun loadProducts(searchQuery: String? = currentSearchQuery, category: String? = currentCategory) {
        currentSearchQuery = searchQuery ?: ""
        currentCategory = category ?: "all"

        // Cancel previous request to eliminate coroutine race conditions
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _products.value = Resource.Loading()
            val result = repository.getProducts(currentSearchQuery, currentCategory)
            _products.value = result
        }
    }

    fun searchInstantly(query: String) {
        currentSearchQuery = query
        loadProducts(currentSearchQuery, currentCategory)
    }

    fun selectCategory(categoryId: String) {
        currentCategory = categoryId
        loadProducts(currentSearchQuery, currentCategory)
    }

    fun getProductDetail(productId: String) {
        _selectedProduct.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getProductById(productId)
            _selectedProduct.value = result
        }
    }
}
