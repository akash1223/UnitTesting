package com.inmoment.moments.home.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmoment.moments.framework.common.*
import com.inmoment.moments.framework.common.AppConstants.PAGE_ITEM_SIZE
import com.inmoment.moments.framework.datamodel.*
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.home.ActivityLogDataManager
import com.inmoment.moments.home.ActivityLogEnum
import com.inmoment.moments.home.DashboardService
import com.inmoment.moments.home.MomentType
import com.inmoment.moments.home.model.ActivityLogModel
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.login.UserSignManager
import com.inmoment.moments.menu.collection.CollectionDataManager
import com.inmoment.moments.menu.saved_views.SavedViewsManager
import com.inmoment.moments.program.ProgramDataManager
import com.inmoment.moments.program.model.Program
import com.inmoment.moments.userprofile.UserProfileService
import com.lysn.clinician.utility.extensions.forceRefresh
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val programDataManager: ProgramDataManager,
    private val dashboardService: DashboardService,
    private val userProfileService: UserProfileService,
    private val savedViewsManager: SavedViewsManager,
    private val activityLogDataManager: ActivityLogDataManager,
    private val collectionDataManager: CollectionDataManager,
    private val userSignManager: UserSignManager,
    private val sharedPrefsInf: SharedPrefsInf
) :
    ViewModel() {

    val TAG = "HomeViewModel"

    var feedDadaList = MutableLiveData<MutableList<Feed>>(mutableListOf())
    var collectionList = mutableListOf<CollectionModel>()
    val favoriteList = mutableListOf<String>()
    var activeSavedView = MutableLiveData<SavedViewsListResponseData>()

    private var actionPerformFeedInfo: String = AppConstants.EMPTY_VALUE
    var pageCount: Int = 0
    var dataSourceType = MutableLiveData(MomentType.SAVED_VIEWS)
    val isProgramDbExist = MutableLiveData<Boolean>()
    private var _selectedProgram = MutableLiveData<Program>()
    val selectedProgram: LiveData<Program>
        get() = _selectedProgram


    init {
        getSelectedSavedView()
        fetchSelectedProgram()
    }

    private fun fetchSelectedProgram() {
        viewModelScope.launch {

            activeSavedView.value =
                async(Dispatchers.IO) { savedViewsManager.getSelectedSavedViewFromDb() }.await()
            isProgramDbExist.value =
                async(Dispatchers.IO) { programDataManager.checkProgramTableExist() }.await()

        }
        programDataManager.getSelectedProgram().observeForever {
            _selectedProgram.postValue(it)
        }
    }

    fun addToMomentReadList(firstVisiblePosition: Int, lastVisiblePosition: Int) {

        val readMomentList = mutableListOf<Feed>()
        Logger.i("${TAG} Position", "first->$firstVisiblePosition, last->$lastVisiblePosition")

        if (firstVisiblePosition >= 0 && lastVisiblePosition >= 0) {
            for (position in firstVisiblePosition..lastVisiblePosition) {
                feedDadaList.value?.get(position)?.let {
                    if (!it.experienceId.isNullOrEmpty() && !it.wasRead!!)
                        readMomentList.add(it)
                }
            }
            if (readMomentList.isNotEmpty()) {
                Logger.i("${TAG} readMomentList->", readMomentList.size.toString())
                viewModelScope.launch {


                    val status =
                        withContext(Dispatchers.IO) { dashboardService.markMomentRead(readMomentList.toList()) }
                    Logger.i("${TAG} Network Response->", status.toString())
                    if (status > 0) {
                        readMomentList.forEach {
                            val updatedFeed = it?.copy(wasRead = true)
                            val recordModifyPosition = feedDadaList.value?.indexOf(it)
                            Logger.i(
                                "${TAG} status.await->feedDadaList.size",
                                feedDadaList.value?.size.toString()
                            )
                            Logger.i(
                                "${TAG} status.await->recordModifyPosition",
                                recordModifyPosition.toString()
                            )
                            //Logger.i("${TAG} status.await->updatedFeed",updatedFeed.toString())
                            if (updatedFeed != null && -1 < recordModifyPosition!! && recordModifyPosition < feedDadaList.value?.size!!) {
                                feedDadaList.value?.set(recordModifyPosition!!, updatedFeed)
                            }

                        }
                        feedDadaList.forceRefresh()
                    }
                }
            }
        }
    }

    /*fun markMomentRead() {
        viewModelScope.launch {
            if (readMomentList.isNotEmpty()) {
                val status = dashboardService.markMomentRead(readMomentList.toList())
                if (status == 1) readMomentList.clear()
            }
        }
    }*/

    fun getUserInfo(): OperationResult<UserProfileResponseData> {
        return dashboardService.getUserDetails(viewModelScope)
    }

    fun saveActionClickFeedInfo(experienceId: String) {
        actionPerformFeedInfo = experienceId
    }

    fun getSavedActionFeedInfo(): String {
        return actionPerformFeedInfo
    }

    fun storeProgramAndAccountToDB(program: UserProgramsResponseData) {
        programDataManager.storeProgramAndAccount(program, viewModelScope)
    }

    fun saveUserName(firstName: String, lastName: String) {
        sharedPrefsInf.put(SharedPrefsInf.PREF_FIRST_NAME, firstName)
        sharedPrefsInf.put(SharedPrefsInf.PREF_LAST_NAME, lastName)
    }

    fun saveUserData(firstName: String, lastName: String, userId: String) {
        userProfileService.saveUserData(firstName, lastName, userId)
    }

    fun getSavedViews(): OperationResult<List<SavedViewsListResponseData>> {
        return savedViewsManager.getSavedViews(viewModelScope)
    }

    fun getMoment(refresh: Boolean): OperationResult<List<MomentsResponseData>> {
        if (refresh) pageCount = 0
        pageCount++
        val requestParam = HomeDashBoardRequestParam(
            pageCount,
            PAGE_ITEM_SIZE
        )
        return dashboardService.getMoments(requestParam, viewModelScope)
    }

    fun getMomentActivityLog(experienceId: String): OperationResult<List<ActivityLogModel>> {
        return activityLogDataManager.getActivityLog(experienceId, viewModelScope)
    }

    fun getPrograms(): OperationResult<UserProgramsResponseData> {
        return programDataManager.getAccountPrograms(viewModelScope)
    }

    fun logActivity(
        activityLogEnum: ActivityLogEnum,
        activityDescription: String,
        experienceId: String
    ): OperationResult<ActivityLogResponseData> {
        val activityLogRequestParam = ActivityLogRequestParam(
            activityLogEnum.value, activityDescription, experienceId
        )
        return activityLogDataManager.logActivity(activityLogRequestParam, viewModelScope)
    }

    fun convertActivityResponseToModel(activityLogResponseData: ActivityLogResponseData): ActivityLogModel {
        return activityLogDataManager.convertActivityResponseToModel(activityLogResponseData)
    }

    fun addToFavourite(
        feed: Feed,
        myCollection: String
    ): OperationResult<CollectionOperationResponseData> {
        val myFav: CollectionModel? = collectionList.find { it -> it.label == myCollection }

        return if (myFav != null && myFav.records.contains(feed.experienceId)) {
            OperationResult<CollectionOperationResponseData>().generateError()
        } else if (myFav != null) {
            feed.experienceId?.let {
                myFav.records.add(it)
                favoriteList.add(it)
            }
            val collectionParam =
                CreateCollectionRequestParam(myCollection, myFav.records, myFav.id)
            collectionDataManager.updateCollection(collectionParam, viewModelScope)
        } else {
            feed.experienceId?.let { favoriteList.add(it) }
            val collectionParam = CreateCollectionRequestParam(
                myCollection,
                listOf(feed.experienceId!!)
            )
            collectionDataManager.createCollection(collectionParam, viewModelScope)
        }
    }

    fun removeFromCollection(
        feed: Feed
    ): OperationResult<CollectionOperationResponseData> {
        val collection: CollectionModel? =
            collectionDataManager.getStoreCollectionData(collectionList)

        return if (collection != null) {
            feed.experienceId?.let {
                collection.records.remove(it)
                favoriteList.remove(it)
            }
            val collectionParam =
                CreateCollectionRequestParam(
                    collection.label,
                    collection.records.toList(),
                    collection.id
                )
            collectionDataManager.updateCollection(collectionParam, viewModelScope)

        } else {
            OperationResult<CollectionOperationResponseData>().generateError()
        }
    }


    /* fun getDefaultProgram(): Program? {
         *//*var program: Program? = null
        val (_, defaultProgramId) = sharedPrefsInf.getDefaultAccountAndProgramId()
        programList.value?.forEach { it1 ->
            it1.programList.forEach { pro ->
                if (defaultProgramId == pro.id) {
                    program = pro
                }
            }
        }*//*
        return selectedProgram
    }

*/
    fun storeSavedViewsData(data: List<SavedViewsListResponseData>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!data.isNullOrEmpty())
                data[0].activeSavedView = true
            savedViewsManager.saveSavedViewsInDb(data)
        }
        if (data.isEmpty()) {
            activeSavedView.value = null
            savedViewsManager.storeSavedViewsDada(null)
        } else {
            savedViewsManager.storeSavedViewsDada(data[0])
        }

    }

    private fun getSelectedSavedView() {
        savedViewsManager.getSelectedSavedView().observeForever {
            activeSavedView.value = it
        }
    }

    fun getCollection(): OperationResult<CollectionsResponseData> {
        return collectionDataManager.getCollection(viewModelScope)
    }

    fun setCollection(collectionsResponseData: CollectionsResponseData) {
        collectionsResponseData?.data?.collections?.let {
            collectionList = it
            favoriteList.clear()
            collectionList.forEach {
                favoriteList.addAll(it.records)
            }
        }
    }

    fun addToCollectionOrUpdateCollection(collectionModel: CollectionModel) {
        val myFav: CollectionModel? = collectionList.find { it.id == collectionModel.id }
        if (myFav != null) {
            myFav.records.clear()
            myFav.records.addAll(collectionModel.records)
        } else {
            collectionList.add(collectionModel)
        }
        favoriteList.clear()
        collectionList.forEach {
            favoriteList.addAll(it.records)
        }
    }

    fun enableLoaderFooter() {
        val feed = feedDadaList.value?.last()
        feed?.rowType = FOOTER_ROW
        feedDadaList.forceRefresh()
    }

    fun disableLoaderFooter() {
        val feed = feedDadaList.value?.last()
        feed?.rowType = CAUGHT_UP
        feedDadaList.forceRefresh()
    }

    fun removeAllAndAddItem(feeds: List<Feed>, momentsTitle: String) {
        feedDadaList.value?.clear()
        val feed = Feed()
        feed.accountName = momentsTitle
        feed.rowType = FIRST_ROW
        feedDadaList.value?.add(feed)
        feedDadaList.value?.addAll(feeds)
        if (feedDadaList.value?.size!! > 1) {
            val listEndView = Feed()
            listEndView.rowType = CAUGHT_UP
            feedDadaList.value?.add(listEndView)
        }
        feedDadaList.forceRefresh()
    }

    fun addItems(feeds: List<Feed>) {
        feedDadaList.value?.addAll(feedDadaList.value?.lastIndex!!, feeds)
        feedDadaList.forceRefresh()
    }

    fun updateActivityLog(listActivityLog: List<ActivityLogModel>, experienceId: String) {
        val feed = feedDadaList.value?.find { it.experienceId == experienceId }
        val updatedFeed = feed?.copy(loadingEffect = false)
        val recordModifyPosition = feedDadaList.value?.indexOf(feed)
        updatedFeed?.activitiesLogModel?.addAll(listActivityLog)
        if (updatedFeed != null) {
            feedDadaList.value?.set(recordModifyPosition!!, updatedFeed)
        }

        feedDadaList.forceRefresh()
    }

    fun addActivityLog(response: ActivityLogResponseData?) {
        if (response != null) {
            val activityLog = convertActivityResponseToModel(response)
            val feed = feedDadaList.value?.find { it.experienceId == activityLog.experienceId }
            val updatedFeed = feed?.copy()
            updatedFeed?.activityLogCount = updatedFeed?.activityLogCount!! + 1
            val recordModifyPosition = feedDadaList.value?.indexOf(feed)
            if (updatedFeed != null) {
                feedDadaList.value?.set(recordModifyPosition!!, updatedFeed)
            }
            feedDadaList.forceRefresh()
        }
    }

    fun removeItem(experienceId: String) {
        feedDadaList.value?.filter { it -> it.experienceId == experienceId }?.forEach {
            feedDadaList.value!!.remove(it)
        }
        // 0-> Header
        //1-(n-1) Moments Data
        //n- CAUGHT_UP
        // Check if list contains no moments then remove second last element that is CAUGHT_UP
        if (feedDadaList.value?.size == 2) {
            feedDadaList.value!!.removeAt(1)
        }
        feedDadaList.forceRefresh()
    }

    fun addItem(feed: Feed, position: Int) {
        feedDadaList.value?.add(position, feed)
        // In moments list added one element then add  CAUGHT_UP
        if (feedDadaList.value?.size == 2) {
            val listEndView = Feed()
            listEndView.rowType = CAUGHT_UP
            feedDadaList.value?.add(listEndView)
        }
        feedDadaList.forceRefresh()
    }

    fun getFeedPosition(feed: Feed): Int? {
        return feedDadaList.value?.indexOf(feed)
    }

    fun getMomentsTitle(): String {
        return dashboardService.getMomentsTitle()
    }

    fun convertMomentResponseToFeed(momentResponse: List<MomentsResponseData>): List<Feed> {
        return dashboardService.convertMomentResponseToFeed(momentResponse)
    }

    fun getMomentType(): String {
        return dashboardService.getMomentType()
    }

    fun getLoginSessionDetails(): Pair<String, Long> {
        return userSignManager.getLoginSessionDetails()
    }
}