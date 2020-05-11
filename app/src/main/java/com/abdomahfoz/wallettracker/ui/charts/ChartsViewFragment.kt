package com.abdomahfoz.wallettracker.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.abdomahfoz.wallettracker.R
import com.abdomahfoz.wallettracker.databinding.FragmentChartsViewBinding
import com.abdomahfoz.wallettracker.viewModels.ChartsViewModel
import com.abdomahfoz.wallettracker.viewModels.ViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ChartsViewFragment : Fragment() {
    private lateinit var binding : FragmentChartsViewBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_charts_view, container, false)
        val viewModel = ViewModelFactory.of(this).get(ChartsViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.lastMonthData.observe(viewLifecycleOwner, Observer {
            setUpBarChart(binding.barChart, it)
            setUpPieChart(binding.pieChart, it)
        })
        viewModel.allYearData.observe(viewLifecycleOwner, Observer {
            setUpBarChart(binding.barChart2, it)
            setUpPieChart(binding.pieChart2, it)
        })
        return binding.root
    }
    private fun setUpBarChart(barChart : BarChart,  elements : List<Pair<String, Double>>) {
        val entries = elements.mapIndexed { i, v -> BarEntry(i.toFloat(), v.second.toFloat()) }
        val labels = elements.map { it.first }
        val barDataSet = BarDataSet(entries, "Spend Summary").apply {
            setColors(*ColorTemplate.COLORFUL_COLORS)
            valueTextColor = Color.WHITE
        }
        barChart.apply {
            clear()
            xAxis.apply {
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float) = labels[value.toInt()]
                }
                textColor = Color.WHITE
            }
            xAxis.setDrawLabels(true)
            data = BarData(barDataSet)
            legend.isEnabled = false
            /*
            legend.textColor = Color.WHITE
            legend.setCustom(labels.mapIndexed { i, it ->
                LegendEntry().apply {
                    label = it
                    formColor = ColorTemplate.COLORFUL_COLORS[i % ColorTemplate.COLORFUL_COLORS.size]
                }
            })
            */
            //description.textColor = Color.WHITE
            description.isEnabled = false
            axisLeft.textColor = Color.WHITE
            //axisRight.textColor = Color.WHITE
            //axisRight.isEnabled = true
            axisRight.setDrawLabels(false)
        }
    }
    private fun setUpPieChart(pieChart: PieChart, elements : List<Pair<String, Double>>) {
        val data = elements.map { PieEntry(it.second.toFloat(), it.first) }
        val dataSet = PieDataSet(data, "Spend Summary").apply {
            setColors(*ColorTemplate.COLORFUL_COLORS)
            setDrawValues(true)
        }
        pieChart.apply {
            clear()
            this.data = PieData(dataSet)
            legend.textColor = Color.WHITE
            legend.setCustom(elements.mapIndexed { i, it ->
                LegendEntry().apply {
                    label = it.first
                    formColor = ColorTemplate.COLORFUL_COLORS[i % ColorTemplate.COLORFUL_COLORS.size]
                }
            })
            //legend.isEnabled = false
            //description.textColor = Color.WHITE
            description.isEnabled = false
            isDrawHoleEnabled = false
            setDrawEntryLabels(false)
        }
    }
}
