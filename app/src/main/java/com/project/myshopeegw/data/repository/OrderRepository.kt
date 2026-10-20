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
