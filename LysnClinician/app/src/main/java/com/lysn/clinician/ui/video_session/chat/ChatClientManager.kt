package com.lysn.clinician.ui.video_session.chat

import android.content.Context
import android.util.Log
import com.lysn.clinician.utils.MixPanelData
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ChatClientListener
import com.twilio.chat.ErrorInfo

interface TaskCompletionListener<T, U> {
    fun onSuccess(t: T)
    fun onError(u: U)
}
class ChatClientManager(private val context: Context) {
    private var chatClient: ChatClient? = null

    fun getChatClient(): ChatClient? {
        return chatClient
    }

     fun buildClient(
         token: String,
         listener: TaskCompletionListener<ChatClient?, String?>
    ) {

         if(chatClient!=null) {
             listener.onSuccess(null)
             return
         }
         val props = ChatClient.Properties.Builder()
             .createProperties()

             ChatClient.create(
                 context.applicationContext,
                 token,
                 props,
                 object : CallbackListener<ChatClient?>() {
                     override fun onSuccess(chatClient: ChatClient?) {
                         this@ChatClientManager.chatClient = chatClient
                         listener.onSuccess(null)
                     }

                     override fun onError(errorInfo: ErrorInfo?) {
                         if (errorInfo != null) {
                             listener.onError(errorInfo.message)
                         }
                     }
                 }
             )
    }
    fun shutdown() {
        if (chatClient != null) {
            chatClient!!.shutdown()
            MixPanelData.getInstance(context).addEvent(MixPanelData.CHAT_CONNECTION_SHUTDOWN)
            chatClient = null
        }
    }
}