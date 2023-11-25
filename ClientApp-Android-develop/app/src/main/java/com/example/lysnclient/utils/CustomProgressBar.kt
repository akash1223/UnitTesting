package com.example.lysnclient.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.lysnclient.R

class CustomProgressBar(con: Context) : Dialog(con) {

    init {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_progress_bar_view)
        setCancelable(false)
    }

    override fun dismiss() {
        super.dismiss()
    }
}
