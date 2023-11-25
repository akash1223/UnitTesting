package com.lysn.clinician.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.model.ConsultationsDetailsResponse
import com.lysn.clinician.repository.ConsultationRepository
import com.lysn.clinician.ui.consultation_list.ConsultationListViewModel
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ConsultationListViewModelTest {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var consultationRepository: ConsultationRepository

    @Mock
    private lateinit var localizeTextProvider: LocalizeTextProvider

    @Mock
    private lateinit var apiObserver: Observer<Resource<ConsultationsDetailsResponse>>


    @Before
    fun setUp() {

    }

    @Test
    fun test_objects_not_null() {
        Assert.assertNotNull(consultationRepository)
        Assert.assertNotNull(localizeTextProvider)
        Assert.assertNotNull(apiObserver)
    }

    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() {
        testCoroutineRule.runBlockingTest {
            doReturn(Resource.success(ConsultationsDetailsResponse()))
                .`when`(consultationRepository).executeConsultationDetailsList()

            val coroutineViewModel = ConsultationListViewModel(consultationRepository, localizeTextProvider)
            coroutineViewModel.getConsultations().observeForever(apiObserver)
            verify(consultationRepository, times(1)).executeConsultationDetailsList()
            verify(apiObserver).onChanged(Resource.success(ConsultationsDetailsResponse()))
            coroutineViewModel.getConsultations().removeObserver(apiObserver)
        }
    }

    @Test
    fun givenServerResponseError_whenFetch_shouldReturnError() {
        testCoroutineRule.runBlockingTest {
            val errorMessage = "Error Message For You"
            doReturn(
                Resource.error(
                    RuntimeException(errorMessage).toString(), null
                )
            )
                .`when`(consultationRepository)
                .executeConsultationDetailsList()
            val coroutineViewModel = ConsultationListViewModel(consultationRepository, localizeTextProvider)
            coroutineViewModel.getConsultations().observeForever(apiObserver)
            verify(consultationRepository).executeConsultationDetailsList()
            verify(apiObserver).onChanged(
                Resource.error(
                    RuntimeException(errorMessage).toString(),
                    null
                )
            )
            coroutineViewModel.getConsultations().removeObserver(
                apiObserver
            )
        }
    }

    @Test
    fun `verify_setUpcomingConsultation()_invoked`() {
        val consultationDetails = ConsultationDetails(id = 1234)
        val viewModel = ConsultationListViewModel(consultationRepository, localizeTextProvider)
        viewModel.setUpcomingConsultation(listOf(consultationDetails))
        Assert.assertTrue(viewModel.getUpcomingConsultation().value?.get(0)?.id == 1234)
    }

    @Test
    fun verify_setReadyToJoin_invoked() {
        val consultationDetails = ConsultationDetails(id = 1234)
        val viewModel = ConsultationListViewModel(consultationRepository, localizeTextProvider)
        viewModel.setReadyToJoin(listOf(consultationDetails))
        Assert.assertTrue(viewModel.getReadyToJoin().value?.get(0)?.id == 1234)
    }

    @Test
    fun shouldCreateTimer() {
           val coroutineViewModel = ConsultationListViewModel(consultationRepository, localizeTextProvider)
             val dateTime =
                LocalDateTime.now().plusMinutes(4)
            val consultationDetails = ConsultationDetails(
                id = 1234,
                dateTime = dateTime.format(ISO_DATE_TIME),
                status = "in_progress",
                statusForClientDisplay = "IN PROGRESS"
            )
            coroutineViewModel.setReadyToJoin(listOf(consultationDetails))
            coroutineViewModel.timerChange()
            val timerStatus = coroutineViewModel.getReadyToJoin().value?.get(0)
            Assert.assertTrue(timerStatus?.timerStatus == "start_session")

        }


}



