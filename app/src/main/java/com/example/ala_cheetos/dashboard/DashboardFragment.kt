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
import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import com.example.ala_cheetos.laporan.LaporanPdfGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ala_cheetos.layanan.LayananActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences

    private val dataTransaksi = listOf(
        Transaksi("Layanan Hair Spa — Rina", "Hari ini · 09.18", "+ Rp 275.000", true),
        Transaksi("Belanja bahan color cream", "Hari ini · 08.42", "− Rp 680.000", false),
        Transaksi("Layanan Creambath — Dewi", "Kemarin · 16.05", "+ Rp 150.000", true),
        Transaksi("Layanan Potong Rambut — Budi", "Kemarin · 14.30", "+ Rp 60.000", true),
        Transaksi("Bayar listrik salon", "Kemarin · 10.00", "− Rp 850.000", false),
        Transaksi("Layanan Hair Coloring — Sari", "2 hari lalu · 15.20", "+ Rp 450.000", true)
    )

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
        binding.btnExportPdf.setOnClickListener { exportPdf() }
        binding.menuKaryawan.setOnClickListener { selectTab(R.id.nav_karyawan) }
        binding.menuProfil.setOnClickListener { selectTab(R.id.nav_profile) }
        binding.menuLogout.setOnClickListener { showLogoutDialog() }
        binding.rvTransaksi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransaksi.adapter = TransaksiAdapter(dataTransaksi) { trx ->
            Toast.makeText(requireContext(), trx.title, Toast.LENGTH_SHORT).show()
        }
        binding.menuLayanan.setOnClickListener {
            startActivity(Intent(requireContext(), LayananActivity::class.java))
        }
    }

    private fun selectTab(menuId: Int) {
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            .selectedItemId = menuId
    }

    /** Buat laporan keuangan PDF, simpan ke Downloads, lalu tawarkan untuk membukanya. */
    private fun exportPdf() {
        val ctx = requireContext()
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val namaFile = "Laporan_Keuangan_Oscardo_$stamp.pdf"

        val pdf = LaporanPdfGenerator.buatPdf(ctx)
        val hasil = try {
            LaporanPdfGenerator.simpan(ctx, pdf, namaFile)
        } finally {
            pdf.close()
        }

        if (hasil == null) {
            Toast.makeText(ctx, R.string.pdf_failed, Toast.LENGTH_LONG).show()
            return
        }

        val pesan = if (hasil.diDownloads) {
            getString(R.string.pdf_saved_downloads, hasil.lokasi)
        } else {
            getString(R.string.pdf_saved_app_folder, hasil.lokasi)
        }
        val dialog = AlertDialog.Builder(ctx)
            .setTitle(R.string.pdf_saved_title)
            .setMessage(pesan)
            .setNegativeButton(R.string.btn_close, null)
        hasil.uri?.let { uri ->
            dialog.setPositiveButton(R.string.btn_open) { _, _ -> bukaPdf(uri) }
        }
        dialog.show()
    }

    private fun bukaPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.pdf_no_viewer, Toast.LENGTH_LONG).show()
        }
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