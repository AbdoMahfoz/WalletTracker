package com.example.wallettracker.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wallettracker.R
import com.example.wallettracker.ui.main.charts.ChartsViewFragment
import com.example.wallettracker.databinding.FragmentMainBinding
import com.example.wallettracker.logic.interfaces.IAuth
import com.example.wallettracker.ui.main.goals.GoalsViewFragment
import com.example.wallettracker.ui.main.spend.SpendHistoryFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

interface IFabConsumer {
    fun setupFab(fab : FloatingActionButton, navController: NavController)
}

class MainFragment : Fragment(), KodeinAware {
    private val frags = listOf(SpendHistoryFragment(), GoalsViewFragment(), ChartsViewFragment())
    private val fragNames = listOf("History", "Goal", "Summary")

    override val kodein: Kodein by closestKodein()
    private val auth by instance<IAuth>()

    private lateinit var binding : FragmentMainBinding
    private var lastFrag : IFabConsumer? = null
    private lateinit var fab : FloatingActionButton
    private lateinit var toolbar : Toolbar
    private lateinit var nav : NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        fab = binding.mainFab
        toolbar = binding.toolBar
        nav = findNavController()
        fab.hide()
        (activity as MainActivity).setSupportActionBar(binding.toolBar)
        lastFrag?.run { setupFab(fab, nav) }
        NavigationUI.setupActionBarWithNavController(requireActivity() as MainActivity, nav)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logutMenuItem -> {
                auth.logOut()
                nav.navigate(MainFragmentDirections.actionMainFragmentToAuthActivity())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.pager.adapter = PageAdapter(childFragmentManager, frags, fragNames)
        binding.tabLayout.setupWithViewPager(binding.pager)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            fun handleSelection(i : Int) {
                if(frags[i] is IFabConsumer){
                    val consumer = frags[i] as IFabConsumer
                    consumer.setupFab(fab, findNavController())
                    lastFrag = consumer
                }
                else {
                    fab.hide()
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) = handleSelection(tab!!.position)
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) = handleSelection(tab!!.position)
        })
        if(lastFrag == null && frags[0] is IFabConsumer){
            val consumer = frags[0] as IFabConsumer
            consumer.setupFab(fab, findNavController())
            lastFrag = consumer
        }
    }
    /*
    override fun onPause() {
        super.onPause()
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        fab.hide()
    }
    override fun onResume() {
        super.onResume()
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
    }
    */
    class PageAdapter(fm: FragmentManager,
                      private val frags: List<Fragment>,
                      private val fragNames: List<String>)
        : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = frags.count()
        override fun getItem(i: Int) = frags[i]
        override fun getPageTitle(position: Int) = fragNames[position]
    }
}

