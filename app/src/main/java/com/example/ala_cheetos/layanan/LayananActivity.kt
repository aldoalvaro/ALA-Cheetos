package com.example.ala_cheetos.layanan

import android.os.Bundle
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ala_cheetos.R
import com.example.ala_cheetos.ToolbarActivity
import com.example.ala_cheetos.databinding.ActivityLayananBinding

class LayananActivity : ToolbarActivity() {

    private lateinit var binding: ActivityLayananBinding

    // Tiap Map = 1 baris ListView
    private val data = listOf(
        mapOf("nama" to "Potong Rambut Pria", "durasi" to "30 menit", "harga" to "Rp 60.000"),
        mapOf("nama" to "Potong Rambut Wanita", "durasi" to "45 menit", "harga" to "Rp 85.000"),
        mapOf("nama" to "Creambath", "durasi" to "60 menit", "harga" to "Rp 150.000"),
        mapOf("nama" to "Hair Spa", "durasi" to "75 menit", "harga" to "Rp 275.000"),
        mapOf("nama" to "Hair Coloring", "durasi" to "120 menit", "harga" to "Rp 450.000"),
        mapOf("nama" to "Smoothing", "durasi" to "150 menit", "harga" to "Rp 650.000"),
        mapOf("nama" to "Hair Mask", "durasi" to "45 menit", "harga" to "Rp 120.000"),
        mapOf("nama" to "Manicure", "durasi" to "40 menit", "harga" to "Rp 90.000")
    )

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

        // SimpleAdapter: map key -> id TextView di item_layanan.xml
        val adapter = SimpleAdapter(
            this,
            data,
            R.layout.item_layanan,
            arrayOf("nama", "durasi", "harga"),
            intArrayOf(R.id.tvNamaLayanan, R.id.tvDurasi, R.id.tvHarga)
        )
        binding.lvLayanan.adapter = adapter

        binding.lvLayanan.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, data[position]["nama"], Toast.LENGTH_SHORT).show()
        }
    }
}