package com.project.myshopeegw.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyHelper {
    fun formatRupiah(amount: Long): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return formatter.format(amount).replace("Rp", "Rp ")
    }

    fun formatRupiah(amount: Int): String {
        return formatRupiah(amount.toLong())
    }
}
