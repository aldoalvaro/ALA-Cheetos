package com.example.ala_cheetos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ala_cheetos.data.api.SupabaseClient
import com.example.ala_cheetos.data.model.UserModel
import com.example.ala_cheetos.databinding.ActivityVerifikasiBinding
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.MessageDigest

/** Soal 2: Tampilkan isian dari SharedPreferences, validasi, lalu simpan ke Supabase (tabel users). */
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

        if (name.isBlank()) errors += "Nama tidak boleh kosong"

        if (email.isBlank()) errors += "Email tidak boleh kosong"
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) errors += "Format email tidak valid"

        if (birthDate.isBlank()) errors += "Tanggal lahir belum dipilih"
        if (gender.isBlank()) errors += "Jenis kelamin belum dipilih"
        if (username.isBlank()) errors += "Username tidak boleh kosong"
        if (password.isBlank()) errors += "Password tidak boleh kosong"
        if (confirm.isBlank()) errors += "Confirm Password tidak boleh kosong"

        if (password.isNotBlank() && confirm.isNotBlank() && password != confirm) {
            errors += "Password dan Confirm Password harus sama"
        }
        return errors
    }

    private fun submit() {
        val errors = validate()
        if (errors.isNotEmpty()) {
            showErrors(errors)
            return
        }
        registerToSupabase()
    }

    private fun registerToSupabase() {
        setLoading(true)

        val user = UserModel(
            name = pref(RegisterActivity.KEY_NAME),
            email = pref(RegisterActivity.KEY_EMAIL),
            birthDate = toIsoDate(pref(RegisterActivity.KEY_BIRTH_DATE)),
            gender = pref(RegisterActivity.KEY_GENDER),
            username = pref(RegisterActivity.KEY_USERNAME),
            // Password disimpan dalam bentuk hash, bukan teks asli
            password = sha256(pref(RegisterActivity.KEY_PASSWORD))
        )

        lifecycleScope.launch {
            try {
                val response = SupabaseClient.userApi.register(user)
                when {
                    response.isSuccessful -> {
                        // Tandai akun valid (dipakai aturan login lama), lanjut ke halaman berhasil
                        regPref.edit().putBoolean(RegisterActivity.KEY_VERIFIED, true).apply()
                        startActivity(Intent(this@VerifikasiActivity, RegistrasiBerhasilActivity::class.java))
                        finish()
                    }
                    response.code() == 409 ->
                        showErrors(listOf("Username atau email sudah terdaftar"))
                    else ->
                        showErrors(listOf("Gagal menyimpan ke server (kode ${response.code()})"))
                }
            } catch (e: IOException) {
                showErrors(listOf("Tidak bisa terhubung ke server. Periksa koneksi internet."))
            } finally {
                setLoading(false)
            }
        }
    }

    private fun showErrors(errors: List<String>) {
        binding.tvError.text = errors.joinToString(separator = "\n") { "• $it" }
        binding.tvError.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        binding.btnSubmit.isEnabled = !loading
    }

    /** dd/MM/yyyy (dari DatePicker) -> yyyy-MM-dd (format kolom date di PostgreSQL) */
    private fun toIsoDate(ddMMyyyy: String): String {
        val p = ddMMyyyy.split("/")
        return if (p.size == 3) "${p[2]}-${p[1]}-${p[0]}" else ddMMyyyy
    }

    private fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}