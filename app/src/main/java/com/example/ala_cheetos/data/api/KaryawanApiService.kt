package com.example.ala_cheetos.data.api

import com.example.ala_cheetos.data.model.KaryawanModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface KaryawanApiService {
    @GET("karyawan?order=id.asc")
    suspend fun getAll(): Response<List<KaryawanModel>>

    @POST("karyawan")
    suspend fun insert(@Body body: KaryawanModel): Response<Unit>

    // id diisi "eq.5" artinya id sama dengan 5 (sama seperti di modul)
    @PATCH("karyawan")
    suspend fun update(@Query("id") id: String, @Body body: KaryawanModel): Response<Unit>

    @DELETE("karyawan")
    suspend fun delete(@Query("id") id: String): Response<Unit>
}