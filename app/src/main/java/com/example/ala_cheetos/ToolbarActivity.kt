package com.example.ala_cheetos

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

/** Dasar activity bertoolbar (Toolbar + tombol back). Dipakai WebView dan activity detail. */
abstract class ToolbarActivity : AppCompatActivity() {

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