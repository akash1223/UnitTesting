package com.inmoment.moments.menu.saved_views

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inmoment.moments.framework.datamodel.SavedViewsListResponseData
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.database.SavedViewsDao
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_SAVED_SELECTED_SAVED_VIEW
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_STRING_DEFAULT
import com.inmoment.moments.home.MomentType
import com.lysn.clinician.utility.extensions.justTry
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@Suppress("RedundantSuspendModifier")
class SavedViewsManager @Inject constructor(
    private val apiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf,
    private val savedViewsDao: SavedViewsDao
) : TaskManager() {

    fun getSavedViews(coroutineScope: CoroutineScope) =
        execute(::getServedViewsInfo, coroutineScope)

    @VisibleForTesting
    suspend fun getServedViewsInfo(): OperationResult<List<SavedViewsListResponseData>> {
        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        return apiHelper.getSavedViews(xiContextHeader)
    }

    fun storeSavedViewsDada(data: SavedViewsListResponseData?): Boolean {
        data?.let {
            sharedPrefsInf.put(SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_ID, it.dataSourceId)
            sharedPrefsInf.put(SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_NAME, it.name)
            sharedPrefsInf.put(SharedPrefsInf.PREF_MOMENT_TYPE, MomentType.SAVED_VIEWS.value)
            sharedPrefsInf.put(SharedPrefsInf.PREF_SAVED_VIEW_ID, it.savedViewId)
            saveVisitedSavedView(it.savedViewId)
        } ?: kotlin.run {
            sharedPrefsInf.put(SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_ID, PREF_STRING_DEFAULT)
            sharedPrefsInf.put(SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_NAME, PREF_STRING_DEFAULT)
            sharedPrefsInf.put(SharedPrefsInf.PREF_MOMENT_TYPE, MomentType.SAVED_VIEWS.value)
            sharedPrefsInf.put(SharedPrefsInf.PREF_SAVED_VIEW_ID, PREF_STRING_DEFAULT)
        }
        return true
        /*
         return if(data.name != sharedPrefsInf.get(SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_NAME,SharedPrefsInf.PREF_STRING_DEFAULT)) {

        } else {
             false
         }*/
    }

    suspend fun saveSavedViewsInDb(data: List<SavedViewsListResponseData>?) {
        if (!data.isNullOrEmpty()) {
            data.map {
                it.isNonDxDataSource = isNonDX(it)
                it
            }
            savedViewsDao.removeSavedSelectedSavedView()
            savedViewsDao.insertSavedView(data)
        } else {
            savedViewsDao.removeSavedSelectedSavedView()
        }
    }

    private fun saveVisitedSavedView(savedViewId: String) {
        justTry {
            val savedData =
                sharedPrefsInf.get(PREF_SAVED_SELECTED_SAVED_VIEW, Gson().toJson(setOf<String>()))
            val dataList: MutableSet<String> =
                Gson().fromJson(savedData, object : TypeToken<Set<String>>() {}.type)
            dataList.add(savedViewId)
            sharedPrefsInf.put(PREF_SAVED_SELECTED_SAVED_VIEW, Gson().toJson(dataList))
        }
    }

    fun getVisitedSavedViewList(): Set<String> {
        return try {
            val savedData =
                sharedPrefsInf.get(PREF_SAVED_SELECTED_SAVED_VIEW, Gson().toJson(setOf<String>()))
            Gson().fromJson(savedData, object : TypeToken<Set<String>>() {}.type)
        } catch (ex: Exception) {
            setOf()
        }
    }

    suspend fun updateSavedViewsInDb(data: SavedViewsListResponseData?) {
        if (data != null) {
            savedViewsDao.updateSavedView(data.savedViewId)
        }
    }


    fun isNonDX(data: SavedViewsListResponseData): Boolean {
        var checkIsNonDX = false
        if (data.savedViewSource == "XI") {
            if (!(data.dataSourceName.isNullOrBlank() || data.savedViewId == data.name)) {
                checkIsNonDX = true
            }
        }
        return checkIsNonDX
    }

    suspend fun checkSavedViewTableExist(): Boolean {
        return savedViewsDao.checkSavedViewTableExist() > 0
    }

    fun getSelectedSavedView(): LiveData<SavedViewsListResponseData> {
        return savedViewsDao.getSelectedSavedViewLiveData()
    }

    suspend fun getSelectedSavedViewFromDb(): SavedViewsListResponseData {
        return savedViewsDao.getSelectedSavedView()
    }

    fun getSavedViewsListFromDB(): LiveData<List<SavedViewsListResponseData>> {
        return savedViewsDao.getSavedViewList()
    }

    fun getDataSourceListFromDB(isAddToFavorite: Boolean): LiveData<List<SavedViewsListResponseData>> {
        return if (isAddToFavorite) {
            savedViewsDao.getSelectedSavedViewLiveDataList()
        } else {
            savedViewsDao.getDataSourceListLiveData()
        }
    }

    fun getActiveDataSource(): LiveData<SavedViewsListResponseData> {
        return savedViewsDao.getSelectedSavedViewLiveData()
    }


}