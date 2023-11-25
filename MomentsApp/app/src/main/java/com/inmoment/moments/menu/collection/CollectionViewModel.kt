package com.inmoment.moments.menu.collection


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.datamodel.*
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.menu.saved_views.SavedViewsManager
import com.lysn.clinician.utility.extensions.forceRefresh
import com.lysn.clinician.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionDataManager: CollectionDataManager,
    private val savedViewsManager: SavedViewsManager
) :
    ViewModel() {


    val initViewModel = SingleLiveEvent<Boolean>()
    var isShowActionButton = MutableLiveData(false)
    var addToFavorite = MutableLiveData(false)
    var isDeleteEnable = MutableLiveData(false)

    var experienceId = AppConstants.EMPTY_VALUE
    var activeCollection: CollectionModel? = null
    private var _collectionList = MutableLiveData<MutableList<CollectionModel>>(mutableListOf())
    val collectionList: MutableLiveData<MutableList<CollectionModel>>
        get() = _collectionList

    private var collectionsResponseData: CollectionsResponseData? = null


    fun setCollectionData(
        collectionsResponseData: CollectionsResponseData
    ) {
        collectionsResponseData.data.collections?.let {
            _collectionList.value?.addAll(it)
        }
        activeCollection = _collectionList.value?.let {
            collectionDataManager.getStoreCollectionData(
                it
            )
        }
        this.collectionsResponseData = collectionsResponseData
    }

    init {
        initViewModel.value = true
    }

    fun addItemToCollectionList(item: CollectionModel) {
        _collectionList.value?.add(item)
        _collectionList.forceRefresh()
    }

    fun deleteItemFromCollectionList(item: CollectionModel) {
        _collectionList.value?.filter { it -> it.id == item.id }?.forEach {
            _collectionList.value!!.remove(it)
        }
        _collectionList.forceRefresh()
    }

    fun createCollection(collectionName: String): OperationResult<CollectionOperationResponseData> {
        return collectionDataManager.createCollection(
            CreateCollectionRequestParam(collectionName, listOf()),
            viewModelScope
        )
    }

    fun addToFavouriteCollection(
        collectionModel: CollectionModel?, collectionName: String?
    ): OperationResult<CollectionOperationResponseData> {

        return if (collectionModel != null) {
            collectionModel.records.add(experienceId)
            val collectionParam =
                CreateCollectionRequestParam(
                    collectionModel.label,
                    collectionModel.records,
                    collectionModel.id
                )
            collectionDataManager.updateCollection(collectionParam, viewModelScope)
        } else {
            val collectionParam = CreateCollectionRequestParam(
                collectionName!!,
                listOf(experienceId)
            )
            collectionDataManager.createCollection(collectionParam, viewModelScope)
        }
    }

    fun deleteCollection(collectionId: String): OperationResult<CollectionOperationResponseData> {
        return collectionDataManager.deleteCollection(
            collectionId,
            viewModelScope
        )
    }

    fun showActionButton(status: Boolean) {
        isShowActionButton.value = status
    }

    fun setDeleteAction(status: Boolean) {
        isDeleteEnable.value = status
    }

    fun setAddToFavorite(status: Boolean) {
        addToFavorite.value = status
    }

    fun getDataSourceList(): LiveData<List<SavedViewsListResponseData>> {
        return savedViewsManager.getDataSourceListFromDB(addToFavorite.value!!)
    }

    fun getActiveDataSource(): LiveData<SavedViewsListResponseData> {
        return savedViewsManager.getActiveDataSource()
    }

    fun getCollectionData(): CollectionsResponseData? {
        return collectionsResponseData
    }

    fun getCollectionInfo(
        dataSourceList: List<String>,
        myFavCollection: String
    ): OperationResult<CollectionsResponseData> {

        return collectionDataManager.getCollectionForAllDataSourceInfo(
            viewModelScope,
            dataSourceList,
            myFavCollection

        )
    }

    fun storeCollectionData(data: CollectionModel?): Boolean {
        return collectionDataManager.storeCollectionData(data)
    }

}
