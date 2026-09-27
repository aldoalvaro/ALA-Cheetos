package com.example.ala_cheetos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ala_cheetos.databinding.ActivitySnackDetailBinding

class SnackDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnackDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnackDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = intent.getStringExtra("EXTRA_NAMA") ?: "Kamu"
        binding.tvSapaan.text = getString(R.string.sapaan_detail, nama)
    }
}