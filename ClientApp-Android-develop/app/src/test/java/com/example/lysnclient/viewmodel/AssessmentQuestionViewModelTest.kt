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

class AssessmentQuestionViewModelTest {

    private lateinit var viewModel: AssessmentQuestionViewModel
    private lateinit var repository: AppRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = AssessmentQuestionViewModel(repository)
    }

    @Test
    fun `verifyFetchAssessmentQueList()InvokeRepositoryMethodGetAssessmentQuestionsById`() {
        viewModel.fetchAssessmentQueList(TestData.TEST_ASSESSMENT_ID)
        Mockito.verify(repository, times(1)).getAssessmentQuestionsById(TestData.TEST_ASSESSMENT_ID)
    }

    @Test
    fun `verifyFetchAssessmentQueList()InvokeRepositoryMethodGetAssessmentById()`() {
        viewModel.fetchAssessmentQueList(TestData.TEST_ASSESSMENT_ID)
        Mockito.verify(repository, times(1)).getAssessmentById(TestData.TEST_ASSESSMENT_ID)
    }

    @Test
    fun `verifyOnBtnBackClick()ChnageObservableValue`() {
        viewModel.onBtnBackClick()
        Assert.assertTrue(viewModel.moveToPreviousQues.value ?: false)
    }

    @Test
    fun `verifyOnBtnCloseClickListener()ChnageObservableValue`() {
        viewModel.onBtnCloseClickListener()
        Assert.assertTrue(viewModel.onCloseBtnClickObservable.value ?: false)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verifyFetchAssessmentQueList()ReturnSuccessWithData`() {
        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        val response = Gson().fromJson<List<AssessmentType>>(
            MockResponseFileReader("AssessmentListResponse.json").content,
            myType
        )
        val assessmentType = response.get(0)
        val assessmentQueList = assessmentType.listOfQuestions
        whenever(repository.getAssessmentById(TestData.TEST_ASSESSMENT_ID)).thenReturn(
            assessmentType
        )
        whenever(repository.getAssessmentQuestionsById(TestData.TEST_ASSESSMENT_ID)).thenReturn(
            assessmentQueList
        )
        viewModel.fetchAssessmentQueList(TestData.TEST_ASSESSMENT_ID)
        Assert.assertNotNull(viewModel.selectedAssessmentDetails)
        Assert.assertTrue(viewModel.selectedAssessmentDetails?.name?.isNotEmpty() ?: false)
        Assert.assertTrue(viewModel.listOfQuestion.isNotEmpty())
        Assert.assertTrue(viewModel.listOfQuestion.get(0).label.isNotEmpty())
        Assert.assertTrue(viewModel.listOfQuestion.get(0).id > 0)
        Assert.assertTrue(viewModel.listOfQuestion.get(0).questionOptionType.isNotEmpty())
        Assert.assertTrue(viewModel.listOfQuestion.get(0).label.isNotEmpty())
        Assert.assertTrue(viewModel.listOfQuestion.get(0).listOfOptions.isNotEmpty())
        Assert.assertTrue(viewModel.totalQuestionNumber.value == viewModel.listOfQuestion.size)
    }
}