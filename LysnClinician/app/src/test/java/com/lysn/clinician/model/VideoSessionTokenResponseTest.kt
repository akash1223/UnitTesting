package com.lysn.clinician.model

import com.google.gson.Gson
import com.lysn.clinician.utils.MockResponseFileReader
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VideoSessionTokenResponseTest {


    private lateinit var videoSessionTokenResponse:VideoSessionTokenResponse

    @Before
    fun setup() {
        val mockJson =MockResponseFileReader("VideoSessionTokenResponse.json").content
         videoSessionTokenResponse=Gson().fromJson(mockJson, VideoSessionTokenResponse::class.java)
    }

    @Test
    fun setNullValueToModel()
    {
        val videoSessionTokenResponse=VideoSessionTokenResponse(null,null,null,null)
        Assert.assertNull(videoSessionTokenResponse.token)

        val videoSessionConsultationDetails=VideoSessionConsultationDetails(null,null,0.0,false)
        Assert.assertNull(videoSessionConsultationDetails.adminUrl)

        val client=VideoSessionConsultationDetails.Client(null,null,0,false)
        Assert.assertNull(client.getFullName)

        val therapist= VideoSessionConsultationDetails.Therapist(true, true, true, 0,null)
        Assert.assertNull(therapist.description)
    }

    @Test
    fun checkVideoSessionTokenResponseModel()
    {
       Assert.assertNotNull(videoSessionTokenResponse.token)
       Assert.assertNotNull(videoSessionTokenResponse.sessionId)
       Assert.assertNotNull(videoSessionTokenResponse.consultation)
       Assert.assertNotNull(videoSessionTokenResponse.streamType)
    }
    @Test
    fun checkVideoSessionConsultationDetailsModel()
    {
        val videoSessionConsultationDetails:VideoSessionConsultationDetails?=videoSessionTokenResponse.consultation
        Assert.assertNotNull(videoSessionConsultationDetails?.id)
        Assert.assertNotNull(videoSessionConsultationDetails?.client)
        Assert.assertNotNull(videoSessionConsultationDetails?.therapist)
        Assert.assertNotNull(videoSessionConsultationDetails?.dateTime)
    }
    @Test
    fun checkClientModel()
    {
        val client:VideoSessionConsultationDetails.Client?=videoSessionTokenResponse.consultation?.client
        Assert.assertNotNull(client?.id)
        Assert.assertNotNull(client?.nextOfKinName)
        Assert.assertNotNull(client?.user)
        Assert.assertNotNull(client?.photo)
    }
    @Test
    fun checkTherapistModel()
    {
        val therapist:VideoSessionConsultationDetails.Therapist?=videoSessionTokenResponse.consultation?.therapist
        Assert.assertNotNull(therapist?.id)
        Assert.assertNotNull(therapist?.firstName)
    }
}
