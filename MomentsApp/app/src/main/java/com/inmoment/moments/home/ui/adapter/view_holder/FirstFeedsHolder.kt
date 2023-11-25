package com.inmoment.moments.home.ui.adapter.view_holder

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.inmoment.moments.R
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.program.model.Program


class FirstFeedsHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val cxLabel: TextView = v.findViewById(R.id.cxLabel)
    private val tvNoOfDays: TextView = v.findViewById(R.id.tvNoOfDays)
    private val cxIV: TextView = v.findViewById(R.id.cxIV)
    private val allMomentsTV: TextView = v.findViewById(R.id.allMomentsTV)

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    fun bindData(
        context: FragmentActivity,
        feed: Feed,
        program: Program
    ) {
        //cxLabel.text = feeds[position].accountName?.trim()

        tvNoOfDays.text = feed.noOfDays
        (cxIV.background as GradientDrawable).setColor(program.domainColor)
        cxIV.text = program.cloudShortText
        cxLabel.text = program.programName
        allMomentsTV.text = feed.accountName
        //Moments
        cxLabel.setOnClickListener {
            context.findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_AccountProgramPickerFragment)
        }
    }
}