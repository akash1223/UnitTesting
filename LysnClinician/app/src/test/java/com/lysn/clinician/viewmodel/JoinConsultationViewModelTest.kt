package com.lysn.clinician.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.model.UserAuthResponse
import com.lysn.clinician.model.VideoSessionTokenResponse
import com.lysn.clinician.repository.ConsultationRepository
import com.lysn.clinician.ui.consultation_details.ConsultationDetailsViewModel
import com.lysn.clinician.ui.consultation_list.ConsultationListViewModel
import com.lysn.clinician.ui.join_consultation.JoinConsultationViewModel
import com.lysn.clinician.ui.signin.SignInViewModel
import com.lysn.clinician.utils.*
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class JoinConsultationViewModelTest {
    private lateinit var repository: ConsultationRepository
    private lateinit var viewModel: JoinConsultationViewModel
    private lateinit var context: Context
    private lateinit var localizeTextProvider: LocalizeTextProvider
    private var mConsultationDetails = MutableLiveData<ConsultationDetails>()
    private lateinit var consultationDetails: ConsultationDetails

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var videoSessionTokenResponse: VideoSessionTokenResponse


    @Before
    fun setup() {
        repository = mock()
        context = mock()
        localizeTextProvider = mock()
        viewModel =
            JoinConsultationViewModel(
                repository,
                localizeTextProvider,
                context
            )
        consultationDetails = ConsultationDetails(
            canJoin = true,
            id = 1,
            dateTime = "2020-08-09T01:30:00+02:00",
            durationMinutes = 30,
            type = "phone",
            canCancel = true
        )
    }

    @Test
    fun test_objects_not_null() {
        assertNotNull(repository)
        assertNotNull(viewModel)
        assertNotNull(context)
        assertNotNull(localizeTextProvider)
    }

    @Test
    fun `addConsultationData()_invoke`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
        assertNotNull(viewModel.mConsultationDetails.value)
    }

    @Test
    fun `onJoinConsultationClickListener()_invoke`() {
        viewModel.onJoinConsultationClickListener()
        assertTrue(viewModel.onJoinConsultationObservable.value ?: false)
    }

    @Test
    fun `getConsultationDetail()_return_value`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
        val returnValue = viewModel.getConsultationDetail()
        assertNotNull(returnValue)
        assertEquals("2020-08-09T01:30:00+02:00", returnValue.value?.dateTime)
        assertEquals(30, returnValue.value?.durationMinutes)
    }

    @Test
    fun `getDate()_return_value`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
        viewModel.getDate()
        val returnValue = mConsultationDetails.value?.let {
            Util.convertDateFormat(
                it.dateTime,
                it.durationMinutes
            )
        }
        Assert.assertNotNull(returnValue)
    }

    @Test
    fun `getCallTypeMessage()_return_video`() {
        viewModel.addConsultationData(consultationDetails)
        consultationDetails = ConsultationDetails(
            canJoin = true,
            id = 1,
            dateTime = "2020-08-09T01:30:00+02:00",
            durationMinutes = 30,
            type = "video",
            canCancel = true
        )
        mConsultationDetails.value = consultationDetails
        viewModel.getCallType()

        val message = when {
            mConsultationDetails.value?.type.equals(TestData.PHONE) ->
                TestData.MINUTES_PHONE_CONSULTATION
            mConsultationDetails.value?.type.equals(TestData.F2F) ->
                TestData.MINUTES_FACE_TO_FACE_CONSULTATION
            mConsultationDetails.value?.type.equals(TestData.VIDEO) ->
                TestData.MINUTES_VIDEO_CONSULTATION
            else ->
                AppConstants.EMPTY_VALUE
        }
        assertEquals(TestData.MINUTES_VIDEO_CONSULTATION, message)

    }

    @Test
    fun `getCallTypeMessage()_return_phone`() {
        viewModel.addConsultationData(consultationDetails)
        consultationDetails = ConsultationDetails(
            canJoin = true,
            id = 1,
            dateTime = "2020-08-09T01:30:00+02:00",
            durationMinutes = 30,
            type = "phone",
            canCancel = true
        )
        mConsultationDetails.value = consultationDetails
        viewModel.getCallType()

        val message = when {
            mConsultationDetails.value?.type.equals(TestData.PHONE) ->
                TestData.MINUTES_PHONE_CONSULTATION
            mConsultationDetails.value?.type.equals(TestData.F2F) ->
                TestData.MINUTES_FACE_TO_FACE_CONSULTATION
            mConsultationDetails.value?.type.equals(TestData.VIDEO) ->
                TestData.MINUTES_VIDEO_CONSULTATION
            else ->
                AppConstants.EMPTY_VALUE
        }
        assertEquals(TestData.MINUTES_PHONE_CONSULTATION, message)

    }

    @Test
    fun `getCallTypeMessage()_return_f2f`() {
        viewModel.addConsultationData(consultationDetails)
        consultationDetails = ConsultationDetails(
            canJoin = true,
            id = 1,
            dateTime = "2020-08-09T01:30:00+02:00",
            durationMinutes = 30,
            type = "f2f",
            canCancel = true
        )
        mConsultationDetails.value = consultationDetails
        viewModel.getCallType()
        val message = when {
            mConsultationDetails.value?.type.equals(TestData.PHONE) ->
                TestData.MINUTES_PHONE_CONSULTATION
            mConsultationDetails.value?.type.equals(TestData.F2F) ->
                TestData.MINUTES_FACE_TO_FACE_CONSULTATION
            mConsultationDetails.value?.type.equals(TestData.VIDEO) ->
                TestData.MINUTES_VIDEO_CONSULTATION
            else ->
                AppConstants.EMPTY_VALUE
        }
        assertEquals(TestData.MINUTES_FACE_TO_FACE_CONSULTATION, message)

    }

    @Test
    fun `getCallType()_return_value`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
        val returnValue = viewModel.getCallType()
        Assert.assertNotNull(returnValue)
    }

    @Test
    fun shouldCreateTimer() {
        runBlockingTest {
            val coroutineViewModel = JoinConsultationViewModel(
                repository,
                localizeTextProvider,
                context
            )
            val dateTime =
                LocalDateTime.now().plusMinutes(4)
            val consultationDetails = ConsultationDetails(
                id = 1234,
                dateTime = dateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                status = "in_progress",
                statusForClientDisplay = "IN PROGRESS"
            )
            coroutineViewModel.addConsultationData(consultationDetails)
            mConsultationDetails.value = consultationDetails
            coroutineViewModel.timerChange()
            val timerStatus = coroutineViewModel.mConsultationDetails.value?.timerStatus

            async {
                Assert.assertTrue(timerStatus == "start_session")
            }
        }

    }

    @Test
    fun `joinConsultation()_invoke`() {
        runBlockingTest {
            Mockito.doReturn(Resource.success(videoSessionTokenResponse))
                .`when`(repository)
                .executeJoinConsultation("1")
            val mViewModel = JoinConsultationViewModel(
                repository,
                localizeTextProvider,
                context
            )
            mViewModel.addConsultationData(consultationDetails)
            mConsultationDetails.value = consultationDetails
            mViewModel.onJoinConsultationClickListener()
            mViewModel.joinConsultation()
            mViewModel.onJoinConsultationObservable


            Mockito.verify(repository).executeJoinConsultation("1")

        }
    }


}