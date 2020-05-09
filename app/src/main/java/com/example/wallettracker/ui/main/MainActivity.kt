package com.example.wallettracker.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wallettracker.R
import com.example.wallettracker.databinding.ActivityMainBinding
import com.example.wallettracker.logic.interfaces.IAuth
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {
    private lateinit var nav : NavController
    private lateinit var binding : ActivityMainBinding
    override val kodein: Kodein by closestKodein()
    private val auth by instance<IAuth>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainFab.hide()
        setSupportActionBar(binding.toolBar)
        nav = findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, nav)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logutMenuItem -> {
                auth.logOut()
                nav.navigate(MainFragmentDirections.actionMainFragmentToAuthActivity())
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getFab() = binding.mainFab
    fun getToolBar() = binding.toolBar
    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp()
    }


}
