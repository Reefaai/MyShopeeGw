package com.project.myshopeegw.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("price")
    val price: Long = 0L,
    @SerializedName("original_price")
    val originalPrice: Long = 0L,
    @SerializedName("rating")
    val rating: Float = 5.0f,
    @SerializedName("sold_count")
    val soldCount: Int = 0,
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("variants")
    val variants: List<String> = emptyList(),
    @SerializedName("stock")
    val stock: Int = 100
) : Serializable {
    val discountPercent: Int
        get() = if (originalPrice > price) {
            (((originalPrice - price).toDouble() / originalPrice) * 100).toInt()
        } else 0
}
