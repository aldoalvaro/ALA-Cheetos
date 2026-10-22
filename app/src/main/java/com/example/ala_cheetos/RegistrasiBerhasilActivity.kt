package com.example.ala_cheetos

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.ala_cheetos.databinding.ActivityRegistrasiBerhasilBinding

class RegistrasiBerhasilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrasiBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoLogin.setOnClickListener { goToLogin() }
        // Tombol back sistem juga ke Login, supaya tidak balik ke form registrasi
        onBackPressedDispatcher.addCallback(this) { goToLogin() }
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}