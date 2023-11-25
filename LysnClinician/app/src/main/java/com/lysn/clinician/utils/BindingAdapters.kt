package com.lysn.clinician.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.lysn.clinician.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BindingAdapters(context: Context) {

    companion object {
        @BindingAdapter("app:errorMsg")
        @JvmStatic
        fun setError(view: TextInputLayout, msg: String) {
            view.error = msg
        }

        @BindingAdapter("app:bindServerDate")
        @JvmStatic
        fun bindServerDate(textView: TextView, date: String?) {
            val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
            textView.text=parsedDate.format(
                DateTimeFormatter.ofPattern(
                    "EEEE, d MMM, h:mma",
                    Locale.UK
                )
            )
        }

        @BindingAdapter("loadImage")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: String?) {
            if(!imageUrl.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load(imageUrl).apply(RequestOptions().circleCrop())
                    .into(view)
            }
        }

        @BindingAdapter("app:bindCallType")
        @JvmStatic
        fun bindCallType(textView: TextView, callType: String?) {
            when {
                callType.equals("video") -> textView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_videocam,
                    0,
                    0,
                    0
                )
                callType.equals("phone") -> textView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_phone,
                    0,
                    0,
                    0
                )
                callType.equals("f2f") -> textView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_f2f,
                    0,
                    0,
                    0
                )
            }
        }

        @BindingAdapter("app:layoutCustomHeight")
        @JvmStatic
        fun layoutCustomHeight(view: ImageView, height: Float) {
            val layoutParams: android.view.ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = height.toInt()
            view.layoutParams = layoutParams
        }

        @BindingAdapter("app:layoutCustomWidth")
        @JvmStatic
        fun layoutCustomWidth(view: ImageView, height: Float) {
            val layoutParams: android.view.ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = height.toInt()
            view.layoutParams = layoutParams
        }

        @BindingAdapter("app:layoutPercentWidth")
        @JvmStatic
        fun layoutPercentWidth(view: View, height: Float) {
            val lp: ConstraintLayout.LayoutParams =
                view.layoutParams as ConstraintLayout.LayoutParams
            lp.matchConstraintPercentHeight = height
            view.layoutParams = lp
            view.postInvalidate()
        }
//        @BindingAdapter("app:statusBarHeight")
//        @JvmStatic
//        fun statusBarHeight(view: View) {
//            // status bar height
//
//            // status bar height
//            var statusBarHeight = 0
//            val resourceId: Int =
//                view.context.resources.getIdentifier("status_bar_height", "dimen", "android")
//            if (resourceId > 0) {
//               statusBarHeight = view.context.resources.getDimensionPixelSize(resourceId)
//                val params: ViewGroup.MarginLayoutParams = view!!.layoutParams as ViewGroup.MarginLayoutParams
//                params.topMargin = statusBarHeight
//            }
//        }

    }
}