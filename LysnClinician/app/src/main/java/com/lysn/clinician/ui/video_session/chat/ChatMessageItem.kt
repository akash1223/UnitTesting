package com.lysn.clinician.ui.video_session.chat


import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.annotation.DrawableRes
import androidx.core.text.toSpannable
import com.lysn.clinician.R
import com.lysn.clinician.utility.extensions.removeUnderlines
import com.twilio.chat.Message
import com.twilio.chat.Messages
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_chat_message.*


class ChatMessageItem(var personName:String,val message: Message) : Item() {

    override fun getLayout() = R.layout.item_chat_message

    override fun bind(viewBinding: GroupieViewHolder, position: Int) {
        if(personName.contains(":"))
        {
            personName = personName.substring(0,personName.indexOf(":"))
        }
        viewBinding.txt_person_name.text = personName
        viewBinding.txt_message.text = message.messageBody
    }

}
