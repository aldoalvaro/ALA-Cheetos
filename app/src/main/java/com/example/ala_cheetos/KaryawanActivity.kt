package com.example.ala_cheetos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ala_cheetos.databinding.ActivityKaryawanBinding
import com.example.ala_cheetos.databinding.ItemKaryawanBinding

data class Karyawan(val name: String, val role: String, val phone: String, val status: String)

class KaryawanActivity : ToolbarActivity() {

    private lateinit var binding: ActivityKaryawanBinding

    private val data = listOf(
        Karyawan("Lina Oscardo", "Senior Hair Stylist", "0812 9400 1720", "Hadir"),
        Karyawan("Evie Mariana", "Hair Colorist", "0813 6612 4588", "Hadir"),
        Karyawan("Fatima", "Beauty Therapist", "0857 2088 9145", "Libur"),
        Karyawan("Jason Oscardo", "Kasir & Admin", "0819 8422 0751", "Cuti")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaryawanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.appBar.toolbar, getString(R.string.title_karyawan))

        binding.rvKaryawan.layoutManager = LinearLayoutManager(this)
        binding.rvKaryawan.adapter = KaryawanAdapter(data)
    }
}

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
        holder.b.tvStatus.backgroundTintList =
            android.content.res.ColorStateList.valueOf(ContextCompat.getColor(ctx, bg))
        holder.b.tvStatus.setTextColor(ContextCompat.getColor(ctx, fg))
    }
}
