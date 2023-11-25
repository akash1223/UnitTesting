package com.lysn.clinician.ui.video_session

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson

import com.lysn.clinician.BuildConfig
import com.lysn.clinician.R
import com.lysn.clinician.databinding.ActivityVideoSessionBinding
import com.lysn.clinician.model.VideoSessionTokenResponse
import com.lysn.clinician.ui.base.BaseActivity
import com.lysn.clinician.ui.join_consultation.HelpDialogFragment
import com.lysn.clinician.ui.join_consultation.SwitchCameraDialogFragment
import com.lysn.clinician.ui.join_consultation.SwitchCameraInterface
import com.lysn.clinician.ui.video_session.chat.ChannelManager
import com.lysn.clinician.ui.video_session.chat.ChatClientManager
import com.lysn.clinician.ui.video_session.chat.TaskCompletionListener
import com.lysn.clinician.utility.CameraCapturerCompat
import com.lysn.clinician.utility.KeyboardEventListener
import com.lysn.clinician.utility.extensions.*
import com.lysn.clinician.utils.*
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo
import com.twilio.chat.StatusListener
import com.twilio.video.*
import kotlinx.android.synthetic.main.activity_video_session.*
import kotlinx.android.synthetic.main.activity_video_session.view.*
import kotlinx.android.synthetic.main.layout_room_video_view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin
import timber.log.Timber
import kotlin.properties.Delegates


class VideoSessionActivity : BaseActivity(), RemoteParticipantInterface, SwitchCameraInterface {

    private val scope = getKoin().getOrCreateScope(
        AppConstants.VIDEO_SESSION_SCOPED_SESSION_ID, named(AppConstants.VIDEO_SESSION_SCOPED_NAME)
    )
    private val mViewModel: VideoSessionViewModel by viewModel()
    private val chatClientManager = scope.getScope(AppConstants.VIDEO_SESSION_SCOPED_SESSION_ID).get<ChatClientManager>()
    private val chatChannelManager = scope.getScope(AppConstants.VIDEO_SESSION_SCOPED_SESSION_ID).get<ChannelManager>()

    /*
     Test Data
     private var accessToken ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTS2ZmZmQ4MTE1NDNmZDI4NDkzMWU3MzIxZWU3MGI5NmYyLTE1OTg1NTAxMjEiLCJpc3MiOiJTS2ZmZmQ4MTE1NDNmZDI4NDkzMWU3MzIxZWU3MGI5NmYyIiwic3ViIjoiQUMyOTA4MGJiMTcwZTY3MTY3ZDg2M2E2YThlODBjMGE5MyIsImV4cCI6MTU5ODU1MzcyMSwiZ3JhbnRzIjp7ImlkZW50aXR5IjoiTWFoYWRldjEzIiwidmlkZW8iOnsicm9vbSI6InJvb20xNyJ9fX0.bAcbiSBCF__1Ux6fFEP-OzBjm8FLmP4pAefJFOSc7RY"
     private var SESSION_ID = "room17"*/



    private lateinit var accessToken: String
    private lateinit var roomName: String
    private lateinit var mBinding: ActivityVideoSessionBinding


    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private val cameraCapturerCompat by lazy {
        CameraCapturerCompat(
            this,
            getAvailableCameraSource()
        )
    }
    private val audioSwitch by lazy {
        AudioSwitch(applicationContext)
    }
    private var savedVolumeControlStream by Delegates.notNull<Int>()
    private lateinit var audioDeviceMenuItem: MenuItem

    private var participantIdentity: String? = null
    private lateinit var localVideoView: VideoRenderer
    private var disconnectedFromOnDestroy = false
    private var isSpeakerPhoneEnabled = true

    /*
    * A Room represents communication between a local participant and one or more participants.
    */
    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null

