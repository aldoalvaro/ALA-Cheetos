package com.example.ala_cheetos

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

/** Dasar semua activity selain MainActivity: memasang Toolbar + tombol back. */
abstract class BaseActivity : AppCompatActivity() {

    protected fun setupToolbar(toolbar: MaterialToolbar, title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
