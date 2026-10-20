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

        const val DEFAULT_SUPABASE_URL = "https://yyuiobkasslmvcvvzarn.supabase.co/"
        const val DEFAULT_SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl5dWlvYmthc3NsbXZjdnZ6YXJuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTI0ODE0OTIsImV4cCI6MjEwODA1NzQ5Mn0.86nBOgRAsyPzHVotonF6BkDIghqWW68coN2bK8Tia-M"
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

    var supabaseUrl: String
        get() = pref.getString(KEY_SUPABASE_URL, DEFAULT_SUPABASE_URL) ?: DEFAULT_SUPABASE_URL
        set(value) = pref.edit().putString(KEY_SUPABASE_URL, value).apply()

    var supabaseKey: String
        get() = pref.getString(KEY_SUPABASE_KEY, DEFAULT_SUPABASE_KEY) ?: DEFAULT_SUPABASE_KEY
        set(value) = pref.edit().putString(KEY_SUPABASE_KEY, value).apply()

    fun logout() {
        pref.edit().clear().apply()
    }
}
