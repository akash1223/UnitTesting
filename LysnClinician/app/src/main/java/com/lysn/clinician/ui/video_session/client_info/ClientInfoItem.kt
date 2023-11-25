package com.lysn.clinician.ui.video_session.client_info


import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.annotation.DrawableRes
import androidx.core.text.toSpannable
import com.lysn.clinician.R
import com.lysn.clinician.utility.extensions.removeUnderlines
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_client_info.*


class ClientInfoItem(
    @DrawableRes private val icon: Int,
    private val infoItem: String,
    private val sectionType: String = "client"
) : Item() {

    override fun getLayout() = R.layout.item_client_info

    override fun bind(viewBinding: GroupieViewHolder, position: Int) {

            if (sectionType == "client" && icon == R.drawable.ic_phone_client_info) {
            viewBinding.txt_client_info_item.autoLinkMask = Linkify.PHONE_NUMBERS
        } else if (sectionType == "client" && icon == R.drawable.ic_email) {
            viewBinding.txt_client_info_item.autoLinkMask = Linkify.EMAIL_ADDRESSES
        }
        viewBinding.txt_client_info_item.text = infoItem
        viewBinding.txt_client_info_item.text.toSpannable().removeUnderlines()
        viewBinding.txt_client_info_item.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    }

}