    /*
     * AudioCodec and VideoCodec represent the preferred codec for encoding and decoding audio and
     * video.
     */
    private val audioCodec: AudioCodec = OpusCodec()
    private val videoCodec: VideoCodec = Vp8Codec()
    private val encodingParameters: EncodingParameters = EncodingParameters(0, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_video_session
        ) as ActivityVideoSessionBinding
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
      // BindingAdapters.statusBarHeight(motionLayout.motionLayout)
        /*  motionLayout.setTransition(R.id.tran)
          motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
              override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

              }

              override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

              }

              override fun onTransitionCompleted(motionlayout: MotionLayout?, currentId: Int) {

              }

              override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

              }
          })

          */
        motionLayout.transitionToEnd()
        setup()
    }


    override fun setup() {

          statusBarHeight()
          KeyboardEventListener(this) {
            Log.v("Keyboard checker", "Keyboard is open = $it")
            if (it) {
                mViewModel.thumbnailVideoViewHeight.value = 0.4f
            } else {
                if(mViewModel.getIsBottomSectionExpanded().value!!)
                    mViewModel.thumbnailVideoViewHeight.value = 0.2f
                else
                    mViewModel.thumbnailVideoViewHeight.value = 0.3f
            }


        }
        primaryVideoView.addOnSizeListener(heightChange = {
            // mViewModel.videoViewHeight.value = it
            //val percent = (it * 0.02)
        })

        clickHandleObserver()
        audioSwitch.start { audioDevices, audioDevice ->
            audioDevice?.let {
                mViewModel.setIsSpeakerIcon(audioDevice is AudioDevice.Speakerphone)
            }
        }
        localAudioTrack = LocalAudioTrack.create(this, true)!!
        savedVolumeControlStream = volumeControlStream
        volumeControlStream = AudioManager.STREAM_VOICE_CALL
        mixPanelScreenVisitedEvent(MixPanelData.VIDEO_SESSION_VIEW_SHOWN_EVENT)

       /* if(BuildConfig.DEBUG) {
            val content = this.readFileFromAssets("VideoSessionTokenResponse.json")
            val jsonData: VideoSessionTokenResponse =
                Gson().fromJson(content, VideoSessionTokenResponse::class.java)
            val bundle = Bundle()
            bundle.putParcelable(BundleConstants.VIDEO_SESSION_DATA, jsonData)
            intent.putExtras(bundle)
        }*/
        this.intent?.getParcelableExtra<VideoSessionTokenResponse>(BundleConstants.VIDEO_SESSION_DATA)
            .let {
                if (it != null) {
                    accessToken = it.token!!
                    roomName =
                        "${it.consultation?.id}-${BuildConfig.ENVIROMENT}-$AppConstants.ROOM_SMALL_GROUP"
                    it.consultation?.let { it1 -> mViewModel.setConsultationData(it1) }
                    if (!accessToken.isNullOrEmpty()) {
                        activityScope.launch {
                            delay(1000)
                            connectToRoom()
                        }
                    }
                        view_pager.offscreenPageLimit=3
                        view_pager.adapter = PagerAdapter(supportFragmentManager, it)
                        tab_layout.setupWithViewPagerAndKeepIcons(view_pager)
                        tab_layout.addOnTabSelectedListener(onTabSelectedListener)

                    buildClient(accessToken, it.consultation?.id.toString())
                }
            }
    }
    private fun buildClient(token: String, channelName: String) {
        chatClientManager.buildClient(token, object : TaskCompletionListener<ChatClient?, String?> {
            override fun onSuccess(chatClient: ChatClient?) {
                Timber.d("chatClient created")
                chatChannelManager.joinOrCreateGeneralChannelWithCompletion(
                    channelName,
                    object : StatusListener() {
                        override fun onSuccess() {
                            Timber.d(" chatChannel created")
                            showDebugToast("chatChannel created")
                            chatChannelManager.channelLoadFinished.value = true
                            mixPanelActionEvent(MixPanelData.CHAT_CONNECTED)
                        }

                        override fun onError(errorInfo: ErrorInfo) {
                            Timber.d("error in chatClient creation => ${errorInfo.message}")
                            showDebugToast(errorInfo.message)
                            chatChannelManager.channelLoadFinished.value = true
                            mixPanelActionEvent(MixPanelData.CHAT_CONNECTION_FAILURE)
                        }
                    })
            }

            override fun onError(message: String?) {
                chatChannelManager.channelLoadFinished.value = true
                Timber.d("error in chatChannel creation => $message")
                showDebugToast("error in chatChannel creation => $message")
            }
        })
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
            if (mViewModel.getIsBottomSectionExpanded().value!!)
                mViewModel.onBtnBottomSectionExpandClickListener()
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (mViewModel.getIsBottomSectionExpanded().value!!)
                mViewModel.onBtnBottomSectionExpandClickListener()

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            if (tab?.position == 1) {
                hideKeyboard()
            }
        }
    }


    private fun showAudioDevices(isEnable: Boolean) {

        if (isEnable)
            audioSwitch.selectDevice(audioSwitch.availableAudioDevices.lastOrNull())
        else
            audioSwitch.selectDevice(audioSwitch.availableAudioDevices.firstOrNull())
    }

    private fun clickHandleObserver() {
        mViewModel.getIsBottomSectionExpanded().observe(this, Observer {
            if (it) {
                    motionLayout.transitionToStart()
                    mViewModel.thumbnailVideoViewHeight.value = 0.2f
                    mViewModel.circularIconPercent.value = 0.1f
                    hideKeyboard()
            } else {
                motionLayout.transitionToEnd()
                mViewModel.thumbnailVideoViewHeight.value = 0.3f
                mViewModel.circularIconPercent.value = 0.2f
            }

        })

        mViewModel.getIsLocalVideoOff().observe(this, Observer { onOffCheck ->
            localVideoTrack?.let {
                it.enable(onOffCheck)
                if (onOffCheck)
                    localParticipant?.publishTrack(it)
                else
                    localParticipant?.unpublishTrack(it)
            }
            val mixPanelInstance = MixPanelData.getInstance(this)
            val props = JSONObject()
            props.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
            props.put(MixPanelData.KEY_SCREEN_NAME, mViewModel.getConsultationDetail().value?.id)
            props.put(MixPanelData.KEY_ROOM_NAME, roomName)
            props.put(MixPanelData.KEY_PARTICIPANT_ID, participantIdentity)
            props.put(MixPanelData.KEY_CAMERA_STATUS, cameraCapturerCompat.cameraSource)
            mixPanelInstance.addEvent(props, MixPanelData.CAMERA_BUTTON_CLICKED_EVENT)
        })
        mViewModel.getIsLocalAudioMute().observe(this, Observer { onOffCheck ->
            localAudioTrack?.enable(onOffCheck)
            val mixPanelInstance = MixPanelData.getInstance(this)
            val props = JSONObject()
            props.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
            props.put(MixPanelData.KEY_SCREEN_NAME, mViewModel.getConsultationDetail().value?.id)
            props.put(MixPanelData.KEY_ROOM_NAME, roomName)
            props.put(MixPanelData.KEY_PARTICIPANT_ID, participantIdentity)
            props.put(MixPanelData.KEY_CAMERA_STATUS, localAudioTrack?.isEnabled)
            mixPanelInstance.addEvent(props, MixPanelData.MIC_BUTTON_CLICKED_EVENT)
        })
        mViewModel.onExitEventObservable.observe(this, Observer {
            showAlert()
        })
        mViewModel.getIsSpeakerEnable().observe(this, Observer {
            showAudioDevices(it)
        })
        mViewModel.getIsFrontCameraEnabled().observe(this, Observer {
            switchCameraClickListener(it)
        })
        mBinding.includeVideoView.imgSetting.setOnClickListener {
            SwitchCameraDialogFragment(this).apply {
                show(supportFragmentManager, "SwitchCameraDialogFragment")
            }
        }

    }

    private fun switchCameraClickListener(value: Boolean) {
        val cameraSource = cameraCapturerCompat.cameraSource
        if (value && cameraSource != CameraCapturer.CameraSource.FRONT_CAMERA) {
            cameraCapturerCompat.switchCamera()
            thumbnailVideoView.mirror = true
        }
        if (!value && cameraSource != CameraCapturer.CameraSource.BACK_CAMERA) {
            cameraCapturerCompat.switchCamera()
            thumbnailVideoView.mirror = false
        }

        val mixPanelInstance = MixPanelData.getInstance(this)
        val props = JSONObject()
        props.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
        props.put(MixPanelData.KEY_SCREEN_NAME, mViewModel.getConsultationDetail().value?.id)
        props.put(MixPanelData.KEY_ROOM_NAME, roomName)
        props.put(MixPanelData.KEY_PARTICIPANT_ID, participantIdentity)
        props.put(MixPanelData.KEY_CAMERA_STATUS, cameraCapturerCompat.cameraSource)
        mixPanelInstance.addEvent(props, MixPanelData.CAMERA_BUTTON_CLICKED_EVENT)
    }

    private fun resizeTabBarUi(isChatSelected: Boolean) {
       // val layout = findViewById<MotionLayout>(R.id.motionLayout)
        val viewContentLayout = findViewById<LinearLayout>(R.id.content_layout)
        var constraintLayout: ConstraintLayout? = null

        val parent: ViewParent? = viewContentLayout.parent
        if (parent == null) {
           Timber.d("this.getParent() is null")
        } else {
            if (parent is ConstraintLayout) {
                constraintLayout = parent
            } else {
                Timber.d("this.getParent() is not a ViewGroup")
            }
        }
        val set = ConstraintSet()
        if (isChatSelected)
            set.constrainPercentHeight(R.id.content_layout, 0.55F)
        else
            set.constrainPercentHeight(R.id.content_layout, 0.4F)

        (constraintLayout?.layoutParams as ConstraintLayout.LayoutParams)
            .matchConstraintPercentHeight = 0.55F
        constraintLayout.requestLayout()
        //set.applyTo(constraintLayout);
    }

    private fun connectToRoom() {
        audioSwitch.activate()
        val connectOptionsBuilder = ConnectOptions.Builder(accessToken)
            .roomName(roomName)
        localAudioTrack?.let { connectOptionsBuilder.audioTracks(listOf(it)) }
        localVideoTrack?.let { connectOptionsBuilder.videoTracks(listOf(it)) }
        connectOptionsBuilder.preferAudioCodecs(listOf(audioCodec))
        connectOptionsBuilder.preferVideoCodecs(listOf(videoCodec))
        connectOptionsBuilder.encodingParameters(encodingParameters)
        connectOptionsBuilder.enableAutomaticSubscription(true)
        room = Video.connect(this, connectOptionsBuilder.build(), roomListener)

    }

    private fun createAudioAndVideoTracks() {

        localVideoTrack = if (localVideoTrack == null) {
            LocalVideoTrack.create(
                this,
                true,
                cameraCapturerCompat.videoCapturer
            )
        } else {
            localVideoTrack
        }

        localVideoView = thumbnailVideoView
        thumbnailVideoView.mirror = cameraCapturerCompat.cameraSource ==
                CameraCapturer.CameraSource.FRONT_CAMERA
        localVideoTrack?.addRenderer(localVideoView)
        localVideoTrack?.let { localParticipant?.publishTrack(it) }
        mViewModel.getIsLocalVideoOff().value?.let { onOffCheck ->
            localVideoTrack?.let {
                it.enable(onOffCheck)
                if (onOffCheck)
                    localParticipant?.publishTrack(it)
                else
                    localParticipant?.unpublishTrack(it)
            }
        }
        localParticipant?.setEncodingParameters(encodingParameters)
        room?.let {
            reconnectingProgressBar.visibility = if (it.state != Room.State.RECONNECTING)
                View.GONE else
                View.VISIBLE
        }
    }

    private val roomListener = object : Room.Listener {
        override fun onConnected(room: Room) {
            localParticipant = room.localParticipant
            showDebugToast("onConnected to room=>${room.name}")
            mixPanelActionEvent(MixPanelData.ROOM_CONNECTED)
            room.remoteParticipants.firstOrNull()?.let { addRemoteParticipant(it) }
        }

        override fun onReconnected(room: Room) {
            reconnectingProgressBar.visibility = View.GONE
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            reconnectingProgressBar.visibility = View.VISIBLE
        }

        override fun onConnectFailure(room: Room, e: TwilioException) {
            mixPanelActionEvent(MixPanelData.ROOM_CONNECTION_FAILURE)

        }

        override fun onDisconnected(room: Room, e: TwilioException?) {
            showDebugToast("onDisconnected")
            localParticipant = null
            reconnectingProgressBar.visibility = View.GONE
            this@VideoSessionActivity.room = null
            // Only reinitialize the UI if disconnect was not called from onDestroy()
            if (!disconnectedFromOnDestroy) {
                audioSwitch.deactivate()
            }
        }

        override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
            showDebugToast("onParticipantConnected")
            addRemoteParticipant(participant)
        }

        override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {
            removeRemoteParticipant(participant)
        }

        override fun onRecordingStarted(room: Room) {
            //onRecordingStarted
        }

        override fun onRecordingStopped(room: Room) {
            //onRecordingStopped
        }
    }

    override fun addRemoteParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.addRenderer(primaryVideoView)
        mViewModel.setIsParticipantVideoOn(true)
    }

    override fun removeParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeRenderer(primaryVideoView)
        mViewModel.setIsParticipantVideoOn(false)
    }

    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {

        participantIdentity = remoteParticipant.identity

        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { addRemoteParticipantVideo(it) }
            }
        }
        mViewModel.setIsParticipantConnected(remoteParticipant)
        remoteParticipant.setListener(RemoteParticipantWrapper(this))

    }

    private fun removeRemoteParticipant(remoteParticipant: RemoteParticipant) {
        if (remoteParticipant.identity != participantIdentity) {
            return
        }
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { removeParticipantVideo(it) }
            }
        }
        mViewModel.setIsParticipantConnected(remoteParticipant)
    }

    override fun onResume() {
        super.onResume()
        createAudioAndVideoTracks()
    }

    override fun onPause() {
        super.onPause()
        localVideoTrack?.let { localParticipant?.unpublishTrack(it) }
        localVideoTrack?.release()
        localVideoTrack = null

    }

    override fun onBackPressed() {
        showAlert()
    }

    override fun onDestroy() {

        justTry {
            audioSwitch.stop()
            volumeControlStream = savedVolumeControlStream
            room?.disconnect()
            disconnectedFromOnDestroy = true
            localAudioTrack?.release()
            localVideoTrack?.release()
            chatClientManager.shutdown()
            chatChannelManager.shutdown()
            scope.close()
        }
        super.onDestroy()
    }

    private fun showAlert() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.exit_consultation))
            .setMessage(getString(R.string.exit_consultation_message))

            .setNegativeButton(getString(R.string.cancel_text))
            { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.exit_text))
            { _, _ ->
                finish()
            }
            .show()
    }


    private fun getAvailableCameraSource(): CameraCapturer.CameraSource {
        return if (CameraCapturer.isSourceAvailable(CameraCapturer.CameraSource.FRONT_CAMERA))
            CameraCapturer.CameraSource.FRONT_CAMERA
        else
            CameraCapturer.CameraSource.BACK_CAMERA
    }

    override fun onSwitchCamera() {
        mViewModel.setIsFrontCameraEnabled()
    }

    override fun onHelp() {
        HelpDialogFragment().apply {
            show(supportFragmentManager, "HelpDialogFragment")
        }
    }

        fun statusBarHeight() {
            // status bar height

            // status bar height
            var statusBarHeight = 0
            val resourceId: Int =
                motionLayout.context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
               statusBarHeight = motionLayout.context.resources.getDimensionPixelSize(resourceId)
                val params: ViewGroup.MarginLayoutParams = motionLayout!!.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin  = statusBarHeight * -1
            }
        }
}