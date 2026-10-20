package com.project.myshopeegw.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CheckoutPayload(
    @SerializedName("items")
    val items: List<CartItem>,
    @SerializedName("shipping_address")
    val shippingAddress: String,
    @SerializedName("shipping_city")
    val shippingCity: String = "Kota Cirebon",
    @SerializedName("shipping_type")
    val shippingType: String,
    @SerializedName("shipping_cost")
    val shippingCost: Long,
    @SerializedName("subtotal")
    val subtotal: Long,
    @SerializedName("total_amount")
    val totalAmount: Long,
    @SerializedName("payment_method")
    val paymentMethod: String = "BCA Virtual Account",
    @SerializedName("va_number")
    val vaNumber: String = "123 456 7890"
) : Serializable

data class Order(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("order_number")
    val orderNumber: String = "",
    @SerializedName("items")
    val items: List<CartItem> = emptyList(),
    @SerializedName("shipping_address")
    val shippingAddress: String = "",
    @SerializedName("shipping_city")
    val shippingCity: String = "Kota Cirebon",
    @SerializedName("shipping_type")
    val shippingType: String = "Reguler (JNE Cirebon)",
    @SerializedName("shipping_cost")
    val shippingCost: Long = 15000L,
    @SerializedName("subtotal")
    val subtotal: Long = 0L,
    @SerializedName("total_amount")
    val totalAmount: Long = 0L,
    @SerializedName("payment_method")
    var paymentMethod: String = "BCA Virtual Account",
    @SerializedName("va_number")
    var vaNumber: String = "123 456 7890",
    @SerializedName("payment_status")
    var paymentStatus: String = "PENDING", // PENDING, PAID
    @SerializedName("tracking_status")
    var trackingStatus: String = "DIKEMAS", // DIPESAN, DIKEMAS, DIKIRIM, SELESAI
    @SerializedName("courier_resi")
    val courierResi: String = "JNE-CRB-8839210",
    @SerializedName("created_at")
    val createdAt: String = ""
) : Serializable

data class OrderResponse(
    @SerializedName("success")
    val success: Boolean = true,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("order")
    val order: Order? = null
)

data class PaymentResponse(
    @SerializedName("success")
    val success: Boolean = true,
    @SerializedName("message")
    val message: String = "Pembayaran Berhasil Dilunasi!",
    @SerializedName("order_id")
    val orderId: String = "",
    @SerializedName("status")
    val status: String = "PAID"
)
