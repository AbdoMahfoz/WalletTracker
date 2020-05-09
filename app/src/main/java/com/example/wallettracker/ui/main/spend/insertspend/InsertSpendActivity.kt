package com.example.wallettracker.ui.main.spend.insertspend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wallettracker.R

class InsertSpendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_spend)
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "New Transaction"
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
