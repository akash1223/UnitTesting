package com.inmoment.moments.framework.ui

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class TopLinearSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }
}