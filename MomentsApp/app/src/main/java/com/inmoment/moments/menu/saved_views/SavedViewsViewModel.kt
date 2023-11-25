package com.inmoment.moments.menu.saved_views


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmoment.moments.framework.datamodel.SavedViewsListResponseData
import com.inmoment.moments.framework.dto.OperationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewsViewModel @Inject constructor(private val savedViewsManager: SavedViewsManager) :
    ViewModel() {


    val visitedSavedViewList = mutableSetOf<String>()

    init {
        visitedSavedViewList.addAll(savedViewsManager.getVisitedSavedViewList())
    }

    fun getSavedViewsInfo(): OperationResult<List<SavedViewsListResponseData>> {
        return savedViewsManager.getSavedViews(viewModelScope)
    }

    fun storeSelectedSavedViewsData(data: List<SavedViewsListResponseData>) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedSavedViewData = savedViewsManager.getSelectedSavedViewFromDb()
            val cloneSelectedData: SavedViewsListResponseData? =
                data.find { it1 -> it1.savedViewId == selectedSavedViewData.savedViewId }
            cloneSelectedData?.activeSavedView = true
            savedViewsManager.saveSavedViewsInDb(data)
        }
    }

    fun getSavedViewsFromDB(): LiveData<List<SavedViewsListResponseData>> {
        return savedViewsManager.getSavedViewsListFromDB()
    }

    fun storeSavedViewsData(data: SavedViewsListResponseData): Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            savedViewsManager.updateSavedViewsInDb(data)
        }
        return savedViewsManager.storeSavedViewsDada(data)
    }


}
