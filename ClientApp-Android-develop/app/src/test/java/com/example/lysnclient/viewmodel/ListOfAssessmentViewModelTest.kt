package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.model.AssessmentType
import com.example.lysnclient.model.BaseResponse
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.MockResponseFileReader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ListOfAssessmentViewModelTest {

    private lateinit var viewModel: ListOfAssessmentViewModel
    private lateinit var repository: AppRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = ListOfAssessmentViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verifyOnBtnContinueEmailClickListener()Invoke`() {
        val listOfAssessmentType = ArrayList<AssessmentType>()
        listOfAssessmentType.add(
            AssessmentType(
                6, "DAss-21", "", "", "", 1, "", "",true, "", ""
            ,"")
        )
        viewModel.listOfAssessmentLiveData = MutableLiveData()
        viewModel.listOfAssessmentLiveData.value = BaseResponse(
            status = ResponseStatus.SUCCESS,
            apiResponse = listOfAssessmentType
        )
        viewModel.onAssessmentItemClick(0)
        Assert.assertTrue(viewModel.navigateToDetailObservable.value == 6)
    }

    @Test
    fun `verifyGetListOfAssessments()ReturnSuccessWithData`() {
        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        val response = Gson().fromJson<List<AssessmentType>>(
            MockResponseFileReader("AssessmentListResponse.json").content,
            myType
        )
        val mainApiResponse = MutableLiveData<BaseResponse<List<AssessmentType>>>()
        mainApiResponse.value = BaseResponse(
            ResponseStatus.SUCCESS, "",
            response
        )
        whenever(repository.executeGetListOfAssessment()).thenReturn(
            mainApiResponse
        )
        val methodName = "getListOfAssessments"
        val methodInvocation = ListOfAssessmentViewModel::class.java
            .getDeclaredMethod(methodName)
        methodInvocation.isAccessible = true
        methodInvocation.invoke(viewModel)

        val actualResponse: List<AssessmentType>? =
            viewModel.listOfAssessmentLiveData.value?.apiResponse
        Assert.assertTrue(viewModel.listOfAssessmentLiveData.value != null)
        Assert.assertTrue(actualResponse?.isNotEmpty() ?: false)
        Assert.assertTrue(actualResponse?.get(0)?.assessmentType?.isNotEmpty() ?: false)
        Assert.assertTrue(
            actualResponse?.get(0)?.name?.isNotEmpty() ?: false
        )
    }
}
