package com.example.ala_cheetos.data.api

import com.example.ala_cheetos.data.model.LayananModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface LayananApiService {
    @GET("layanan?order=id.asc")
    suspend fun getAll(): Response<List<LayananModel>>

    @POST("layanan")
    suspend fun insert(@Body body: LayananModel): Response<Unit>

    @PATCH("layanan")
    suspend fun update(@Query("id") id: String, @Body body: LayananModel): Response<Unit>

    @DELETE("layanan")
    suspend fun delete(@Query("id") id: String): Response<Unit>
}