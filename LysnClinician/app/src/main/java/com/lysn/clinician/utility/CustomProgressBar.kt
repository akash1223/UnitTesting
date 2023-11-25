package com.lysn.clinician.utility


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.lysn.clinician.R


class CustomProgressBar(context: Context) : Dialog(context) {

    init {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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