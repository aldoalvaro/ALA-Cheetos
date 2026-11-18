package com.example.ala_cheetos.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ala_cheetos.R
import com.example.ala_cheetos.databinding.ItemTransaksiBinding

data class Transaksi(
    val title: String,
    val time: String,
    val amount: String,
    val isIncome: Boolean
)

class TransaksiAdapter(
    private val items: List<Transaksi>,
    private val onItemClick: (Transaksi) -> Unit
) : RecyclerView.Adapter<TransaksiAdapter.VH>() {

    class VH(val b: ItemTransaksiBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvTitle.text = item.title
        holder.b.tvTime.text = item.time
        holder.b.tvAmount.text = item.amount

        // Hijau = pemasukan, merah = pengeluaran
        val warna = if (item.isIncome) R.color.oscardo_green else R.color.oscardo_red
        holder.b.tvAmount.setTextColor(ContextCompat.getColor(holder.b.root.context, warna))

        holder.b.root.setOnClickListener { onItemClick(item) }
    }
}