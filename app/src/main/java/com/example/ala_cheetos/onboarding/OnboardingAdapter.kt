package com.example.ala_cheetos.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.example.ala_cheetos.databinding.ItemOnboardingBinding

data class OnboardingPage(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    @StringRes val desc: Int
)

class OnboardingAdapter(private val pages: List<OnboardingPage>) :
    RecyclerView.Adapter<OnboardingAdapter.VH>() {

    class VH(val b: ItemOnboardingBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = pages.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val page = pages[position]
        holder.b.imgIcon.setImageResource(page.icon)
        holder.b.tvTitle.setText(page.title)
        holder.b.tvDesc.setText(page.desc)
    }
}