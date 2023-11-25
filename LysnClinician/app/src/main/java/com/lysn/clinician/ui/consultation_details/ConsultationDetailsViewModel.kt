package com.lysn.clinician.ui.consultation_details

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lysn.clinician.R
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.CancelConsultationDetails
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.repository.ConsultationRepository
import com.lysn.clinician.utility.extensions.minutesDiff
import com.lysn.clinician.utils.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ConsultationDetailsViewModel(
    private val consultationRepository: ConsultationRepository,
    private val context: Context,
    private val localizeTextProvider: LocalizeTextProvider
) : ViewModel() {

    private var timer = Timer()
    var mConsultationDetails = MutableLiveData<ConsultationDetails>()
    private var _mCancelConsultationResponseDetails =
        MutableLiveData<Resource<CancelConsultationDetails>>()
    private val mCancelConsultationResponseDetails: LiveData<Resource<CancelConsultationDetails>>
        get() = _mCancelConsultationResponseDetails
    val onCancelConsultationObservable = SingleLiveEvent<Boolean>()

    fun addConsultationData(consultationDetails: ConsultationDetails) {
        mConsultationDetails.value = consultationDetails
    }

    fun onCancelConsultationClickListener() {
        onCancelConsultationObservable.value = true
    }

    fun getConsultationDetail(): LiveData<ConsultationDetails> {
        return mConsultationDetails
    }

    fun getDate(): String? {
        return mConsultationDetails.value?.let {
            Util.convertDateFormat(
                it.dateTime,
                it.durationMinutes
            )
        }
    }

    fun cancelConsultation(): LiveData<Resource<CancelConsultationDetails>> {
        _mCancelConsultationResponseDetails.value = (Resource.loading(null))
        viewModelScope.launch {
            _mCancelConsultationResponseDetails.postValue(
                consultationRepository.executeCancelConsultation(
                    mConsultationDetails.value?.id.toString()
                )
            )
        }
        return mCancelConsultationResponseDetails
    }

    fun getCallType(): String {
        return getCallTypeMessage()
    }

    fun getCallTypeMessage(): String {
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

    fun timerChange() {
        mConsultationDetails.value?.let { value ->
            if (!value.dateTime.isNullOrEmpty()) {

                var minutes = LocalDateTime.now().minutesDiff(value.dateTime)
                if (minutes > 0) {
                    value.timerDisplayName =
                        localizeTextProvider.getConsultationMinutesMessage(
                            minutes.toInt()
                        )
                    value.timerStatus = "start_session"
                } else {
                    value.timerDisplayName = value.statusForClientDisplay
                    value.timerStatus = value.status
                }

            }
            mConsultationDetails.postValue(value)
        }
    }
}