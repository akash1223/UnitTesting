package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lysnclient.model.AssessmentType
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.MockResponseFileReader
import com.example.lysnclient.utils.TestData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class AssessmentDetailViewModelTest {
    private lateinit var repository: AppRepository
    private lateinit var viewModel: AssessmentDetailViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = AssessmentDetailViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verifyOnBtnBeginAssessmentClickListener()Invoke`() {
        viewModel.onBtnBeginAssessmentClick()
        Assert.assertTrue(viewModel.onBeginAssessmentObservable.value ?: false)
    }

    @Test
    fun `verifyFetchAssessmentDetailById()InvokeRepositoryMethodGetAssessmentById()`() {
        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        val response = Gson().fromJson<List<AssessmentType>>(
            MockResponseFileReader("AssessmentListResponse.json").content,
            myType
        )
        whenever(repository.getAssessmentById(TestData.TEST_ASSESSMENT_ID)).thenReturn(
            response[0]
        )
        viewModel.fetchAssessmentDetailById(TestData.TEST_ASSESSMENT_ID)
        Assert.assertNotNull(viewModel.selectedAssessmentDetails)
        Assert.assertNotNull(viewModel.selectedAssessmentDetails?.id)

        Assert.assertTrue(viewModel.assessmentEstimatedTimeField.value == viewModel.selectedAssessmentDetails?.estimatedTime)
        Assert.assertTrue(viewModel.assessmentCodeField.value == viewModel.selectedAssessmentDetails?.code)
        Assert.assertTrue(viewModel.assessmentIntroField.value == viewModel.selectedAssessmentDetails?.intro)
        Assert.assertTrue(viewModel.assessmentIDField.value == viewModel.selectedAssessmentDetails?.id)
        Assert.assertTrue(viewModel.assessmentTitleField.value == viewModel.selectedAssessmentDetails?.name)
    }

    @Test
    fun `verifyFetchAssessmentDetailById()ReturnAssessmentTypeData()`() {
        viewModel.fetchAssessmentDetailById(TestData.TEST_ASSESSMENT_ID)
        Mockito.verify(repository, times(1)).getAssessmentById(TestData.TEST_ASSESSMENT_ID)
    }

    @Test
    fun `verifyNavigateBack()NeverPublishTrueValue`() {
        viewModel.navigateBack()
        Assert.assertTrue(viewModel.navigateBackObservable.value ?: false)
    }

    @Test
    fun `verifyNavigateBack()NeverPublishFalseValue`() {
        viewModel.navigateBack()
        Assert.assertNotSame(false, viewModel.navigateBackObservable.value!!)
    }
}