package com.example.wallettracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.wallettracker.R

class MainActivity : AppCompatActivity() {
    private lateinit var nav : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nav = findNavController(R.id.navHostFragment)
    }
    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp()
    }
}
