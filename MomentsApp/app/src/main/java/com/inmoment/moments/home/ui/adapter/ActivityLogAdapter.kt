package com.inmoment.moments.home.ui.adapter

import android.content.Context
import android.text.Spanned
import com.inmoment.moments.R
import com.inmoment.moments.databinding.ListActivityLogBinding
import com.inmoment.moments.framework.ui.GenericRecycleAdapter
import com.inmoment.moments.home.model.ActivityLogModel
import com.lysn.clinician.utility.extensions.toHtmlSpan

abstract class ActivityLogAdapter(
    context: Context,
    mArrayList: MutableList<ActivityLogModel> = mutableListOf()
) :
    GenericRecycleAdapter<ActivityLogModel, ListActivityLogBinding>(context, mArrayList) {
    override val layoutResId = R.layout.list_activity_log

    override fun onBindData(
        model: ActivityLogModel,
        position: Int,
        dataBinding: ListActivityLogBinding
    ) {
        if (dataBinding != null) {

            dataBinding.activityIV.setImageResource(model.iconRes)
            dataBinding.activityTimeTV.text = model.timeAgo
            dataBinding.userInitialsTV.text = model.initialsName
            dataBinding.activityTV.text = model.description.toHtmlSpan()
        }
    }

    override fun onItemClick(model: ActivityLogModel, position: Int) {
    }
}