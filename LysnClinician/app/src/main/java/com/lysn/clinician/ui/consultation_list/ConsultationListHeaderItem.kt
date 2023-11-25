package com.lysn.clinician.ui.consultation_list

import android.view.View
import androidx.annotation.StringRes
import com.lysn.clinician.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_consultation_list_header.*


class ConsultationListHeaderItem(@StringRes private val titleStringResId: Int) : Item(titleStringResId.toLong()) {

    override fun getLayout()= R.layout.item_consultation_list_header

    override fun bind(viewBinding: GroupieViewHolder, position: Int) {
        viewBinding.txt_header.setText(titleStringResId)
    }
}