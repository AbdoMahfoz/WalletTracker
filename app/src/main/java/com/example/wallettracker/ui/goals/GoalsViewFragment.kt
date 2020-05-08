package com.example.wallettracker.ui.goals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController

import com.example.wallettracker.R
import com.example.wallettracker.database.GoalEntity
import com.example.wallettracker.databinding.FragmentGoalsViewBinding
import com.example.wallettracker.databinding.GoalItemBinding
import com.example.wallettracker.ui.main.IFabConsumer
import com.example.wallettracker.ui.main.MainFragmentDirections
import com.example.wallettracker.utils.GenericRecyclerAdapter
import com.example.wallettracker.viewModels.GoalsViewModel
import com.example.wallettracker.viewModels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GoalsViewFragment : Fragment(), IFabConsumer {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentGoalsViewBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_goals_view, container, false
        )
        val viewModel = ViewModelFactory.of(this).get(GoalsViewModel::class.java)
        val adapter = GenericRecyclerAdapter.create<GoalEntity, GoalItemBinding>(R.layout.goal_item)
        binding.recyclerView.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.goals.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        return binding.root
    }
    override fun setupFab(fab: FloatingActionButton, navController: NavController) {
        fab.show()
        fab.setOnClickListener {
            navController.navigate(MainFragmentDirections.actionMainFragmentToNewGoalActivity())
        }
    }
}
