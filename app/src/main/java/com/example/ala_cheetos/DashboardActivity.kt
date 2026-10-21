package com.example.ala_cheetos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.ala_cheetos.databinding.ActivityDashboardBinding

class DashboardActivity : ToolbarActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sharedPref: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        setupToolbar(binding.appBar.toolbar, getString(R.string.title_dashboard))

        // Tampilkan username yang tersimpan (opsional)
        val username = sharedPref.getString("username", "-")
        // jika ada TextView untuk nama, set di sini

        binding.btnKaryawan.setOnClickListener {
            startActivity(Intent(this, KaryawanActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnLogout.setOnClickListener { showLogoutDialog() }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                // Hapus data SharedPreferences
                val editor = sharedPref.edit()
                editor.clear()          // atau editor.remove("isLogin")
                editor.apply()

                dialog.dismiss()

                // Kembali ke Login dan tutup Dashboard
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}