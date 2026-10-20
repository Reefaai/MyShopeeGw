package com.project.myshopeegw.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("shopiku_pref", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ADDRESS = "user_address"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_SUPABASE_URL = "supabase_url"
        private const val KEY_SUPABASE_KEY = "supabase_key"
    }

    var isLoggedIn: Boolean
        get() = pref.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = pref.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userName: String
        get() = pref.getString(KEY_USER_NAME, "Mahasiswa IKMI") ?: "Mahasiswa IKMI"
        set(value) = pref.edit().putString(KEY_USER_NAME, value).apply()

    var userEmail: String
        get() = pref.getString(KEY_USER_EMAIL, "mahasiswa@ikmi.ac.id") ?: "mahasiswa@ikmi.ac.id"
        set(value) = pref.edit().putString(KEY_USER_EMAIL, value).apply()

    var userPhone: String
        get() = pref.getString(KEY_USER_PHONE, "0812-3456-7890") ?: "0812-3456-7890"
        set(value) = pref.edit().putString(KEY_USER_PHONE, value).apply()

    var userAddress: String
        get() = pref.getString(KEY_USER_ADDRESS, "Jl. Perjuangan No. 10B, Karyamulya, Kec. Kesambi, Kota Cirebon, Jawa Barat 45131")
            ?: "Jl. Perjuangan No. 10B, Karyamulya, Kec. Kesambi, Kota Cirebon, Jawa Barat 45131"
        set(value) = pref.edit().putString(KEY_USER_ADDRESS, value).apply()

    fun logout() {
        pref.edit().clear().apply()
    }
}
