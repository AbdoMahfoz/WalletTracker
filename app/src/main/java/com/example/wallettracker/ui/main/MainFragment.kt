package com.example.wallettracker.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.wallettracker.R
import com.example.wallettracker.ui.charts.ChartsViewFragment
import com.example.wallettracker.databinding.FragmentMainBinding
import com.example.wallettracker.ui.goals.GoalsViewFragment
import com.example.wallettracker.ui.spend.SpendHistoryFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

interface IFabConsumer {
    fun setupFab(fab : FloatingActionButton, navController: NavController)
}

class MainFragment : Fragment() {
    private val frags = listOf(SpendHistoryFragment(), GoalsViewFragment(), ChartsViewFragment())
    private val fragNames = listOf("History", "Goal", "Summary")

    private lateinit var binding : FragmentMainBinding
    private var lastFrag : IFabConsumer? = null
    private val fab get() = (requireNotNull(this.activity) as MainActivity).getFab()
    private val toolbar get() = (requireNotNull(this.activity) as MainActivity).getToolBar()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        fab.hide()
        lastFrag?.run { setupFab(fab, findNavController()) }
        return binding.root
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

