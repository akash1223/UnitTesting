package com.lysn.clinician.ui.join_consultation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lysn.clinician.R
import com.lysn.clinician.databinding.ActivityJoinConsultationBinding
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.ui.base.BaseActivity
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.ui.video_session.VideoSessionActivity
import com.lysn.clinician.utility.ConsultationStatusCode
import com.lysn.clinician.utility.extensions.observeNetworkCall
import com.lysn.clinician.utils.*
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.video.CameraCapturer
import com.twilio.video.CameraCapturer.CameraSource
import com.twilio.video.LocalAudioTrack
import com.twilio.video.LocalVideoTrack
import kotlinx.android.synthetic.main.activity_join_consultation.*
import kotlinx.android.synthetic.main.layout_video_view.*
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * This class shows preview video call before join consultation
 */
class JoinConsultationActivity : BaseActivity(), SwitchCameraInterface {

    private val mViewModel: JoinConsultationViewModel by viewModel()
    private lateinit var mBinding: ActivityJoinConsultationBinding
    private lateinit var mConsultationDetails: ConsultationDetails
    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private val mPreferenceUtil: PreferenceUtil by inject()
    private var localVideoTrack: LocalVideoTrack? = null
    private var localAudioTrack: LocalAudioTrack? = null
    private var isVideoFullScreen = false
    private val audioSwitch by lazy {
        AudioSwitch(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_join_consultation
        ) as ActivityJoinConsultationBinding

        try {
            this.intent?.getParcelableExtra<ConsultationDetails>(BundleConstants.KEY_CONSULTATION_DATA)
                .let {
                    if (it != null) {
                        mConsultationDetails = it
                    }
                }
        } catch (e: Exception) {
            showToast(
                LocalizeTextProvider(this).getSomethingWrongMessage(),
                true
            )
            finish()
        }

        setup()
        setObserver()

    }

    var prevAudioDevice : AudioDevice? = null

    override fun setup() {
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel

        mBinding.statusColor =
            ConsultationStatusCode(this)
        setDataToViewModel(mConsultationDetails)




        if (!checkPermission(permissions)) {
            requestPermissions(permissions, AppConstants.REQUEST_PERMISSION)
        } else {
            setVideoView()
            setViews()
            setProfile()
            setAudioSwitch()
        }

        img_view_video.setOnClickListener {
            mPreferenceUtil.putValue(
                PreferenceUtil.IS_VIDEO_CALL_ENABLED,
                !mPreferenceUtil.isVideoCallEnabled()
            )
            setViews()
        }

        img_view_audio.setOnClickListener {
            mPreferenceUtil.putValue(
                PreferenceUtil.IS_AUDIO_CALL_ENABLED,
                !mPreferenceUtil.isAudioCallEnabled()
            )
            setViews()
        }
        img_audio_device.setOnClickListener {

            if(mPreferenceUtil.isAudioDeviceSpeaker())
            {

                audioSwitch.selectDevice(audioSwitch.availableAudioDevices.firstOrNull())
            }
            else{
                audioSwitch.selectDevice(audioSwitch.availableAudioDevices.lastOrNull())

            }
        }

        img_expand.setOnClickListener {
            if (isVideoFullScreen) {
                user_detail_layout.visibility = View.VISIBLE
            } else {
                user_detail_layout.visibility = View.GONE
            }
            isVideoFullScreen = !isVideoFullScreen

        }
        mBinding.txtEndConsultation.setOnClickListener {
            endConsultation()
            mixPanelButtonClickEvent(
                mBinding.txtEndConsultation,
                MixPanelData.END_CONSULTATION_BUTTON_CLICKED_EVENT,
                this
            )

        }

        img_cross.setOnClickListener {
            endConsultation()
            val properties = JSONObject()
            properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
            MixPanelData.getInstance(this)
                .addEvent(
                    properties,
                    MixPanelData.EXIT_CONSULTATION_BUTTON_CLICKED_EVENT
                )
        }


        img_setting.setOnClickListener {
            SwitchCameraDialogFragment(this).apply {
                show(supportFragmentManager, "SwitchCameraDialogFragment")
            }
        }

        mixPanelScreenVisitedEvent(MixPanelData.JOIN_CONSULTATION_VIEW_SHOWN_EVENT)

    }


