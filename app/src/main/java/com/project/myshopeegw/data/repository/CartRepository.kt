package com.project.myshopeegw.data.repository

import android.content.Context
import com.project.myshopeegw.data.api.MockDataProvider
import com.project.myshopeegw.data.api.RetrofitClient
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class CartRepository(private val context: Context) {

    private val api = RetrofitClient.getSupabaseService(context)

    suspend fun getCartItems(): Resource<List<CartItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getCart()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val supabaseItems = response.body()!!
                    supabaseItems.forEach { item ->
                        val local = MockDataProvider.cartItems.find { it.id == item.id }
                        item.isSelected = local?.isSelected ?: true
                    }
                    MockDataProvider.cartItems.clear()
                    MockDataProvider.cartItems.addAll(supabaseItems)
                    Resource.Success(supabaseItems)
                } else {
                    Resource.Success(MockDataProvider.cartItems.toList())
                }
            } catch (e: Exception) {
                Resource.Success(MockDataProvider.cartItems.toList())
            }
        }
    }

    suspend fun addToCart(product: Product, variant: String, quantity: Int): Resource<CartItem> {
        return withContext(Dispatchers.IO) {
            try {
                val existing = MockDataProvider.cartItems.find {
                    it.productId == product.id && it.variant == variant
                }

                val itemToSave = if (existing != null) {
                    existing.quantity += quantity
                    existing
                } else {
                    val newItem = CartItem(
                        id = "cart-" + UUID.randomUUID().toString().take(8),
                        productId = product.id,
                        productName = product.name,
                        productImage = product.imageUrl,
                        price = product.price,
                        quantity = quantity,
                        variant = variant,
                        isSelected = true
                    )
                    MockDataProvider.cartItems.add(newItem)
                    newItem
                }

                // Push to live Supabase backend
                try {
                    api.addToCart(itemToSave)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Resource.Success(itemToSave)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Gagal menambahkan ke keranjang")
            }
        }
    }

    suspend fun updateQuantity(cartId: String, newQuantity: Int): Resource<CartItem> {
        return withContext(Dispatchers.IO) {
            val item = MockDataProvider.cartItems.find { it.id == cartId }
            if (item != null) {
                if (newQuantity <= 0) {
                    MockDataProvider.cartItems.remove(item)
                    try {
                        api.deleteCartItem("eq.$cartId")
                    } catch (ignored: Exception) {}
                    Resource.Error("Item dihapus dari keranjang")
                } else {
                    item.quantity = newQuantity
                    try {
                        api.updateCartQuantity("eq.$cartId", mapOf("quantity" to newQuantity))
                    } catch (ignored: Exception) {}
                    Resource.Success(item)
                }
            } else {
                Resource.Error("Item tidak ditemukan")
            }
        }
    }

    suspend fun deleteCartItem(cartId: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            val removed = MockDataProvider.cartItems.removeAll { it.id == cartId }
            try {
                api.deleteCartItem("eq.$cartId")
            } catch (ignored: Exception) {}
            if (removed) Resource.Success(true) else Resource.Error("Item gagal dihapus")
        }
    }

    fun toggleSelect(cartId: String, isSelected: Boolean) {
        MockDataProvider.cartItems.find { it.id == cartId }?.isSelected = isSelected
    }

    fun toggleSelectAll(isSelected: Boolean) {
        MockDataProvider.cartItems.forEach { it.isSelected = isSelected }
    }

    fun clearSelectedItems(): List<CartItem> {
        val selected = MockDataProvider.cartItems.filter { it.isSelected }
        MockDataProvider.cartItems.removeAll { it.isSelected }
        return selected
    }
}
