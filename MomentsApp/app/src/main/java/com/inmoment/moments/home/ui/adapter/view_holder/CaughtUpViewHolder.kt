package com.inmoment.moments.home.ui.adapter.view_holder

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.inmoment.moments.R
import com.inmoment.moments.framework.extensions.setSafeOnClickListener


class CaughtUpViewHolder(val v: View, recyclerView: RecyclerView) : RecyclerView.ViewHolder(v) {
    init {
        val youAreCaughtUpButton = v.findViewById<AppCompatButton>(R.id.youAreCaughtUpButton)
        youAreCaughtUpButton.setSafeOnClickListener {
            recyclerView.scrollToPosition(0)
        }
    }
}