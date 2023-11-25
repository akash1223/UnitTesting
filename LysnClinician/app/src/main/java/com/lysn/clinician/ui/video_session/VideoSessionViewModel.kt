package com.lysn.clinician.ui.video_session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lysn.clinician.model.VideoSessionConsultationDetails
import com.lysn.clinician.ui.base.BaseViewModel
import com.lysn.clinician.utils.PreferenceUtil
import com.lysn.clinician.utils.SingleLiveEvent
import com.twilio.video.RemoteParticipant


class VideoSessionViewModel(val preferenceUtil: PreferenceUtil) : BaseViewModel() {

    private var mConsultationDetails = MutableLiveData<VideoSessionConsultationDetails>()

    private val isLocalVideoOff = MutableLiveData<Boolean>(true)
    private val isLocalAudioMute = MutableLiveData<Boolean>(true)
    private val isBottomSectionExpanded = MutableLiveData<Boolean>(false)
    private val isFrontCameraEnabled = MutableLiveData<Boolean>(false)

    private val isSpeakerEnable = SingleLiveEvent<Boolean>()
    private val isSpeakerIcon = MutableLiveData<Boolean>(false)

    val onExitEventObservable = SingleLiveEvent<Boolean>()
    val thumbnailVideoViewHeight = MutableLiveData<Float>()
    val circularIconPercent = MutableLiveData<Float>()
    private val isParticipantVideoOn = MutableLiveData<Boolean>()
    private val isParticipantConnected = MutableLiveData<RemoteParticipant>()


    init {
        setConsultationData(VideoSessionConsultationDetails())
        isLocalVideoOff.value = preferenceUtil.isVideoCallEnabled()
        isLocalAudioMute.value = preferenceUtil.isAudioCallEnabled()
        isFrontCameraEnabled.value = preferenceUtil.isFrontCameraEnabled()
        isSpeakerEnable.value = preferenceUtil.isAudioDeviceSpeaker()
    }

    //Setter
    fun setConsultationData(consultationDetails: VideoSessionConsultationDetails) {
        mConsultationDetails.value = consultationDetails
    }

    fun setIsParticipantVideoOn(value: Boolean) {
        isParticipantVideoOn.value = value
    }

    fun setIsParticipantConnected(value: RemoteParticipant) {
        isParticipantConnected.value = value
    }

    fun setIsSpeakerIcon(value: Boolean) {
        preferenceUtil.putValue(
            PreferenceUtil.IS_AUDIO_DEVICE_SPEAKER,
            value
        )
        isSpeakerIcon.value = value
    }

    fun setIsFrontCameraEnabled() {
        preferenceUtil.putValue(
            PreferenceUtil.IS_FRONT_CAMERA_ENABLED,
            !preferenceUtil.isFrontCameraEnabled()
        )
        isFrontCameraEnabled.value = !isFrontCameraEnabled.value!!
    }
    //Getter

    fun getConsultationDetail(): LiveData<VideoSessionConsultationDetails> {
        return mConsultationDetails
    }

    fun getIsParticipantVideoOn(): LiveData<Boolean> {
        return isParticipantVideoOn
    }

    fun getIsParticipantConnected(): LiveData<RemoteParticipant> {
        return isParticipantConnected
    }

    fun getIsSpeakerIcon(): LiveData<Boolean> {
        return isSpeakerIcon
    }

    fun getIsFrontCameraEnabled(): LiveData<Boolean> {
        return isFrontCameraEnabled
    }

    // Setter is not for below getter
    //value set after button click
    fun getIsLocalVideoOff(): LiveData<Boolean> {
        return isLocalVideoOff
    }

    fun getIsLocalAudioMute(): LiveData<Boolean> {
        return isLocalAudioMute
    }

    fun getIsBottomSectionExpanded(): LiveData<Boolean> {
        return isBottomSectionExpanded
    }

    fun getIsSpeakerEnable(): LiveData<Boolean> {
        return isSpeakerEnable
    }

    //Button CLick Listener
    fun onBtnLocalVideoOffClickListener() {
        preferenceUtil.putValue(
            PreferenceUtil.IS_VIDEO_CALL_ENABLED,
            !preferenceUtil.isVideoCallEnabled()
        )
        isLocalVideoOff.value = !isLocalVideoOff.value!!
    }

    fun onBtnLocalAudioMuteClickListener() {
        preferenceUtil.putValue(
            PreferenceUtil.IS_AUDIO_CALL_ENABLED,
            !preferenceUtil.isAudioCallEnabled()
        )
        isLocalAudioMute.value = !isLocalAudioMute.value!!
    }

    fun onBtnBottomSectionExpandClickListener() {
        isBottomSectionExpanded.value = !isBottomSectionExpanded.value!!
    }

    fun onBtnExitClickListener() {
        onExitEventObservable.value = true
    }

    fun onBtnSpeakerClickListener() {
        isSpeakerEnable.value = !isSpeakerIcon.value!!
    }

}
