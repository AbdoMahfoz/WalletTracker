package com.example.wallettracker.ui.spend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController

import com.example.wallettracker.R
import com.example.wallettracker.viewModels.ViewModelFactory
import com.example.wallettracker.databinding.FragmentSpendHistoryBinding
import com.example.wallettracker.ui.main.IFabConsumer
import com.example.wallettracker.ui.main.MainFragmentDirections
import com.example.wallettracker.utils.GenericRecyclerAdapter
import com.example.wallettracker.viewModels.SpendViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SpendHistoryFragment : Fragment(), IFabConsumer {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentSpendHistoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_spend_history, container, false
        )
        val viewModel = ViewModelFactory.of(this).get(SpendViewModel::class.java)
        val adapter = GenericRecyclerAdapter(
            SpendHistoryAdapterUtil::viewHolderFactory, SpendHistoryAdapterUtil::viewHolderType
        )
        binding.recyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.spendHistory.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        return binding.root
    }
    override fun setupFab(fab: FloatingActionButton, navController: NavController) {
        fab.show()
        fab.setOnClickListener {
            navController.navigate(MainFragmentDirections.actionMainFragmentToInsertSpendActivity())
        }
    }
}
