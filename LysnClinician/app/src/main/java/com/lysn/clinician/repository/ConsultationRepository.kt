package com.lysn.clinician.repository

import com.lysn.clinician.http.IHTTPService
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.CancelConsultationDetails
import com.lysn.clinician.model.ConsultationsDetailsResponse
import com.lysn.clinician.model.Empty
import com.lysn.clinician.model.VideoSessionTokenResponse
import com.lysn.clinician.utils.LocalizeTextProvider

class ConsultationRepository(private val httpIService: IHTTPService, localizeTextProvider: LocalizeTextProvider) :
    BaseRepository(localizeTextProvider) {

    // API call for cancel consultation
    suspend fun executeConsultationDetailsList(): Resource<ConsultationsDetailsResponse> {
        return getResult { httpIService.callGetConsultationDetails()}
    }
    // API call for cancel consultation
    suspend fun executeCancelConsultation(id: String) : Resource<CancelConsultationDetails> {
        val consultationDetails  = Empty()
        return getResult {  httpIService.callCancelConsultation(consultationDetails,id)}
    }

    // API call for cancel consultation
    suspend fun executeJoinConsultation(id: String) : Resource<VideoSessionTokenResponse> {
        return getResult {  httpIService.callJoinConsultation(id)}
    }
}