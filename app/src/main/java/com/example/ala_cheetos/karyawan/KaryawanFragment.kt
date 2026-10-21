package com.example.ala_cheetos.karyawan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ala_cheetos.R
import com.example.ala_cheetos.databinding.FragmentKaryawanBinding

class KaryawanFragment : Fragment() {

    private var _binding: FragmentKaryawanBinding? = null
    private val binding get() = _binding!!

    private val data = listOf(
        Karyawan("Lina Oscardo", "Senior Hair Stylist", "0812 9400 1720", "Hadir"),
        Karyawan("Evie Mariana", "Hair Colorist", "0813 6612 4588", "Hadir"),
        Karyawan("Fatima", "Beauty Therapist", "0857 2088 9145", "Libur"),
        Karyawan("Jason Oscardo", "Kasir & Admin", "0819 8422 0751", "Cuti")
    )

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

        binding.rvKaryawan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKaryawan.adapter = KaryawanAdapter(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}