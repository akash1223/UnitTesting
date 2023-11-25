package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lysnclient.model.WBTOutputObservation
import com.example.lysnclient.model.WBTOutputScreenResponse
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.MockResponseFileReader
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

class WBTOutputScreenViewModelTest {
    private lateinit var viewModel: WBTOutputScreenViewModel
    private lateinit var repository: AppRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = WBTOutputScreenViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verifyGetWBTInterpretationList()ReturnWBTInterpretationList`() {
        viewModel.getWBTInterpretationList()
        Mockito.verify(repository, times(1)).getWBTInterpretation()
    }

    @Test
    fun `verifyGetWBTInterpretationList()InvokeRepositoryMethodGetWBTInterpretation`() {
        val mWBTData = getWbtOutputData()
        whenever(repository.getWBTInterpretation()).thenReturn(
            mWBTData?.insightsMessages
        )
        viewModel.getWBTInterpretationList()
        Assert.assertTrue(viewModel.listOfInterpretation.isNotEmpty())
        Assert.assertTrue(viewModel.listOfInterpretation.size == mWBTData?.insightsMessages?.size)
        Assert.assertTrue(viewModel.interpretationText.value?.isNotEmpty() ?: false)
        Mockito.verify(repository, times(1)).getWBTInterpretation()
    }


    @Test
    fun `verifyBtnNextOnClick()ChangeObservableValue`() {
        val mWBTData = getWbtOutputData()
        whenever(repository.getWBTInterpretation()).thenReturn(
            mWBTData?.insightsMessages
        )
        viewModel.getWBTInterpretationList()
        viewModel.btnNextOnClick()
        Assert.assertTrue(viewModel.currentQuestionIndex.value == 1)
        Assert.assertTrue(viewModel.interpretationText.value == mWBTData?.insightsMessages?.get(1))
    }

    @Test
    fun `verifyBtnBackOnclick()ChangeObservableValue`() {
        val mWBTData = getWbtOutputData()
        whenever(repository.getWBTInterpretation()).thenReturn(
            mWBTData?.insightsMessages
        )
        viewModel.currentQuestionIndex.value = 2
        viewModel.getWBTInterpretationList()
        viewModel.btnBackOnclick()
        Assert.assertTrue(viewModel.currentQuestionIndex.value == 1)
        Assert.assertTrue(viewModel.interpretationText.value == mWBTData?.insightsMessages?.get(1))
    }

    @Test
    fun `verifyBtnBackOnclick()NotChangeObservableValueWhenCurrentIndexIsZero`() {
        val mWBTData = getWbtOutputData()
        whenever(repository.getWBTInterpretation()).thenReturn(
            mWBTData?.insightsMessages
        )
        viewModel.getWBTInterpretationList()
        viewModel.btnBackOnclick()
        Assert.assertNotEquals(1, viewModel.currentQuestionIndex.value)
    }

    @Test
    fun `verifyBtnNextOnClick()NotChangeObservableValueWhenCurrentIndexIsLast`() {
        val mWBTData = getWbtOutputData()
        whenever(repository.getWBTInterpretation()).thenReturn(
            mWBTData?.insightsMessages
        )
        viewModel.currentQuestionIndex.value = mWBTData?.insightsMessages?.size?.minus(1)
        viewModel.getWBTInterpretationList()
        viewModel.btnNextOnClick()
        Assert.assertEquals(mWBTData?.insightsMessages?.size?.minus(1), viewModel.currentQuestionIndex.value)
    }

    @Test
    fun `verifyBtnNextOnClick()IncreaseCurrentQuestionIndex`() {
        val arrayList = ArrayList<String>()
        arrayList.add("First")
        arrayList.add("Second")
        viewModel.listOfInterpretation = arrayList
        val currentIndex = viewModel.currentQuestionIndex.value
        viewModel.btnNextOnClick()
        Assert.assertNotEquals(currentIndex, viewModel.currentQuestionIndex.value)
    }

    @Test
    fun `verifyBtnNextOnClick()DecreaseCurrentQuestionIndex`() {
        val arrayList = ArrayList<String>()
        arrayList.add("First")
        arrayList.add("Second")
        arrayList.add("Third")
        arrayList.add("Fourth")
        viewModel.listOfInterpretation = arrayList
        viewModel.currentQuestionIndex.value = arrayList.size
        viewModel.btnBackOnclick()
        Assert.assertNotEquals(arrayList.size, viewModel.currentQuestionIndex.value)
    }

    private fun getWbtOutputData(): WBTOutputObservation? {
        val myType = object : TypeToken<WBTOutputScreenResponse>() {}.type
        val response = Gson().fromJson<WBTOutputScreenResponse>(
            MockResponseFileReader("WBTOutputScreenInterResponse.json").content,
            myType
        )
        return response?.mWBTOutputObservation
    }

    @Test
    fun `verifyBtnBackContinueToHomeClick()ChangeObservableValue`() {
        viewModel.btnBackContinueToHomeClick()
        Assert.assertNotNull(viewModel.btnContinueToHomeObservable.value)
        Assert.assertTrue(viewModel.btnContinueToHomeObservable.value == true)
    }

    @Test
    fun `verifybtnFindPsychologistClick()ChangeObservableValue`() {
        viewModel.btnFindPsychologistClick()
        Assert.assertNotNull(viewModel.btnFindPsychologistObservable.value)
        Assert.assertTrue(viewModel.btnFindPsychologistObservable.value == true)
    }
}
