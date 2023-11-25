package com.lysn.clinician.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lysn.clinician.model.VideoSessionConsultationDetails
import com.lysn.clinician.ui.video_session.VideoSessionViewModel
import com.lysn.clinician.utils.PreferenceUtil
import com.nhaarman.mockitokotlin2.mock
import com.twilio.video.RemoteParticipant
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock

class VideoSessionViewModelTest
{

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var preferenceUtil: PreferenceUtil

    @Mock
    lateinit var remoteParticipant: RemoteParticipant

    lateinit var mViewModel: VideoSessionViewModel

    @Before
    fun setUp()
    {
        preferenceUtil = mock()
        remoteParticipant = mock()
        mViewModel= VideoSessionViewModel(preferenceUtil)
    }

    @Test
    fun `verify_setConsultationData()_invoked`() {
        val consultationDetails = VideoSessionConsultationDetails(id = 1234)
        mViewModel.setConsultationData(consultationDetails)
        Assert.assertTrue(mViewModel.getConsultationDetail().value?.id == 1234)
    }

    @Test
    fun checkParticipantVideoOn() {
       mViewModel.setIsParticipantVideoOn(true)
       Assert.assertTrue(mViewModel.getIsParticipantVideoOn().value!!)
    }

    @Test
    fun checkParticipantConnected() {

       mViewModel.setIsParticipantConnected(remoteParticipant)
       Assert.assertTrue(mViewModel.getIsParticipantConnected().value == remoteParticipant)
    }

    @Test
    fun `verify_onBtnLocalVideoOffClickListener()_invoked()`() {
        mViewModel.onBtnLocalVideoOffClickListener()
        val prevValue = mViewModel.getIsLocalVideoOff()
        Assert.assertNotEquals(mViewModel.getIsParticipantVideoOn().value,prevValue )
    }

    @Test
    fun `verify_onBtnLocalAudioMuteClickListener()_invoked()`() {
        mViewModel.onBtnLocalAudioMuteClickListener()
        val prevValue = mViewModel.getIsLocalAudioMute()
        Assert.assertNotEquals(mViewModel.getIsLocalAudioMute().value,prevValue )
    }

    @Test
    fun `verify_onBtnBottomSectionExpandClickListener()_invoked()`() {
        mViewModel.onBtnBottomSectionExpandClickListener()
        val prevValue = mViewModel.getIsBottomSectionExpanded()
        Assert.assertNotEquals(mViewModel.getIsBottomSectionExpanded().value,prevValue )
    }

}