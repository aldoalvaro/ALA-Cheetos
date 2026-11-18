package com.example.ala_cheetos.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.ala_cheetos.MainActivity
import com.example.ala_cheetos.R
import com.example.ala_cheetos.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val pages = listOf(
        OnboardingPage(R.drawable.ic_dashboard, R.string.onb1_title, R.string.onb1_desc),
        OnboardingPage(R.drawable.ic_karyawan, R.string.onb2_title, R.string.onb2_desc),
        OnboardingPage(R.drawable.ic_lock, R.string.onb3_title, R.string.onb3_desc)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.viewPager.adapter = OnboardingAdapter(pages)
        binding.dotsIndicator.attachTo(binding.viewPager)

        // Slide terakhir: tombol jadi "Mulai" dan "Lewati" disembunyikan
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val last = position == pages.lastIndex
                binding.btnNext.setText(if (last) R.string.onb_start else R.string.onb_next)
                binding.btnSkip.visibility = if (last) View.INVISIBLE else View.VISIBLE
            }
        })

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < pages.lastIndex) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }
        binding.btnSkip.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        // Pref terpisah dari user_pref karena user_pref dihapus saat logout
        getSharedPreferences("app_pref", MODE_PRIVATE)
            .edit().putBoolean("onboarding_done", true).apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}