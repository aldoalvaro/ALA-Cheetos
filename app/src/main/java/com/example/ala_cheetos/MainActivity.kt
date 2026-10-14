package com.example.ala_cheetos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ala_cheetos.databinding.ActivityMainBinding

/** Halaman Login (Auth). Sengaja TANPA Toolbar. */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences harus didefinisikan di awal onCreate
        sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Optional: jika sudah login, langsung ke Dashboard
        // (biasanya sudah di-handle Splash, tapi aman ditaruh juga)
        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnWeb.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString()

        binding.tilEmail.error = if (email.isEmpty()) getString(R.string.error_email_empty) else null
        binding.tilPassword.error = if (password.isEmpty()) getString(R.string.error_password_empty) else null
        if (email.isEmpty() || password.isEmpty()) return

        // ===== PENGECEKAN USERNAME & PASSWORD =====
        if (email == DEMO_EMAIL && password == DEMO_PASSWORD) {
            // Autentikasi berhasil → simpan status login
            val editor = sharedPref.edit()
            editor.putBoolean("isLogin", true)
            editor.putString("username", email)
            editor.apply()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        } else {
            // Gagal → AlertDialog sesuai materi
            AlertDialog.Builder(this)
                .setTitle("Login Gagal")
                .setMessage("Silahkan coba lagi")
                .setPositiveButton("OK", null)
                .show()
            binding.tilPassword.error = getString(R.string.error_login_failed)
        }
    }

    private companion object {
        const val DEMO_EMAIL = "manager@oscardo.com"
        const val DEMO_PASSWORD = "oscardo123"
    }
}