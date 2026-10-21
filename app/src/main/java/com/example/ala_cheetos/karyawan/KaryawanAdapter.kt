package com.example.ala_cheetos.karyawan

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ala_cheetos.R
import com.example.ala_cheetos.databinding.ItemKaryawanBinding

data class Karyawan(val name: String, val role: String, val phone: String, val status: String)

class KaryawanAdapter(private val items: List<Karyawan>) :
    RecyclerView.Adapter<KaryawanAdapter.VH>() {

    class VH(val b: ItemKaryawanBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemKaryawanBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val ctx = holder.b.root.context
        holder.b.tvInitial.text = item.name.split(" ").take(2).joinToString("") { it.first().uppercase() }
        holder.b.tvName.text = item.name
        holder.b.tvRole.text = item.role
        holder.b.tvPhone.text = item.phone
        holder.b.tvStatus.text = item.status

        val (bg, fg) = when (item.status) {
            "Hadir" -> R.color.oscardo_green_light to R.color.oscardo_green
            "Cuti" -> R.color.oscardo_red_light to R.color.oscardo_red
            else -> R.color.oscardo_line to R.color.oscardo_gray
        }
        holder.b.tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, bg))
        holder.b.tvStatus.setTextColor(ContextCompat.getColor(ctx, fg))
    }
}