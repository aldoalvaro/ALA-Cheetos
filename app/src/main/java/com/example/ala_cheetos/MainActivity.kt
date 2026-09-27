package com.example.ala_cheetos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ala_cheetos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMyProject.setOnClickListener {
            val nama = binding.inputNama.text.toString()

            if (nama.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_nama_kosong), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.toast_sapaan, nama), Toast.LENGTH_SHORT).show()

                val intent = Intent(this, SnackDetailActivity::class.java)
                intent.putExtra("EXTRA_NAMA", nama)
                startActivity(intent)
            }
        }
    }
}