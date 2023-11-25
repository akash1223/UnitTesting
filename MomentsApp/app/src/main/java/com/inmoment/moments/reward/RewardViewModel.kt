package com.inmoment.moments.reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.datamodel.ActivityLogRequestParam
import com.inmoment.moments.framework.datamodel.ActivityLogResponseData
import com.inmoment.moments.framework.datamodel.RewardsPointRequestParam
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_STRING_DEFAULT
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_USER_EMAIL_ID
import com.inmoment.moments.home.ActivityLogDataManager
import com.inmoment.moments.home.ActivityLogEnum
import com.inmoment.moments.reward.model.RewardSearchModel
import com.lysn.clinician.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@HiltViewModel
class RewardViewModel @Inject constructor(
    private val rewardDataManager: RewardDataManager,
    private val activityLogDataManager: ActivityLogDataManager,
    private val sharedPrefsInf: SharedPrefsInf
) : ViewModel() {

    var nameField = MutableLiveData(AppConstants.EMPTY_VALUE)
    var selectedSearchItem = MutableLiveData<RewardSearchModel>()
    var recognitionReasonMsg = MutableLiveData(AppConstants.EMPTY_VALUE)
    var pointField = MutableLiveData(AppConstants.EMPTY_VALUE)
    var itemSelected = AtomicBoolean(false)


    val applyFilter = SingleLiveEvent<Boolean>()
    private var _searchEmployeeList = MutableLiveData<List<RewardSearchModel>>()
    val searchEmployeeList: LiveData<List<RewardSearchModel>>
        get() = _searchEmployeeList

    private fun setSearchResult(searchData: List<RewardSearchModel>) {
        _searchEmployeeList.value = searchData
    }


    private fun dataFromNetwork(query: String): Flow<List<RewardSearchModel>> {
        return flow {
            emit(rewardDataManager.getSearchResult(query))
            /* delay(1000)
             emit(PersonNameModel.personName.filter { it1 -> it1.startsWith(query,true) }.map { it2->
                 RewardSearchModel(null,null,null,null,null,it2)
             })*/
        }
    }

    fun postRewardPoints(): OperationResult<Boolean> {

        val rewardedByEmail = sharedPrefsInf.get(PREF_USER_EMAIL_ID, PREF_STRING_DEFAULT)
        val rewardsPointRequestParam =
            selectedSearchItem.value?.email?.let {
                RewardsPointRequestParam(
                    it, pointField.value!!,
                    recognitionReasonMsg.value!!, rewardedByEmail
                )
            }
        return rewardDataManager.postRewardPoints(rewardsPointRequestParam!!, viewModelScope)
    }

    fun logActivity(
        experienceId: String,
        activityLogEnum: ActivityLogEnum,
        activityDescription: String
    ): OperationResult<ActivityLogResponseData> {
        val activityLogRequestParam = ActivityLogRequestParam(
            activityLogEnum.value, activityDescription, experienceId
        )
        return activityLogDataManager.logActivity(activityLogRequestParam, viewModelScope)
    }

    fun isSamePerson(): Boolean {
        val rewardedByEmail = sharedPrefsInf.get(PREF_USER_EMAIL_ID, PREF_STRING_DEFAULT)
        return selectedSearchItem.value?.userName.equals(rewardedByEmail)
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun setAutoCompleteEvent(query: StateFlow<String>) {
        viewModelScope.launch {
            try {
                query.debounce(300).filter { query ->
                    var isFilter = false
                    try {

                        if (!itemSelected.getAndSet(false)) {
                            nameField.value = AppConstants.EMPTY_VALUE
                            if (query.length < 3) {
                                setSearchResult(listOf())
                            } else {
                                if (searchEmployeeList.value?.isNotEmpty() == true) {
                                    applyFilter.value = true
                                } else {
                                    isFilter = true
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        isFilter = false
                    }
                    return@filter isFilter
                }.distinctUntilChanged()
                    .flatMapLatest { query ->
                        dataFromNetwork(query)
                            .catch {
                                emitAll(flowOf(listOf()))
                            }
                    }
                    .flowOn(Dispatchers.Main)
                    .collect { it ->
                        setSearchResult(it)
                    }
            } catch (ex: Exception) {
                Logger.e("RewardViewModel", ex.stackTraceToString())
            }
        }
    }
}