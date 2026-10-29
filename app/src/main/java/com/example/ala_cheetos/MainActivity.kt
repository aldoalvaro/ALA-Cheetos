package com.example.ala_cheetos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ala_cheetos.databinding.ActivityMainBinding
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

/** Halaman Login (Auth). Sengaja TANPA Toolbar. */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: android.content.SharedPreferences

    private lateinit var bioPref: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        // Dipisah dari user_pref karena user_pref dihapus saat logout
        bioPref = getSharedPreferences(BIO_PREF, MODE_PRIVATE)

        if (sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, BaseActivity::class.java))
            finish()
            return
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnBiometric.setOnClickListener { attemptBiometricLogin() }
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
            // Simpan username terakhir yang berhasil login (syarat untuk login biometrik)
            bioPref.edit().putString(KEY_LAST_USERNAME, username).apply()

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
    private fun attemptBiometricLogin() {
        val savedUsername = bioPref.getString(KEY_LAST_USERNAME, null)
        if (savedUsername.isNullOrEmpty()) {
            Toast.makeText(this, R.string.biometric_no_saved, Toast.LENGTH_LONG).show()
            return
        }

        val authenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK
        if (BiometricManager.from(this).canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPrompt(savedUsername, authenticators)
        } else {
            Toast.makeText(this, R.string.biometric_unavailable, Toast.LENGTH_LONG).show()
        }
    }

    private fun showBiometricPrompt(username: String, authenticators: Int) {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    sharedPref.edit()
                        .putBoolean("isLogin", true)
                        .putString("username", username)
                        .apply()
                    startActivity(Intent(this@MainActivity, BaseActivity::class.java))
                    finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Jangan tampilkan pesan kalau pengguna sendiri yang membatalkan
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                        errorCode != BiometricPrompt.ERROR_USER_CANCELED
                    ) {
                        Toast.makeText(this@MainActivity, errString, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity, R.string.biometric_failed, Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setAllowedAuthenticators(authenticators)
            .setNegativeButtonText(getString(R.string.biometric_cancel))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    companion object {
        private const val BIO_PREF = "biometric_pref"
        private const val KEY_LAST_USERNAME = "last_username"
    }
}