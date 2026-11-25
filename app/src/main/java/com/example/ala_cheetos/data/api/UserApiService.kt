package com.example.ala_cheetos.data.api

import com.example.ala_cheetos.data.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("users")
    suspend fun register(@Body user: UserModel): Response<Unit>
}