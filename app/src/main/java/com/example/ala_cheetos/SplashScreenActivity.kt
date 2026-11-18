package com.example.ala_cheetos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ala_cheetos.databinding.ActivitySplashScreenBinding
import com.example.ala_cheetos.onboarding.OnboardingActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Simulasi loading + cek status login dan onboarding
        lifecycleScope.launch {
            delay(2000) // 2 detik

            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val isLogin = sharedPref.getBoolean("isLogin", false)
            val onboardingDone = getSharedPreferences("app_pref", MODE_PRIVATE)
                .getBoolean("onboarding_done", false)

            val intent = when {
                isLogin -> Intent(this@SplashScreenActivity, BaseActivity::class.java)
                !onboardingDone -> Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
                else -> Intent(this@SplashScreenActivity, MainActivity::class.java)
            }
            startActivity(intent)
            finish() // Splash tidak bisa di-back
        }
    }
}