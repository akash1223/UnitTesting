package com.example.lysnclient.utils

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

/*This class used for set custom property using data binding in xml */
class BindingAdapters {

    companion object {
        // For set error message dynamically using data binding
        @BindingAdapter("app:errorMsg")
        @JvmStatic
        fun setError(view: TextInputLayout, msg: String) {
            view.error = msg
        }

        // Used for set bottom margin dynamically using data binding
        @BindingAdapter("layoutMarginTop")
        @JvmStatic
        fun setLayoutMarginTop(view: View, dimen: Float) {
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = dimen.toInt()
            view.layoutParams = layoutParams
        }

        // Used for set text with first letters capital of each word
        @ExperimentalStdlibApi
        @BindingAdapter("capitalizeText")
        @JvmStatic
        fun setFirstLatterCaps(view: RadioButton, value: String) {
            view.text = value.capitalizeWords()
        }

        @BindingAdapter("app:seekBarChangeListener")
        @JvmStatic
        fun setOnSeekBarChangeListener(
            view: AppCompatSeekBar,
            listener: SeekBar.OnSeekBarChangeListener
        ) {
            view.setOnSeekBarChangeListener(listener)
        }

        @BindingAdapter("app:imgSrc")
        @JvmStatic
        fun setImageViewResource(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }

        @BindingAdapter("app:layoutBackgroundColor")
        @JvmStatic
        fun setBackgroundColor(mView: FrameLayout, colorCode: Int) {
            mView.setBackgroundResource(colorCode)
        }

        @SuppressLint("SetTextI18n")
        @BindingAdapter("app:text")
        @JvmStatic
        fun setText(view: MaterialTextView, dateInput: String?) {
            dateInput?.let {
                view.text = "LAST TAKEN ${Utilities.convertDateFormat(it)}"
            }
        }
    }
}
