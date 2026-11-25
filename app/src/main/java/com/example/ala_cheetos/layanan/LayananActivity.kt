package com.example.ala_cheetos.layanan

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ala_cheetos.R
import com.example.ala_cheetos.ToolbarActivity
import com.example.ala_cheetos.data.api.SupabaseClient
import com.example.ala_cheetos.data.model.LayananModel
import com.example.ala_cheetos.databinding.ActivityLayananBinding
import com.example.ala_cheetos.databinding.DialogLayananBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.IOException

class LayananActivity : ToolbarActivity() {

    private lateinit var binding: ActivityLayananBinding
    private lateinit var adapter: LayananAdapter

    private val api get() = SupabaseClient.layananApi
    private val items = mutableListOf<LayananModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLayananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sama seperti BaseActivity: beri jarak untuk status bar / navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setupToolbar(binding.appBar.toolbar, getString(R.string.title_layanan))

        adapter = LayananAdapter(items) { showOptions(it) }
        binding.rvLayanan.layoutManager = LinearLayoutManager(this)
        binding.rvLayanan.adapter = adapter

        binding.btnTambah.setOnClickListener { showForm(null) }

        loadData()
    }

    // ---------- READ ----------
    private fun loadData() {
        lifecycleScope.launch {
            try {
                val res = api.getAll()
                if (res.isSuccessful) {
                    items.clear()
                    items.addAll(res.body().orEmpty())
                    adapter.notifyDataSetChanged()
                } else {
                    toast("Gagal memuat data (kode ${res.code()})")
                }
            } catch (e: IOException) {
                toast("Tidak ada koneksi internet")
            }
        }
    }

    // ---------- Pilihan Edit / Hapus ----------
    private fun showOptions(item: LayananModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle(item.nama)
            .setItems(arrayOf("Edit", "Hapus")) { _, which ->
                if (which == 0) showForm(item) else confirmDelete(item)
            }
            .show()
    }

    // ---------- Form Tambah / Edit ----------
    private fun showForm(existing: LayananModel?) {
        val form = DialogLayananBinding.inflate(layoutInflater)
        existing?.let {
            form.etNama.setText(it.nama)
            form.etDurasi.setText(it.durasiMenit.toString())
            form.etHarga.setText(it.harga.toString())
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(if (existing == null) "Tambah Layanan" else "Edit Layanan")
            .setView(form.root)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val nama = form.etNama.text.toString().trim()
                val durasi = form.etDurasi.text.toString().trim().toIntOrNull()
                val harga = form.etHarga.text.toString().trim().toLongOrNull()

                if (nama.isEmpty()) {
                    form.etNama.error = "Nama layanan wajib diisi"
                    return@setOnClickListener
                }
                if (durasi == null || durasi <= 0) {
                    form.etDurasi.error = "Durasi harus berupa angka > 0"
                    return@setOnClickListener
                }
                if (harga == null || harga < 0) {
                    form.etHarga.error = "Harga harus berupa angka"
                    return@setOnClickListener
                }

                save(existing, LayananModel(nama = nama, durasiMenit = durasi, harga = harga))
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    // ---------- CREATE / UPDATE ----------
    private fun save(existing: LayananModel?, body: LayananModel) {
        lifecycleScope.launch {
            try {
                val res = if (existing == null) api.insert(body)
                else api.update("eq.${existing.id}", body)

                if (res.isSuccessful) {
                    toast(if (existing == null) "Layanan ditambahkan" else "Layanan diperbarui")
                    loadData()
                } else {
                    toast("Gagal menyimpan (kode ${res.code()})")
                }
            } catch (e: IOException) {
                toast("Tidak ada koneksi internet")
            }
        }
    }

    // ---------- DELETE ----------
    private fun confirmDelete(item: LayananModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus layanan?")
            .setMessage("Layanan ${item.nama} akan dihapus permanen.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ -> delete(item) }
            .show()
    }

    private fun delete(item: LayananModel) {
        lifecycleScope.launch {
            try {
                val res = api.delete("eq.${item.id}")
                if (res.isSuccessful) {
                    toast("Layanan dihapus")
                    loadData()
                } else {
                    toast("Gagal menghapus (kode ${res.code()})")
                }
            } catch (e: IOException) {
                toast("Tidak ada koneksi internet")
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}