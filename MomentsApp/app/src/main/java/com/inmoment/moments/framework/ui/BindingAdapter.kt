package com.inmoment.moments.framework.ui

import android.view.View
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.home.model.Feed

class BindingAdapter {


    companion object {
        val TAG = "BindingAdapter"

        @BindingAdapter("app:errorMsg")
        @JvmStatic
        fun setError(view: TextInputLayout, msg: String) {
            view.error = msg
        }

        @BindingAdapter("app:visibilityShimmerAnimation")
        @JvmStatic
        fun visibilityShimmerAnimation(view: View, feed: Feed?) {
            if (feed != null) {
                val simmerView = view as ShimmerFrameLayout
                if (feed.loadingEffect) {
                    simmerView.startShimmerAnimation()
                    simmerView.visibility = View.VISIBLE
                } else {
                    simmerView.stopShimmerAnimation()
                    simmerView.visibility = View.GONE
                }
            } else {
                Logger.i(TAG, "visibilityShimmerAnimation => feed is null")
            }
        }
    }

}

