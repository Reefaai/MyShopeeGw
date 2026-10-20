package com.project.myshopeegw.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartItem(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("product_id")
    val productId: String = "",
    @SerializedName("product_name")
    val productName: String = "",
    @SerializedName("product_image")
    val productImage: String = "",
    @SerializedName("price")
    val price: Long = 0L,
    @SerializedName("quantity")
    var quantity: Int = 1,
    @SerializedName("variant")
    val variant: String = "",
    @SerializedName("is_selected")
    var isSelected: Boolean = true
) : Serializable {
    val totalPrice: Long
        get() = price * quantity
}

data class CartRequest(
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("price")
    val price: Long,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("variant")
    val variant: String
)