    private fun setObserver() {
        mViewModel.onJoinConsultationObservable.observe(this, Observer {
            mViewModel.joinConsultation().observeNetworkCall(this, Observer { response ->
                when (response.status) {

                    Resource.Status.LOADING ->
                        showLoading()

                    Resource.Status.SUCCESS -> {
                        hideLoading()
                        val intent = Intent(this, VideoSessionActivity::class.java)
                        val bundle = Bundle()
                        bundle.putParcelable(BundleConstants.VIDEO_SESSION_DATA, response.data)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }

                    Resource.Status.NO_INTERNET -> {
                        hideLoading()
                        showNoInternetMaterialDialog()
                    }

                    Resource.Status.ERROR -> {
                        hideLoading()
                        showSnackBar(root_layout,LocalizeTextProvider(this).getUnableToJoinMessage() , true)
                    }

                    else -> {
                        hideLoading()
                        showSnackBar(root_layout, LocalizeTextProvider(this).getUnableToJoinMessage(), true)

                    }
                }
            })


            mixPanelButtonClickEvent(
                mBinding.btnJoinConsultation,
                MixPanelData.JOIN_CONSULTATION_BUTTON_CLICKED_EVENT
            )

        })
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED) allSuccess =
                false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != AppConstants.REQUEST_PERMISSION) return
        var allSuccess = true
        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                allSuccess = false

                // handled don't ask again permission
                val askAgain = shouldShowRequestPermissionRationale(permissions[i])
                if (askAgain) {
                    finish()
                } else {
                    goToSettings()
                }

            }
        }
        if (allSuccess) {
            setVideoView()
            setViews()
            setProfile()
            setAudioSwitch()
        }
    }


    private fun setProfile() {
        mConsultationDetails.therapist?.photo100x100?.let {
            Glide.with(this).load(it).into(img_view_user)
        }
    }
    private fun setAudioSwitch() {
        audioSwitch.start { audioDevices, selectedAudioDevice ->
            setAudioDevice(audioSwitch.selectedAudioDevice is AudioDevice.Speakerphone)
        }
        audioSwitch.activate()
        audioSwitch.selectDevice(audioSwitch.availableAudioDevices.lastOrNull())
    }
    private fun setVideoView() {
        localVideoTrack = LocalVideoTrack.create(
            this, true, CameraCapturer(
                this,
                CameraSource.FRONT_CAMERA, null
            )
        )
        localAudioTrack = LocalAudioTrack.create(this, true)!!
        localVideoTrack?.addRenderer(video_view)
    }

    private fun setViews() {
        localVideoTrack?.let {
            if (mPreferenceUtil.isVideoCallEnabled()) {
                img_view_video.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_video_call
                    )
                )
                img_view_user.visibility = View.GONE
                localVideoTrack?.addRenderer(video_view)

                val properties = JSONObject()
                properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
                properties.put(MixPanelData.KEY_CAMERA_STATUS, MixPanelData.ACTIVATED )

                MixPanelData.getInstance(this)
                    .addEvent(
                        properties,
                        MixPanelData.CAMERA_BUTTON_CLICKED_EVENT
                    )

            } else {
                img_view_video.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_video_call_disable
                    )
                )
                img_view_user.visibility = View.VISIBLE

                val properties = JSONObject()
                properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
                properties.put(MixPanelData.KEY_CAMERA_STATUS, MixPanelData.DEACTIVATED )

                MixPanelData.getInstance(this)
                    .addEvent(
                        properties,
                        MixPanelData.CAMERA_BUTTON_CLICKED_EVENT
                    )

            }
            mPreferenceUtil.putValue(
                PreferenceUtil.IS_VIDEO_CALL_ENABLED,
                mPreferenceUtil.isVideoCallEnabled()
            )
            localVideoTrack?.enable(mPreferenceUtil.isVideoCallEnabled())
        }

        localAudioTrack?.let {
            if (mPreferenceUtil.isAudioCallEnabled()) {
                img_view_audio.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_audio_call
                    )
                )

                val properties = JSONObject()
                properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
                properties.put(MixPanelData.KEY_MIC_STATUS, MixPanelData.ACTIVATED )

                MixPanelData.getInstance(this)
                    .addEvent(
                        properties,
                        MixPanelData.MIC_BUTTON_CLICKED_EVENT
                    )

            } else {
                img_view_audio.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_audio_call_disable
                    )
                )

                val properties = JSONObject()
                properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
                properties.put(MixPanelData.KEY_MIC_STATUS, MixPanelData.DEACTIVATED )

                MixPanelData.getInstance(this)
                    .addEvent(
                        properties,
                        MixPanelData.MIC_BUTTON_CLICKED_EVENT
                    )

            }
            mPreferenceUtil.putValue(
                PreferenceUtil.IS_AUDIO_CALL_ENABLED,
                mPreferenceUtil.isAudioCallEnabled()
            )
            localAudioTrack?.enable(mPreferenceUtil.isAudioCallEnabled())
        }
    }

    private fun setAudioDevice(value:Boolean ) {
        if (value) {
            img_audio_device.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_speaker
                )
            )
            val properties = JSONObject()
            properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
            properties.put(MixPanelData.KEY_VOLUME_STATUS, MixPanelData.SPEAKER )

            MixPanelData.getInstance(this)
                .addEvent(
                    properties,
                    MixPanelData.VOLUME_BUTTON_CLICKED_EVENT
                )
        } else {
            img_audio_device.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_ear_piece
                )
            )
            val properties = JSONObject()
            properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
            properties.put(MixPanelData.KEY_VOLUME_STATUS, MixPanelData.EAR_PIECE )

            MixPanelData.getInstance(this)
                .addEvent(
                    properties,
                    MixPanelData.VOLUME_BUTTON_CLICKED_EVENT
                )

        }
        mPreferenceUtil.putValue(
            PreferenceUtil.IS_AUDIO_DEVICE_SPEAKER,
            value
        )
    }

    private fun setDataToViewModel(consultationDetails: ConsultationDetails?) {

        consultationDetails?.let {
            consultationDetails.timerDisplayName = consultationDetails.statusForClientDisplay
            consultationDetails.timerStatus = consultationDetails.status
            mViewModel.addConsultationData(consultationDetails)
            if (consultationDetails.canJoin) {
                setTimeChangeBroadcast(object : BaseFragment.InterfaceTimeChange {
                    override fun timeChanged() {
                        mViewModel.timerChange()
                    }
                })
            }
        }

    }

    private fun goToSettings() {

        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setMessage(getString(R.string.permission_message))
            .setTitle(getString(R.string.permission_required))
            .setPositiveButton(getString(R.string.settings_text))
            { _, _ ->

                val myAppSettings = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivityForResult(myAppSettings, AppConstants.REQUEST_APP_SETTINGS)
                finish()
            }
            .setNegativeButton(getString(R.string.cancel_text))
            { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onPause() {
        localVideoTrack?.release()
        localVideoTrack = null
        super.onPause()

    }

    override fun onRestart() {
        super.onRestart()
       // setVideoView()
        setCameraSource()
        setViews()

        if(mPreferenceUtil.isAudioDeviceSpeaker())
        {
            audioSwitch.selectDevice(audioSwitch.availableAudioDevices.lastOrNull())
        }
        else{
            audioSwitch.selectDevice(audioSwitch.availableAudioDevices.firstOrNull())
        }
       // setProfile()
        mBinding.btnJoinConsultation.text = getString(R.string.rejoin_consultation)
        mBinding.txtEndConsultation.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        endConsultation()
    }

    private fun endConsultation() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioSwitch.stop()
    }


    override fun onSwitchCamera() {
        mPreferenceUtil.putValue(
            PreferenceUtil.IS_FRONT_CAMERA_ENABLED,
            !mPreferenceUtil.isFrontCameraEnabled()
        )
        setCameraSource()
    }

    private fun setCameraSource() {
        localVideoTrack?.release()
        localVideoTrack = null
        if (mPreferenceUtil.isFrontCameraEnabled()) {

            localVideoTrack = LocalVideoTrack.create(
                this, true, CameraCapturer(
                    this,
                    CameraSource.FRONT_CAMERA, null
                )
            )
            video_view.mirror=true
            val properties = JSONObject()
            properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
            properties.put(MixPanelData.SWITCH_CAMERA_STATUS,MixPanelData.FRONT_CAMERA )

            MixPanelData.getInstance(this)
                .addEvent(
                    properties,
                    MixPanelData.SWITCH_CAMERA_BUTTON_CLICKED_EVENT
                )

        } else {
            localVideoTrack = LocalVideoTrack.create(
                this, true, CameraCapturer(
                    this,
                    CameraSource.BACK_CAMERA, null
                )
            )
            video_view.mirror=false
            val properties = JSONObject()
            properties.put(MixPanelData.KEY_SCREEN_NAME, this.javaClass.simpleName)
            properties.put(MixPanelData.SWITCH_CAMERA_STATUS,MixPanelData.BACK_CAMERA)

            MixPanelData.getInstance(this)
                .addEvent(
                    properties,
                    MixPanelData.SWITCH_CAMERA_BUTTON_CLICKED_EVENT
                )

        }
        if(mPreferenceUtil.isVideoCallEnabled()) {
            localVideoTrack?.addRenderer(video_view)
        }
    }

    override fun onHelp() {
        HelpDialogFragment().apply {
            show(supportFragmentManager, "HelpDialogFragment")
        }
    }



}