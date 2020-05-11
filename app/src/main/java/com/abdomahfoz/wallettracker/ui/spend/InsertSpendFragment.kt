package com.abdomahfoz.wallettracker.ui.spend

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abdomahfoz.wallettracker.R
import com.abdomahfoz.wallettracker.databinding.FragmentInsertSpendBinding
import com.abdomahfoz.wallettracker.entities.SpendEntity
import com.abdomahfoz.wallettracker.entities.SpendEntity.Important
import com.abdomahfoz.wallettracker.ui.MainActivity
import com.abdomahfoz.wallettracker.viewModels.SpendViewModel
import com.abdomahfoz.wallettracker.viewModels.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class InsertSpendFragment : Fragment() {
    private val spendDate = Calendar.getInstance()
    private lateinit var binding : FragmentInsertSpendBinding
    private lateinit var viewModel: SpendViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_insert_spend, container, false
        )
        viewModel = ViewModelFactory.of(this).get(SpendViewModel::class.java)
        val mainActivity = activity as MainActivity
        mainActivity.setSupportActionBar(binding.toolBar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.date = spendDate.time
        handleDate()
        handleSubmit()
        val mapFrag = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFrag?.getMapAsync{
            it.apply {
                val sydney = LatLng(-33.852, 151.211)
                addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                moveCamera(CameraUpdateFactory.newLatLng(sydney))
            }
        }
        return binding.root
    }
    private fun handleDate() {
        binding.changeDateButton.setOnClickListener{
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{_, year, month, dayOfMonth ->
                spendDate.set(Calendar.YEAR, year)
                spendDate.set(Calendar.MONTH, month)
                spendDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.date = spendDate.time
                TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener{ _, hour, minute ->
                    spendDate.set(Calendar.HOUR_OF_DAY, hour)
                    spendDate.set(Calendar.MINUTE, minute)
                    binding.date = spendDate.time
                }, spendDate.get(Calendar.HOUR_OF_DAY), spendDate.get(Calendar.MINUTE), false).show()
            }, spendDate.get(Calendar.YEAR), spendDate.get(Calendar.MONTH), spendDate.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
    private fun handleSubmit() {
        binding.submitButton.setOnClickListener{
            viewModel.insertNewSpend(
                SpendEntity(
                    comment = binding.commentText.text.toString(),
                    amount = binding.amountEditText.text.toString().toDouble(),
                    date = spendDate.time,
                    importance = when {
                        binding.veryImportantCheckBox.isChecked -> Important.VeryImportant
                        binding.avgImportanceCheckBox.isChecked -> Important.AverageImportance
                        else -> Important.NotImportant
                    }
                )
            )
            findNavController().popBackStack()
            //requireNotNull(activity).onBackPressed()
        }
    }
}
