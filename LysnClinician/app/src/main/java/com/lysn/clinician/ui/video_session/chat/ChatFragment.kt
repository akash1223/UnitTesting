package com.lysn.clinician.ui.video_session.chat

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lysn.clinician.R
import com.lysn.clinician.databinding.FragmentChatBinding
import com.lysn.clinician.model.VideoSessionTokenResponse
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.ui.video_session.ARG_VIDEO_SESSION_TOKEN
import com.lysn.clinician.utility.extensions.*
import com.lysn.clinician.utils.AppConstants
import com.twilio.chat.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.getKoin
import timber.log.Timber


const val ARG_CONSULTATION_DETAILS = "consultation_details"

class ChatFragment : BaseFragment(), ChannelListenerWrapperInterface {

    private val mViewModel: ChatViewModel by viewModel()
    private lateinit var mBinding: FragmentChatBinding

    private val mapItem = mutableListOf<Item>()
    private var messageAdapter: GroupAdapter<GroupieViewHolder>? = null

    var currentChannel: Channel? = null
    var messagesObject: Messages? = null
    private val scope = getKoin().getScope(AppConstants.VIDEO_SESSION_SCOPED_SESSION_ID)
    private val chatChannelManager =
        scope.getScope(AppConstants.VIDEO_SESSION_SCOPED_SESSION_ID).get<ChannelManager>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chat, container, false
        )
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = viewLifecycleOwner
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapItem.clear()
        setup()
    }

    override fun setup() {
        messageAdapter = null
        messageAdapter = GroupAdapter<GroupieViewHolder>()
        mBinding.messageRecycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
        mBinding.showLoading = true
        arguments?.takeIf { it.containsKey(ARG_VIDEO_SESSION_TOKEN) }?.apply {
            justTry {
                val videoSessionTokenResponse: VideoSessionTokenResponse? =
                    this.getParcelable(ARG_VIDEO_SESSION_TOKEN)
                chatChannelManager.channelLoadFinished.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        if (chatChannelManager.generalChannel != null) {
                            currentChannel = chatChannelManager.generalChannel
                            loadMessages()
                        }
                        mBinding.showLoading = false
                    }

                })

            }
        }
        mViewModel.onSendMessageObservable.observe(viewLifecycleOwner, Observer {
            sendMessage()
        })

    }

    private fun loadMessages() {

        currentChannel?.addListener(ChannelListenerWrapper(this))
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(1000)
            messagesObject = currentChannel?.messages
            messagesObject?.getLastMessages(20, object : CallbackListener<List<Message?>>() {
                override fun onSuccess(messageList: List<Message?>) {
                    mViewModel.isChannelConnected.value = true
                    mapItem.addAll(messageList.toChatMessageItem())
                    messageAdapter?.addAll(mapItem)
                    mBinding.messageRecycleView.scrollToBottom()
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    Timber.d(" chatChannel created")
                    showDebugToast("chatChannel created")
                }
            })
        }

    }

    private fun sendMessage() {
        if (!mViewModel.isChannelConnected.value!!) {
            mBinding.messageInput.clearTextInput()
            return
        }
        val messageText: String = mBinding.messageInput.getTextInput()
        val messageOptions = Message.options().withBody(messageText)
        messagesObject!!.sendMessage(messageOptions, null)
        mBinding.messageInput.clearTextInput()
    }

    private fun List<Message?>.toChatMessageItem(): List<ChatMessageItem> {
        return this.map {
            ChatMessageItem(it?.author!!, it)
        }
    }

    override fun onMessageAdded(msg: Message?) {
        mapItem.add(ChatMessageItem(msg?.author!!, msg))
        messageAdapter?.update(mapItem)
        mBinding.messageRecycleView.scrollToBottom()
    }


}