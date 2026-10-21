package com.example.ala_cheetos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

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