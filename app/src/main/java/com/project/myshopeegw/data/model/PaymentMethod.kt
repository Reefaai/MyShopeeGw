package com.project.myshopeegw.data.model

import java.io.Serializable

data class PaymentMethod(
    val id: String,
    val name: String,
    val category: String, // "Virtual Account", "E-Wallet & QRIS", "Mitra & Tunai"
    val codeOrVa: String,
    val description: String,
    val badgeText: String,
    val instructions: List<String>,
    var isCustom: Boolean = false
) : Serializable
