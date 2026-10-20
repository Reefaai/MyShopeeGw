package com.project.myshopeegw.data.api

import android.content.Context
import com.project.myshopeegw.utils.PrefManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Default Supabase Demo Project URL & Key (Mahasiswa bisa menggantinya di menu Profil / PrefManager)
    const val DEFAULT_SUPABASE_URL = "https://xyzcompany.supabase.co/"
    const val DEFAULT_SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummy_anon_key_for_offline_fallback"

    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String = ""

    fun getSupabaseService(context: Context): SupabaseApiService {
        val pref = PrefManager(context)
        val baseUrl = ensureTrailingSlash(pref.userEmail.let { DEFAULT_SUPABASE_URL })

        if (retrofit == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .header("apikey", DEFAULT_SUPABASE_KEY)
                    .header("Authorization", "Bearer $DEFAULT_SUPABASE_KEY")
                    .header("Content-Type", "application/json")
                chain.proceed(requestBuilder.build())
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!.create(SupabaseApiService::class.java)
    }

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }
}
