package com.example.ala_cheetos.layanan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ala_cheetos.data.model.LayananModel
import com.example.ala_cheetos.databinding.ItemLayananBinding
import java.text.NumberFormat
import java.util.Locale

private fun formatRupiah(value: Long): String =
    "Rp " + NumberFormat.getInstance(Locale.forLanguageTag("id-ID")).format(value)

class LayananAdapter(
    private val items: List<LayananModel>,
    private val onClick: (LayananModel) -> Unit
) : RecyclerView.Adapter<LayananAdapter.VH>() {

    class VH(val b: ItemLayananBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemLayananBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvNamaLayanan.text = item.nama
        holder.b.tvDurasi.text = "${item.durasiMenit} menit"
        holder.b.tvHarga.text = formatRupiah(item.harga)
        holder.b.root.setOnClickListener { onClick(item) }
    }
}