package com.example.wallettracker.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dateTime")
fun textViewDateTime(view : TextView, date : Date){
    val dateFormat = SimpleDateFormat("d/MM/YYYY\nh:mm a", Locale.US)
    view.text = dateFormat.format(date)
}

@BindingAdapter("time")
fun textViewTime(view : TextView, date : Date){
    val dateFormat = SimpleDateFormat("h:mm a", Locale.US)
    view.text = dateFormat.format(date)
}

@BindingAdapter("dateTimeOneLine")
fun textViewDateTimeOneLine(view : TextView, date : Date){
    val dateFormat = SimpleDateFormat("d/MM/YYYY h:mm a", Locale.US)
    view.text = dateFormat.format(date)
}

@BindingAdapter("date")
fun textViewDate(view : TextView, date : Date){
    val dateFormat = SimpleDateFormat("d/MM/YYYY", Locale.US)
    view.text = dateFormat.format(date)
}

@BindingAdapter("longDate")
fun textViewLongDate(view : TextView, date : Date){
    val dateFormat = SimpleDateFormat("E, dd MMM yyyy", Locale.US)
    view.text = dateFormat.format(date)
}