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
        viewModelScope.launch {
            val result = repository.getCartItems()
            val list = result.data ?: emptyList()
            _cartState.value = Resource.Success(list)
            calculateTotals(list)
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
        val currentList = _cartState.value?.data ?: emptyList()
        val newQty = item.quantity + 1

        // 1. Instantly update UI with a fresh immutable copy
        val updatedList = currentList.map {
            if (it.id == item.id) it.copy(quantity = newQty) else it
        }
        _cartState.value = Resource.Success(updatedList)
        calculateTotals(updatedList)

        // 2. Persist to repository and Supabase
        viewModelScope.launch {
            repository.updateQuantity(item.id, newQty)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val currentList = _cartState.value?.data ?: emptyList()
        val newQty = item.quantity - 1

        if (newQty > 0) {
            // 1. Instantly update UI with a fresh immutable copy
            val updatedList = currentList.map {
                if (it.id == item.id) it.copy(quantity = newQty) else it
            }
            _cartState.value = Resource.Success(updatedList)
            calculateTotals(updatedList)

            // 2. Persist in background
            viewModelScope.launch {
                repository.updateQuantity(item.id, newQty)
            }
        } else {
            // Delete item
            val updatedList = currentList.filter { it.id != item.id }
            _cartState.value = Resource.Success(updatedList)
            calculateTotals(updatedList)
            _actionMessage.value = "${item.productName} dihapus dari keranjang"

            viewModelScope.launch {
                repository.deleteCartItem(item.id)
            }
        }
    }

    fun deleteItem(item: CartItem) {
        val currentList = _cartState.value?.data ?: emptyList()
        val updatedList = currentList.filter { it.id != item.id }
        _cartState.value = Resource.Success(updatedList)
        calculateTotals(updatedList)
        _actionMessage.value = "${item.productName} dihapus dari keranjang"

        viewModelScope.launch {
            repository.deleteCartItem(item.id)
        }
    }

    fun toggleSelect(item: CartItem, isSelected: Boolean) {
        repository.toggleSelect(item.id, isSelected)
        val currentList = _cartState.value?.data ?: emptyList()
        val updatedList = currentList.map {
            if (it.id == item.id) {
                it.isSelected = isSelected
                it.copy()
            } else it
        }
        _cartState.value = Resource.Success(updatedList)
        calculateTotals(updatedList)
    }

    fun toggleSelectAll(isSelected: Boolean) {
        repository.toggleSelectAll(isSelected)
        val currentList = _cartState.value?.data ?: emptyList()
        val updatedList = currentList.map {
            it.isSelected = isSelected
            it.copy()
        }
        _cartState.value = Resource.Success(updatedList)
        calculateTotals(updatedList)
    }

    private fun calculateTotals(items: List<CartItem>) {
        val selectedItems = items.filter { it.isSelected }
        val sum = selectedItems.sumOf { it.price * it.quantity }
        _totalPrice.value = sum
        _selectedCount.value = selectedItems.size
    }
}
