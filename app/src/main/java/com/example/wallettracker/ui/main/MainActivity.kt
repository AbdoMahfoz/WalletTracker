package com.example.wallettracker.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wallettracker.R
import com.example.wallettracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var nav : NavController
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainFab.hide()
        setSupportActionBar(binding.toolBar)
        nav = findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, nav)
    }
    fun getFab() = binding.mainFab
    fun getToolBar() = binding.toolBar
    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp()
    }
}
