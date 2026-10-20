package com.project.myshopeegw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.data.repository.CartRepository
import com.project.myshopeegw.utils.Resource
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CartRepository(application)

    private val _cartState = MutableLiveData<Resource<List<CartItem>>>()
    val cartState: LiveData<Resource<List<CartItem>>> = _cartState

    private val _totalPrice = MutableLiveData<Long>()
    val totalPrice: LiveData<Long> = _totalPrice

    private val _selectedCount = MutableLiveData<Int>()
    val selectedCount: LiveData<Int> = _selectedCount

    private val _actionMessage = MutableLiveData<String>()
    val actionMessage: LiveData<String> = _actionMessage

    init {
        loadCart()
    }

    fun loadCart() {
        _cartState.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getCartItems()
            _cartState.value = result
            calculateTotals(result.data ?: emptyList())
        }
    }

    fun addToCart(product: Product, variant: String, quantity: Int = 1) {
        viewModelScope.launch {
            val result = repository.addToCart(product, variant, quantity)
            if (result is Resource.Success) {
                _actionMessage.value = "Berhasil ditambahkan ke keranjang"
                loadCart()
            } else {
                _actionMessage.value = result.message ?: "Gagal menambah ke keranjang"
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            repository.updateQuantity(item.id, item.quantity + 1)
            loadCart()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                repository.updateQuantity(item.id, item.quantity - 1)
            } else {
                repository.deleteCartItem(item.id)
                _actionMessage.value = "${item.productName} dihapus dari keranjang"
            }
            loadCart()
        }
    }

    fun deleteItem(item: CartItem) {
        viewModelScope.launch {
            repository.deleteCartItem(item.id)
            _actionMessage.value = "${item.productName} dihapus dari keranjang"
            loadCart()
        }
    }

    fun toggleSelect(item: CartItem, isSelected: Boolean) {
        repository.toggleSelect(item.id, isSelected)
        val currentList = _cartState.value?.data ?: emptyList()
        calculateTotals(currentList)
    }

    fun toggleSelectAll(isSelected: Boolean) {
        repository.toggleSelectAll(isSelected)
        val currentList = _cartState.value?.data ?: emptyList()
        currentList.forEach { it.isSelected = isSelected }
        calculateTotals(currentList)
        _cartState.value = Resource.Success(currentList)
    }

    private fun calculateTotals(items: List<CartItem>) {
        val selectedItems = items.filter { it.isSelected }
        val sum = selectedItems.sumOf { it.price * it.quantity }
        _totalPrice.value = sum
        _selectedCount.value = selectedItems.size
    }
}
