package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lysnclient.model.*
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.MockResponseFileReader
import com.example.lysnclient.utils.TestData.TEST_ASSESSMENT_ID
import com.example.lysnclient.utils.TestData.TEST_POSITION_INDEX
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

class ReviewAssessmentViewModelTest {

    private lateinit var repository: AppRepository
    private lateinit var viewModel: ReviewAssessmentViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = ReviewAssessmentViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verifyNavigateBack()ChangeObservableValutToTrue`() {
        viewModel.navigateOnClose()
        Assert.assertTrue(viewModel.navigateBackOnCloseObservable.value ?: false)
    }

    @Test
    fun `verifyNavigateBack()NeverPublishFalsValue`() {
        viewModel.navigateOnClose()
        Assert.assertNotSame(false, viewModel.navigateBackOnCloseObservable.value ?: false)
    }

    @Test
    fun `verifyExecuteSubmitAssessmentApi()Invoke`() {
        val listOfAssessmentValueRequest = ArrayList<AssessmentValueRequest>()
        listOfAssessmentValueRequest.add(
            AssessmentValueRequest(
                22,
                "",
                AssessmentAnswerRequest("")
            )
        )
        val assessmentDataRequest = AssessmentDataRequest(6, listOfAssessmentValueRequest)

        viewModel.executeSubmitAssessmentApi(assessmentDataRequest)
        Mockito.verify(repository, times(1)).submitAssessmentAPI(assessmentDataRequest)
    }

    @Test
    fun `verifyOnBtnSubmitAssessmentClick()ChangeObservableToTrue`() {
        viewModel.onBtnSubmitAssessmentClick()
        Assert.assertTrue(viewModel.onSubmitAssessmentObservable.value ?: false)
    }

    @Test
    fun `verifyOnEditItemClick()ChangeObservableToTrue`() {
        viewModel.onEditItemClick(TEST_POSITION_INDEX)
        Assert.assertEquals(TEST_POSITION_INDEX, viewModel.editItemPositionObservable.value ?: 0)
    }

    @Test
    fun `verifySetAssessmentId()ExecuteAndSetsValue`() {
        viewModel.setAssessmentId(TEST_ASSESSMENT_ID)

        Assert.assertEquals(TEST_ASSESSMENT_ID, viewModel.selectedAssessmentId)
    }

    @Test
    fun `verifyFetchAssessmentQueList()ExecuteAndReturnListOfQuestions`() {
        viewModel.setAssessmentId(TEST_ASSESSMENT_ID)
        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        val response = Gson().fromJson<List<AssessmentType>>(
            MockResponseFileReader("AssessmentListResponse.json").content,
            myType
        )
        whenever(repository.getAssessmentQuestionsById(TEST_ASSESSMENT_ID)).thenReturn(
            response[0].listOfQuestions
        )
        val methodName = "fetchAssessmentQueList"
        val methodInvocation = ReviewAssessmentViewModel::class.java
            .getDeclaredMethod(methodName, Int::class.java)
        methodInvocation.isAccessible = true
        methodInvocation.invoke(viewModel, TEST_ASSESSMENT_ID)
        Assert.assertTrue(viewModel.listOfQuestion.isNotEmpty())
    }

    @Test
    fun `verifyFetchSelectedQuestionOptions()ReturnArrayListOfQuestion`() {

        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        val response = Gson().fromJson<List<AssessmentType>>(
            MockResponseFileReader("AssessmentListResponse.json").content,
            myType
        )
        viewModel.listOfQuestion = response[0].listOfQuestions
        val data = viewModel.fetchSelectedQuestionOptions(viewModel.listOfQuestion[0].id)
        Assert.assertTrue(data.isNotEmpty())
    }

    @Test
    fun `verifySetListOfAssessmentAnswer()SetsValueToField`() {
        val listOfAns = ArrayList<AssessmentAnswer>()
        val assessmentAnswer = AssessmentAnswer("MultiChoice", 1, 0, "first", "all well", 0)
        val assessmentAnswer2 = AssessmentAnswer("MultiChoice", 2, 1, "Second", "good", 1)
        listOfAns.add(assessmentAnswer)
        listOfAns.add(assessmentAnswer2)
        viewModel.setListOfAssessmentAnswers(listOfAns)
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.isNotEmpty() ?: false)
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.size == 2)
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.get(0)?.questionLabel == "first")
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.get(0)?.questionType == "MultiChoice")
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.get(0)?.userAnswer == "all well")
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.get(0)?.questionId == 1)
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.get(0)?.quePosition == 0)
        Assert.assertTrue(viewModel.listOfAssessmentAnswer.value?.get(0)?.singleChoiceOptionPosition == 0)

    }

}