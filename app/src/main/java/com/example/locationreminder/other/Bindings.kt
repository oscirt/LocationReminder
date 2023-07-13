package com.example.locationreminder.other

import android.graphics.Paint
import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("strikeThrough")
fun strikeThrough(textView: TextView, checked: Boolean) {
    if (checked) {
        Log.d("TAG", "strikeThrough: check")
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}