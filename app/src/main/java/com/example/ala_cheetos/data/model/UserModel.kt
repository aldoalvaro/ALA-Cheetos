package com.example.ala_cheetos.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    val id: Long? = null,
    val name: String,
    val email: String,
    @SerializedName("birth_date") val birthDate: String,
    val gender: String,
    val username: String,
    val password: String
)