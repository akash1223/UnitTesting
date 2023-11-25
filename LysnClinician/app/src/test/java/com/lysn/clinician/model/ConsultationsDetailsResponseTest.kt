package com.lysn.clinician.model

import com.google.gson.Gson
import com.lysn.clinician.utils.MockResponseFileReader
import org.junit.Assert.*
import org.junit.*
import org.junit.Before
import org.junit.Test

class ConsultationsDetailsResponseTest
{

    private lateinit var consultationsDetailsResponse: ConsultationsDetailsResponse
    private lateinit var listOfConsultation: List<ConsultationDetails>
    @Before
    fun setup() {
        val mockJson = MockResponseFileReader("ConsultationsDetailsResponse.json").content
        consultationsDetailsResponse= Gson().fromJson(mockJson, ConsultationsDetailsResponse::class.java)
        listOfConsultation=consultationsDetailsResponse.byId?.map { x -> x.value }
    }

    @Test
    fun setNullValueToModel()
    {
        val consultationsDetailsResponse=ConsultationsDetailsResponse()
        assertNotNull(consultationsDetailsResponse.byId)
    }

    @Test
    fun checkVideoSessionTokenResponseModel()
    {
        assertNotNull(consultationsDetailsResponse.byId)
        assertNotNull(consultationsDetailsResponse.pastIds)
    }
    @Test
    fun checkConsultationDetailsModel()
    {
        val consultationDetails:ConsultationDetails?=listOfConsultation[0]
        assertNotNull(consultationDetails?.id)
        assertNotNull(consultationDetails?.client)
        assertNotNull(consultationDetails?.therapist)
        assertNotNull(consultationDetails?.dateTime)
    }
    @Test
    fun checkClientModel()
    {
        val client:ConsultationDetails.Client?=listOfConsultation[0]?.client
        assertNotNull(client?.id)
        assertNotNull(client?.photo)
    }
    @Test
    fun checkTherapistModel()
    {
        val therapist:ConsultationDetails.Therapist?=listOfConsultation[0]?.therapist
        assertNotNull(therapist?.id)
        assertNotNull(therapist?.firstName)
    }
}