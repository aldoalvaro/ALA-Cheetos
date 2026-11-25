package com.example.ala_cheetos.karyawan

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ala_cheetos.R
import com.example.ala_cheetos.data.api.SupabaseClient
import com.example.ala_cheetos.data.model.KaryawanModel
import com.example.ala_cheetos.databinding.DialogKaryawanBinding
import com.example.ala_cheetos.databinding.FragmentKaryawanBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.IOException

class KaryawanFragment : Fragment() {

    private var _binding: FragmentKaryawanBinding? = null
    private val binding get() = _binding!!

    private val api get() = SupabaseClient.karyawanApi
    private val items = mutableListOf<KaryawanModel>()
    private lateinit var adapter: KaryawanAdapter
    private val statusList = arrayOf("Hadir", "Libur", "Cuti")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKaryawanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.appBar.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_karyawan)

        adapter = KaryawanAdapter(items) { showOptions(it) }
        binding.rvKaryawan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKaryawan.adapter = adapter

        binding.btnTambah.setOnClickListener { showForm(null) }

        loadData()
    }

    // ---------- READ ----------
    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getAll()
                if (res.isSuccessful) {
                    items.clear()
                    items.addAll(res.body().orEmpty())
                    adapter.notifyDataSetChanged()
                    updateSummary()
                } else {
                    toast("Gagal memuat data (kode ${res.code()})")
                }
            } catch (e: IOException) {
                toast("Tidak ada koneksi internet")
            }
        }
    }

    private fun updateSummary() {
        val hadir = items.count { it.status == "Hadir" }
        val libur = items.count { it.status == "Libur" }
        val cuti = items.count { it.status == "Cuti" }
        binding.tvTotalValue.text = "${items.size} orang"
        binding.tvTotalNote.text = "$hadir hadir · $libur libur · $cuti cuti"
    }

    // ---------- Pilihan Edit / Hapus ----------
    private fun showOptions(item: KaryawanModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(item.name)
            .setItems(arrayOf("Edit", "Hapus")) { _, which ->
                if (which == 0) showForm(item) else confirmDelete(item)
            }
            .show()
    }

    // ---------- Form Tambah / Edit ----------
    private fun showForm(existing: KaryawanModel?) {
        val form = DialogKaryawanBinding.inflate(layoutInflater)
        form.actStatus.setSimpleItems(statusList)
        form.actStatus.setText(existing?.status ?: statusList[0], false)
        existing?.let {
            form.etNama.setText(it.name)
            form.etRole.setText(it.role)
            form.etPhone.setText(it.phone)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "Tambah Karyawan" else "Edit Karyawan")
            .setView(form.root)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null) // listener dipasang manual agar dialog tidak menutup saat input salah
            .create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val nama = form.etNama.text.toString().trim()
                val role = form.etRole.text.toString().trim()
                val phone = form.etPhone.text.toString().trim()
                val status = form.actStatus.text.toString()

                if (nama.isEmpty()) {
                    form.etNama.error = "Nama wajib diisi"
                    return@setOnClickListener
                }
                if (role.isEmpty()) {
                    form.etRole.error = "Jabatan wajib diisi"
                    return@setOnClickListener
                }

                save(existing, KaryawanModel(name = nama, role = role, phone = phone, status = status))
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    // ---------- CREATE / UPDATE ----------
    private fun save(existing: KaryawanModel?, body: KaryawanModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = if (existing == null) api.insert(body)
                else api.update("eq.${existing.id}", body)

                if (res.isSuccessful) {
                    toast(if (existing == null) "Karyawan ditambahkan" else "Karyawan diperbarui")
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
    private fun confirmDelete(item: KaryawanModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus karyawan?")
            .setMessage("Data ${item.name} akan dihapus permanen.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ -> delete(item) }
            .show()
    }

    private fun delete(item: KaryawanModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.delete("eq.${item.id}")
                if (res.isSuccessful) {
                    toast("Karyawan dihapus")
                    loadData()
                } else {
                    toast("Gagal menghapus (kode ${res.code()})")
                }
            } catch (e: IOException) {
                toast("Tidak ada koneksi internet")
            }
        }
    }

    private fun toast(msg: String) {
        if (_binding != null) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}