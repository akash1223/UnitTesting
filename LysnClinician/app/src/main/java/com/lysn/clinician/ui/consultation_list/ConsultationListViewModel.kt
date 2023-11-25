package com.lysn.clinician.ui.consultation_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.model.ConsultationsDetailsResponse
import com.lysn.clinician.repository.ConsultationRepository
import com.lysn.clinician.utility.extensions.minutesDiff
import com.lysn.clinician.utils.LocalizeTextProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ConsultationListViewModel(
    private val consultationRepository: ConsultationRepository,
    private val localizeTextProvider: LocalizeTextProvider
) : ViewModel() {

    private var listOfConsultationsDetailsResponseLiveData =
        MutableLiveData<Resource<ConsultationsDetailsResponse>>()

    private var listOfUpcomingConsultationsDetailsLiveData =
        MutableLiveData<List<ConsultationDetails>>()
    private var listOfReadyToJoin = MutableLiveData<List<ConsultationDetails>>()

    private var timer = Timer()

    init {
        fetchConsultations()
    }

    fun fetchConsultations() {
        listOfConsultationsDetailsResponseLiveData.value = (Resource.loading())
        viewModelScope.launch {
            listOfConsultationsDetailsResponseLiveData.postValue(consultationRepository.executeConsultationDetailsList())
        }
    }

    fun getConsultations(): LiveData<Resource<ConsultationsDetailsResponse>> {
        return listOfConsultationsDetailsResponseLiveData
    }

    fun getUpcomingConsultation(): LiveData<List<ConsultationDetails>> {
        return listOfUpcomingConsultationsDetailsLiveData
    }

    fun getReadyToJoin(): LiveData<List<ConsultationDetails>> {
        return listOfReadyToJoin
    }

    fun setUpcomingConsultation(consultationDetailsList: List<ConsultationDetails>) {
        listOfUpcomingConsultationsDetailsLiveData.postValue(consultationDetailsList)
    }

    fun setReadyToJoin(consultationDetailsList: List<ConsultationDetails>) {
        listOfReadyToJoin.value = consultationDetailsList
    }

    fun timerChange() {

        listOfReadyToJoin.value?.let {
            it.forEach { value ->
                if (!value.dateTime.isNullOrEmpty()) {

                    val minutes = LocalDateTime.now().minutesDiff(value.dateTime)
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
            }
            listOfReadyToJoin.postValue(it)
        }

    }


}