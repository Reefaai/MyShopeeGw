package com.project.myshopeegw.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Review(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("product_id")
    val productId: String = "",
    @SerializedName("user_name")
    val userName: String = "",
    @SerializedName("user_avatar")
    val userAvatar: String = "",
    @SerializedName("rating")
    val rating: Float = 5.0f,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("variant")
    val variant: String = "",
    @SerializedName("comment")
    val comment: String = "",
    @SerializedName("review_image")
    val reviewImage: String = ""
) : Serializable

data class Category(
    val id: String,
    val name: String,
    val iconRes: Int,
    val itemCount: Int = 0
)
