package com.project.myshopeegw.data.repository

import android.content.Context
import com.project.myshopeegw.data.model.CheckoutPayload
import com.project.myshopeegw.data.model.Order
import com.project.myshopeegw.data.model.PaymentResponse
import com.project.myshopeegw.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class OrderRepository(private val context: Context) {

    private val api = com.project.myshopeegw.data.api.RetrofitClient.getSupabaseService(context)

    companion object {
        val ordersList = mutableListOf<Order>()
        var lastCreatedOrder: Order? = null
    }

    suspend fun createOrder(payload: CheckoutPayload): Resource<Order> {
        return withContext(Dispatchers.IO) {
            val orderNumber = "INV/SHOP/" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "/" + (1000..9999).random()
            val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID")).format(Date())

            val newOrder = Order(
                id = "ord-" + UUID.randomUUID().toString().take(8),
                orderNumber = orderNumber,
                items = payload.items,
                shippingAddress = payload.shippingAddress,
                shippingCity = payload.shippingCity,
                shippingType = payload.shippingType,
                shippingCost = payload.shippingCost,
                subtotal = payload.subtotal,
                totalAmount = payload.totalAmount,
                paymentMethod = payload.paymentMethod,
                vaNumber = payload.vaNumber,
                paymentStatus = "PENDING",
                trackingStatus = "DIPESAN",
                courierResi = "JNE-CRB-" + (1000000..9999999).random(),
                createdAt = dateStr
            )

            // Push to Supabase Cloud Database
            try {
                val orderMap = mapOf<String, Any>(
                    "id" to newOrder.id,
                    "order_number" to newOrder.orderNumber,
                    "shipping_address" to newOrder.shippingAddress,
                    "shipping_city" to newOrder.shippingCity,
                    "shipping_type" to newOrder.shippingType,
                    "shipping_cost" to newOrder.shippingCost,
                    "subtotal" to newOrder.subtotal,
                    "total_amount" to newOrder.totalAmount,
                    "va_number" to newOrder.vaNumber,
                    "payment_status" to newOrder.paymentStatus,
                    "tracking_status" to newOrder.trackingStatus,
                    "courier_resi" to newOrder.courierResi
                )
                api.createOrder(orderMap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            ordersList.add(0, newOrder)
            lastCreatedOrder = newOrder

            Resource.Success(newOrder)
        }
    }

    suspend fun simulatePayment(orderId: String): Resource<PaymentResponse> {
        return withContext(Dispatchers.IO) {
            val order = ordersList.find { it.id == orderId } ?: lastCreatedOrder
            if (order != null) {
                order.paymentStatus = "PAID"
                order.trackingStatus = "DIKEMAS"

                // Update status in Supabase Cloud
                try {
                    api.updateOrder(
                        "eq.${order.id}",
                        mapOf(
                            "payment_status" to "PAID",
                            "tracking_status" to "DIKEMAS"
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Resource.Success(
                    PaymentResponse(
                        success = true,
                        message = "Pembayaran Berhasil Dilunasi!",
                        orderId = order.id,
                        status = "PAID"
                    )
                )
            } else {
                Resource.Error("Pesanan tidak ditemukan")
            }
        }
    }

    fun getLastOrder(): Order? {
        return lastCreatedOrder ?: ordersList.firstOrNull()
    }
}
