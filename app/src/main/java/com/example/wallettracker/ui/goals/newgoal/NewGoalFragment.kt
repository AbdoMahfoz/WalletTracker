package com.example.wallettracker.ui.goals.newgoal

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.example.wallettracker.R
import com.example.wallettracker.database.GoalEntity
import com.example.wallettracker.databinding.FragmentNewGoalBinding
import com.example.wallettracker.viewModels.GoalsViewModel
import com.example.wallettracker.viewModels.ViewModelFactory
import java.util.*

class NewGoalFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentNewGoalBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_goal , container, false
        )
        binding.fromDate = Calendar.getInstance().time
        binding.toDate = Calendar.getInstance().time
        binding.changeFromButton.setOnClickListener { handleDateSelection(binding.fromDate!!, binding::setFromDate) }
        binding.changeToButton.setOnClickListener { handleDateSelection(binding.toDate!!, binding::setToDate) }
        val viewModel = ViewModelFactory.of(this).get(GoalsViewModel::class.java)
        binding.submitButton.setOnClickListener {
            viewModel.insert(GoalEntity(
                name = binding.nameEditText.text.toString(),
                start = binding.fromDate!!,
                end = binding.toDate!!,
                amount = binding.amountEditText.text.toString().toDouble()
            ))
            requireNotNull(activity).onBackPressed()
        }
        return binding.root
    }
    private fun handleDateSelection(date : Date, res : (Date) -> Unit) {
        val c = Calendar.getInstance()
        c.time = date
        DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            res(c.time)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }
}
