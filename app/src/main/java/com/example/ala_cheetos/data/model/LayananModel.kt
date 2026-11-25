package com.example.ala_cheetos.data.model

import com.google.gson.annotations.SerializedName

data class LayananModel(
    val id: Long? = null,
    val nama: String,
    @SerializedName("durasi_menit") val durasiMenit: Int,
    val harga: Long
)