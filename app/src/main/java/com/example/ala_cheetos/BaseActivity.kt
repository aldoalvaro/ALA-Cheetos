package com.example.ala_cheetos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.ala_cheetos.dashboard.DashboardFragment
import com.example.ala_cheetos.databinding.ActivityBaseBinding
import com.example.ala_cheetos.karyawan.KaryawanFragment
import com.example.ala_cheetos.profile.ProfileFragment

/** Activity utama setelah login: menampung fragment + BottomNavigation. */
class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // bottom = 0 supaya tidak ada jarak kosong di bawah BottomNavigation
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        /** DashboardFragment sebagai fragment default */
        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    replaceFragment(DashboardFragment())
                    true
                }
                R.id.nav_karyawan -> {
                    replaceFragment(KaryawanFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false // return false jika tidak ada item yang diklik
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            // addToBackStack(null) sengaja tidak dipakai agar tombol back langsung keluar aplikasi
            .commit()
    }
}