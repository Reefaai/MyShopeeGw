package com.project.myshopeegw.data.repository

import android.content.Context
import com.project.myshopeegw.data.api.MockDataProvider
import com.project.myshopeegw.data.api.RetrofitClient
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val context: Context) {

    private val api = RetrofitClient.getSupabaseService(context)

    suspend fun getProducts(searchQuery: String? = null, category: String? = null): Resource<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch from live Supabase API
                val response = api.getProducts()

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    var list = response.body()!!

                    // Filter Category
                    if (!category.isNullOrBlank() && category != "all") {
                        list = list.filter { it.category.trim().equals(category.trim(), ignoreCase = true) }
                    }

                    // Filter Search Query
                    if (!searchQuery.isNullOrBlank()) {
                        val q = searchQuery.trim().lowercase()
                        list = list.filter {
                            it.name.lowercase().contains(q) ||
                                    it.description.lowercase().contains(q) ||
                                    it.category.lowercase().contains(q)
                        }
                    }

                    Resource.Success(list)
                } else {
                    // Fallback to local products
                    filterLocalProducts(searchQuery, category)
                }
            } catch (e: Exception) {
                filterLocalProducts(searchQuery, category)
            }
        }
    }

    private fun filterLocalProducts(searchQuery: String?, category: String?): Resource<List<Product>> {
        var filtered = MockDataProvider.products.toList()

        if (!category.isNullOrBlank() && category != "all") {
            filtered = filtered.filter { it.category.trim().equals(category.trim(), ignoreCase = true) }
        }

        if (!searchQuery.isNullOrBlank()) {
            val query = searchQuery.trim().lowercase()
            filtered = filtered.filter {
                it.name.lowercase().contains(query) ||
                        it.description.lowercase().contains(query) ||
                        it.category.lowercase().contains(query)
            }
        }

        return Resource.Success(filtered)
    }

    suspend fun getProductById(productId: String): Resource<Product> {
        return withContext(Dispatchers.IO) {
            val product = MockDataProvider.products.find { it.id == productId }
            if (product != null) {
                Resource.Success(product)
            } else {
                Resource.Error("Produk tidak ditemukan")
            }
        }
    }
}
