package com.inmoment.moments.menu.collection


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.datamodel.CollectionsResponseData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddCollectionViewModel @Inject constructor(private val collectionDataManager: CollectionDataManager) :
    ViewModel() {

    var collectionName = MutableLiveData(AppConstants.EMPTY_VALUE)
    var enableSaveButton = MutableLiveData(true)

    var collectionsResponseData: CollectionsResponseData? = null

    fun setCollectionData(collectionsResponseData: CollectionsResponseData) {
        this.collectionsResponseData = collectionsResponseData
    }

    fun validateCollectionName(name: String) {
        if (name.isEmpty())
            enableSaveButton.value = true
        else
            enableSaveButton.value = collectionDataManager.validateCollectionName(
                collectionsResponseData?.data?.collections,
                name
            )
    }
}
