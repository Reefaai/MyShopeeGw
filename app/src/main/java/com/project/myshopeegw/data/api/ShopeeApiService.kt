package com.project.myshopeegw.data.api

import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.data.model.CartRequest
import com.project.myshopeegw.data.model.CheckoutPayload
import com.project.myshopeegw.data.model.OrderResponse
import com.project.myshopeegw.data.model.PaymentResponse
import com.project.myshopeegw.data.model.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface Retrofit untuk Konsumsi Endpoint REST API ShopiKu
 * Sesuai dengan Bagian IV Panduan Projek UAS STMIK IKMI Cirebon
 */
interface ShopeeApiService {

    @GET("api/products")
    suspend fun getProducts(
        @Query("search") q: String? = null
    ): Response<List<Product>>

    @GET("api/cart")
    suspend fun getCart(): Response<List<CartItem>>

    @POST("api/cart")
    suspend fun addToCart(
        @Body item: CartRequest
    ): Response<CartItem>

    @PUT("api/cart/{id}")
    suspend fun updateCartQuantity(
        @Path("id") cartId: String,
        @Body body: Map<String, Int>
    ): Response<CartItem>

    @DELETE("api/cart/{id}")
    suspend fun deleteCartItem(
        @Path("id") cartId: String
    ): Response<Void>

    @POST("api/checkout")
    suspend fun checkout(
        @Body payload: CheckoutPayload
    ): Response<OrderResponse>

    @POST("api/payment/simulate")
    suspend fun simulatePayment(
        @Query("order_id") orderId: String
    ): Response<PaymentResponse>
}

/**
 * Interface Retrofit khusus untuk direct PostgREST Supabase REST API
 */
interface SupabaseApiService {

    @GET("rest/v1/products?select=*")
    suspend fun getProducts(
        @Query("name") searchFilter: String? = null
    ): Response<List<Product>>

    @GET("rest/v1/cart?select=*")
    suspend fun getCart(): Response<List<CartItem>>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/cart")
    suspend fun addToCart(
        @Body item: CartItem
    ): Response<List<CartItem>>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/cart")
    suspend fun updateCartQuantity(
        @Query("id") idFilter: String, // eq.{id}
        @Body body: Map<String, Int>
    ): Response<List<CartItem>>

    @DELETE("rest/v1/cart")
    suspend fun deleteCartItem(
        @Query("id") idFilter: String // eq.{id}
    ): Response<Void>

    @Headers("Prefer: return=representation")
    @POST("rest/v1/orders")
    suspend fun createOrder(
        @Body order: Map<String, Any>
    ): Response<List<Map<String, Any>>>
}
