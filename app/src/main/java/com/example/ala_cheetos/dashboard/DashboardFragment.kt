package com.example.ala_cheetos.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ala_cheetos.MainActivity
import com.example.ala_cheetos.R
import com.example.ala_cheetos.databinding.FragmentDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        // Toolbar di Fragment harus lewat requireActivity() as AppCompatActivity
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.appBar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_dashboard)

        // Tombol pintasan: pindah tab bottom navigation
        // (listener di BaseActivity yang mengganti fragment-nya)
        binding.btnKaryawan.setOnClickListener { selectTab(R.id.nav_karyawan) }
        binding.btnProfile.setOnClickListener { selectTab(R.id.nav_profile) }
        binding.btnLogout.setOnClickListener { showLogoutDialog() }
    }

    private fun selectTab(menuId: Int) {
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            .selectedItemId = menuId
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                sharedPref.edit().clear().apply()
                dialog.dismiss()

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish() // tutup BaseActivity, bukan hanya fragment
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}