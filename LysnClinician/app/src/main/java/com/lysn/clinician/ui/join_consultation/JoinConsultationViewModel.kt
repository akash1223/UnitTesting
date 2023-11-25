package com.lysn.clinician.ui.join_consultation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lysn.clinician.R
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.model.VideoSessionTokenResponse
import com.lysn.clinician.repository.ConsultationRepository
import com.lysn.clinician.utility.extensions.minutesDiff
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.SingleLiveEvent
import com.lysn.clinician.utils.Util
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class JoinConsultationViewModel(
    private val consultationRepository: ConsultationRepository,
    private val localizeTextProvider: LocalizeTextProvider,
    private val context: Context
) :
    ViewModel() {

    var mConsultationDetails = MutableLiveData<ConsultationDetails>()
    private var timer = Timer()
    val onJoinConsultationObservable = SingleLiveEvent<Boolean>()
    private var _mJoinConsultationResponse = MutableLiveData<Resource<VideoSessionTokenResponse>>()
    private val mJoinConsultationResponse: LiveData<Resource<VideoSessionTokenResponse>>
        get() = _mJoinConsultationResponse


    fun onJoinConsultationClickListener() {
        onJoinConsultationObservable.value = true
    }

    fun addConsultationData(consultationDetails: ConsultationDetails) {
        mConsultationDetails.value = consultationDetails
    }

    fun getConsultationDetail(): LiveData<ConsultationDetails> {
        return mConsultationDetails
    }

    fun getDate(): String? {
        return mConsultationDetails.value?.let {
            Util.getFormattedTime(
                it.dateTime,
                it.durationMinutes
            )
        }
    }

    fun getCallType(): String {
        return getCallTypeMessage()
    }

    private fun getCallTypeMessage(): String {
        return when {
            mConsultationDetails.value?.type.equals(context.getString(R.string.phone_text)) ->
                context.getString(R.string.minutes_phone_consultation)
            mConsultationDetails.value?.type.equals(context.getString(R.string.f2f_text)) ->
                context.getString(R.string.minutes_face_to_consultation)
            mConsultationDetails.value?.type.equals(context.getString(R.string.video_text)) ->
                context.getString(R.string.minutes_video_consultation)
            else ->
                AppConstants.EMPTY_VALUE
        }
    }

    fun joinConsultation(): LiveData<Resource<VideoSessionTokenResponse>> {
        _mJoinConsultationResponse.value = (Resource.loading(null))

        viewModelScope.launch {
            _mJoinConsultationResponse.postValue(
                consultationRepository.executeJoinConsultation(
                    mConsultationDetails.value?.id.toString()
                )
            )
        }
        return mJoinConsultationResponse
    }


    fun timerChange() {

        mConsultationDetails.value?.let { value ->
            if (!value.dateTime.isNullOrEmpty()) {

                var minutes = LocalDateTime.now().minutesDiff(value.dateTime)
                if (minutes > 0) {
                    value.timerDisplayName =
                        localizeTextProvider.getConsultationMinutesMessage(
                            minutes.toInt()
                        )
                    value.timerStatus = "session_started"
                } else {
                    minutes += value.durationMinutes
                    if (minutes > 0 && minutes < value.durationMinutes) {
                        value.timerDisplayName =
                            localizeTextProvider.getConsultationStartedMessage(
                                minutes.toInt()
                            )
                        value.timerStatus = "started"
                    } else {
                        value.timerDisplayName = AppConstants.EMPTY_VALUE
//                                        localizeTextProvider.getConsultationFinishedMessage(
//                                        )
                        //finished
                        value.timerStatus = AppConstants.EMPTY_VALUE
                    }
                }
            }
            mConsultationDetails.postValue(value)

        }
    }


}