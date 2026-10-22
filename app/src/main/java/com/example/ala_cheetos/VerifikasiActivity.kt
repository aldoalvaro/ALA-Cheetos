package com.example.ala_cheetos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ala_cheetos.databinding.ActivityVerifikasiBinding

/** Soal 2: Tampilkan isian dari SharedPreferences, lalu validasi saat Submit. */
class VerifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifikasiBinding
    private lateinit var regPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regPref = getSharedPreferences(RegisterActivity.PREF_NAME, MODE_PRIVATE)

        showData()

        binding.btnSubmit.setOnClickListener { submit() }
        binding.btnBack.setOnClickListener { finish() } // kembali ke halaman registrasi
    }

    private fun pref(key: String): String = regPref.getString(key, "").orEmpty()

    private fun showData() {
        binding.tvName.text = pref(RegisterActivity.KEY_NAME).ifBlank { "-" }
        binding.tvEmail.text = pref(RegisterActivity.KEY_EMAIL).ifBlank { "-" }
        binding.tvBirthDate.text = pref(RegisterActivity.KEY_BIRTH_DATE).ifBlank { "-" }
        binding.tvGender.text = pref(RegisterActivity.KEY_GENDER).ifBlank { "-" }
        binding.tvUsername.text = pref(RegisterActivity.KEY_USERNAME).ifBlank { "-" }
        binding.tvPassword.text = pref(RegisterActivity.KEY_PASSWORD).ifBlank { "-" }
        binding.tvConfirmPassword.text = pref(RegisterActivity.KEY_CONFIRM_PASSWORD).ifBlank { "-" }
    }

    private fun validate(): List<String> {
        val errors = mutableListOf<String>()

        val name = pref(RegisterActivity.KEY_NAME)
        val email = pref(RegisterActivity.KEY_EMAIL)
        val birthDate = pref(RegisterActivity.KEY_BIRTH_DATE)
        val gender = pref(RegisterActivity.KEY_GENDER)
        val username = pref(RegisterActivity.KEY_USERNAME)
        val password = pref(RegisterActivity.KEY_PASSWORD)
        val confirm = pref(RegisterActivity.KEY_CONFIRM_PASSWORD)

        // 1) Semua inputan tidak boleh kosong + cek value-nya
        if (name.isBlank()) errors += "Nama tidak boleh kosong"

        if (email.isBlank()) errors += "Email tidak boleh kosong"
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) errors += "Format email tidak valid"

        if (birthDate.isBlank()) errors += "Tanggal lahir belum dipilih"
        if (gender.isBlank()) errors += "Jenis kelamin belum dipilih"
        if (username.isBlank()) errors += "Username tidak boleh kosong"
        if (password.isBlank()) errors += "Password tidak boleh kosong"
        if (confirm.isBlank()) errors += "Confirm Password tidak boleh kosong"

        // 2) Password dan Confirm Password harus sama
        if (password.isNotBlank() && confirm.isNotBlank() && password != confirm) {
            errors += "Password dan Confirm Password harus sama"
        }
        return errors
    }

    private fun submit() {
        val errors = validate()

        if (errors.isEmpty()) {
            // Lolos validasi: tandai akun valid, tampilkan halaman "Registrasi Berhasil"
            regPref.edit().putBoolean(RegisterActivity.KEY_VERIFIED, true).apply()
            startActivity(Intent(this, RegistrasiBerhasilActivity::class.java))
            finish()
        } else {
            // Gagal: tampilkan daftar error + munculkan tombol Kembali
            binding.tvError.text = errors.joinToString(separator = "\n") { "• $it" }
            binding.tvError.visibility = View.VISIBLE
            binding.btnBack.visibility = View.VISIBLE
        }
    }
}