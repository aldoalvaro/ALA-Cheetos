package com.example.ala_cheetos

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ala_cheetos.databinding.ActivityRegisterBinding
import java.util.Calendar
import java.util.Locale

/** Soal 1: Halaman Registrasi. Semua isian disimpan ke SharedPreferences, validasi ada di halaman Verifikasi. */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var regPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        loadSavedData() // isi ulang form kalau user kembali dari halaman Verifikasi

        binding.inputBirthDate.setOnClickListener { showDatePicker() }

        binding.btnNext.setOnClickListener {
            saveToSharedPreferences()
            startActivity(Intent(this, VerifikasiActivity::class.java))
        }

        binding.btnToLogin.setOnClickListener { finish() }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)   // 0-based (Januari = 0)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        // Kalau tanggal sudah pernah dipilih, DatePicker dibuka di tanggal tersebut
        val parts = binding.inputBirthDate.text.toString().split("/")
        if (parts.size == 3) {
            day = parts[0].toIntOrNull() ?: day
            month = (parts[1].toIntOrNull() ?: (month + 1)) - 1
            year = parts[2].toIntOrNull() ?: year
        }

        val dialog = DatePickerDialog(this, { _, y, m, d ->
            // m + 1 karena bulan di DatePicker dimulai dari 0
            binding.inputBirthDate.setText(String.format(Locale.US, "%02d/%02d/%04d", d, m + 1, y))
        }, year, month, day)
        dialog.datePicker.maxDate = System.currentTimeMillis() // tidak boleh tanggal masa depan
        dialog.show()
    }

    private fun saveToSharedPreferences() {
        val gender = when (binding.rgGender.checkedRadioButtonId) {
            R.id.rbMale -> getString(R.string.gender_male)
            R.id.rbFemale -> getString(R.string.gender_female)
            else -> "" // belum dipilih, nanti ditangkap validasi di halaman Verifikasi
        }

        regPref.edit()
            .putString(KEY_NAME, binding.inputName.text.toString().trim())
            .putString(KEY_EMAIL, binding.inputRegEmail.text.toString().trim())
            .putString(KEY_BIRTH_DATE, binding.inputBirthDate.text.toString())
            .putString(KEY_GENDER, gender)
            .putString(KEY_USERNAME, binding.inputRegUsername.text.toString().trim())
            .putString(KEY_PASSWORD, binding.inputRegPassword.text.toString())
            .putString(KEY_CONFIRM_PASSWORD, binding.inputConfirmPassword.text.toString())
            .putBoolean(KEY_VERIFIED, false) // data baru, harus diverifikasi ulang
            .apply()
    }

    private fun loadSavedData() {
        binding.inputName.setText(regPref.getString(KEY_NAME, ""))
        binding.inputRegEmail.setText(regPref.getString(KEY_EMAIL, ""))
        binding.inputBirthDate.setText(regPref.getString(KEY_BIRTH_DATE, ""))
        binding.inputRegUsername.setText(regPref.getString(KEY_USERNAME, ""))
        binding.inputRegPassword.setText(regPref.getString(KEY_PASSWORD, ""))
        binding.inputConfirmPassword.setText(regPref.getString(KEY_CONFIRM_PASSWORD, ""))

        when (regPref.getString(KEY_GENDER, "")) {
            getString(R.string.gender_male) -> binding.rbMale.isChecked = true
            getString(R.string.gender_female) -> binding.rbFemale.isChecked = true
        }
    }

    companion object {
        const val PREF_NAME = "register_pref"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_BIRTH_DATE = "birth_date"
        const val KEY_GENDER = "gender"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        const val KEY_CONFIRM_PASSWORD = "confirm_password"
        const val KEY_VERIFIED = "is_verified"
    }
}