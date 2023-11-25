package com.lysn.clinician.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.CancelConsultationDetails
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.repository.ConsultationRepository
import com.lysn.clinician.ui.consultation_details.ConsultationDetailsViewModel
import com.lysn.clinician.ui.consultation_list.ConsultationListViewModel
import com.lysn.clinician.ui.join_consultation.JoinConsultationViewModel
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.TestCoroutineRule
import com.lysn.clinician.utils.TestData
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@ExperimentalCoroutinesApi
class ConsultationDetailsViewModelTest{

    private lateinit var repository: ConsultationRepository
    private lateinit var viewModel: ConsultationDetailsViewModel
    private  lateinit var context: Context
    private  lateinit var consultationDetails : ConsultationDetails
    private lateinit var localizeTextProvider: LocalizeTextProvider

    private var mConsultationDetails = MutableLiveData<ConsultationDetails>()


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var apiUsersObserver: Observer<Resource<CancelConsultationDetails>>

    @Before
    fun setup() {
        repository = mock()
        context = mock()
        localizeTextProvider = mock()
        viewModel =
            ConsultationDetailsViewModel(
                repository,
                context,
                localizeTextProvider
            )
        consultationDetails = ConsultationDetails(id = 1,dateTime = "2020-08-09T01:30:00+02:00",durationMinutes = 30,type = "phone",canCancel = true,
        client = ConsultationDetails.Client(firstName = "Tomasz",lastName = "Mucha"))
    }

    @Test
    fun test_objects_not_null() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(context)
        Assert.assertNotNull(localizeTextProvider)

    }

    @Test
    fun `addConsultationData()_invoke`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
        Assert.assertNotNull(viewModel.mConsultationDetails.value)
    }

    @Test
    fun `onCancelConsultationClickListener()_invoke`() {
        viewModel.onCancelConsultationClickListener()
        Assert.assertTrue(viewModel.onCancelConsultationObservable.value ?: false)
    }

    @Test
    fun `getConsultationDetail()_return_value`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
       val returnValue =  viewModel.getConsultationDetail()
        Assert.assertNotNull(returnValue)
        Assert.assertEquals("2020-08-09T01:30:00+02:00",returnValue.value?.dateTime)
        Assert.assertEquals(30,returnValue.value?.durationMinutes)
    }

    @Test
    fun `getDate()_return_value`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
        val returnValue =  viewModel.getDate()
        Assert.assertNotNull(returnValue)
    }

    @Test
    fun `getCallTypeMessage()_return_value`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails

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
        val returnValue = viewModel.getCallTypeMessage()
        Assert.assertNotNull(returnValue)

        Assert.assertEquals(message,TestData.MINUTES_PHONE_CONSULTATION)


    }

    @Test
    fun `getCallType()_return_value`() {
        viewModel.addConsultationData(consultationDetails)
        mConsultationDetails.value = consultationDetails
         val returnValue =  viewModel.getCallType()
        Assert.assertNotNull(returnValue)
    }
    @Test
    fun `createTimer()_invoke`() {
            val coroutineViewModel = ConsultationDetailsViewModel( repository,
                context,
                localizeTextProvider)

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
                Assert.assertTrue(timerStatus == "start_session")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `cancelConsultation()_invoke`() {
        testCoroutineRule.runBlockingTest {
            doReturn(Any())
                .`when`(repository)
                .executeCancelConsultation("1")

            val mViewModel = ConsultationDetailsViewModel(
                repository,
                context,
                localizeTextProvider
            )
            mViewModel.addConsultationData(consultationDetails)
            mConsultationDetails.value = consultationDetails
            mViewModel.onCancelConsultationClickListener()
            mViewModel.cancelConsultation()
            mViewModel.onCancelConsultationObservable


            verify(repository).executeCancelConsultation("1")
        }
    }

}