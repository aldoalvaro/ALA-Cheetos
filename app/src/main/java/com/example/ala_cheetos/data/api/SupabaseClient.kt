package com.example.ala_cheetos.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseConfig {
    // Harus diakhiri "/rest/v1/"  (dari Integrations -> Data API -> API URL)
    const val PROJECT_URL = "https://iunujyqkxueyqprgpeub.supabase.co/rest/v1/"
    // Publishable key (Project Settings -> API Keys)
    const val API_KEY = "sb_publishable_p0J3h0xRo8IvPOQFUj3Kgw_WFnsV-zz"
    const val SCHEMA = "mobile"
}

object SupabaseClient {

    // Interceptor: semua request otomatis membawa apikey + nama schema
    // (Accept-Profile dipakai GET, Content-Profile dipakai POST/PATCH/DELETE)
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", SupabaseConfig.API_KEY)
                .addHeader("Accept-Profile", SupabaseConfig.SCHEMA)
                .addHeader("Content-Profile", SupabaseConfig.SCHEMA)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SupabaseConfig.PROJECT_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val karyawanApi: KaryawanApiService by lazy { retrofit.create(KaryawanApiService::class.java) }
    val layananApi: LayananApiService by lazy { retrofit.create(LayananApiService::class.java) }
}