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

        sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, BaseActivity::class.java))
            finish()
            return
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnWeb.setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val username = binding.inputUsername.text.toString().trim()
        val password = binding.inputPassword.text.toString()

        binding.tilUsername.error = if (username.isEmpty()) getString(R.string.error_username_empty) else null
        binding.tilPassword.error = if (password.isEmpty()) getString(R.string.error_password_empty) else null
        if (username.isEmpty() || password.isEmpty()) return

        // Data hasil registrasi (Soal 1 & 2)
        val regPref = getSharedPreferences(RegisterActivity.PREF_NAME, MODE_PRIVATE)
        val savedUsername = regPref.getString(RegisterActivity.KEY_USERNAME, "").orEmpty()
        val savedPassword = regPref.getString(RegisterActivity.KEY_PASSWORD, "").orEmpty()
        val isVerified = regPref.getBoolean(RegisterActivity.KEY_VERIFIED, false)

        // Rule 1: username = password -> boleh login (sesuai praktikum)
        val rule1 = username == password
        // Rule 2: username & password cocok dengan yang tersimpan di SharedPreference
        val rule2 = isVerified && username == savedUsername && password == savedPassword

        if (rule1 || rule2) {
            sharedPref.edit()
                .putBoolean("isLogin", true)
                .putString("username", username)
                .apply()

            startActivity(Intent(this, BaseActivity::class.java))
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Login Gagal")
                .setMessage("Silahkan coba lagi")
                .setPositiveButton("OK", null)
                .show()
            binding.tilPassword.error = getString(R.string.error_login_failed)
        }
    }
}