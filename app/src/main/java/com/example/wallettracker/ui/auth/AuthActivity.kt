package com.example.wallettracker.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wallettracker.R

class AuthActivity : AppCompatActivity() {
    private lateinit var nav : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        nav = findNavController(R.id.navHostFragment)
    }
}
