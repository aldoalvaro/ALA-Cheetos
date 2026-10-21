package com.example.ala_cheetos

import android.os.Bundle
import com.example.ala_cheetos.databinding.ActivityProfileBinding

class ProfileActivity : ToolbarActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.appBar.toolbar, "Profil Developer")
    }
}