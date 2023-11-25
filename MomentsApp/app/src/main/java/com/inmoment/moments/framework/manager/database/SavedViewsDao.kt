package com.inmoment.moments.framework.manager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inmoment.moments.framework.datamodel.SavedViewsListResponseData


@Dao
interface SavedViewsDao {

    /* @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertSelectedSavedView(savedViewsListResponseData: SavedViewsListResponseData)

     @Query("delete from SavedViews")
     suspend fun removeSavedSelectedSavedView()

     @Query("SELECT * FROM SavedViews")
     fun getSavedSelectedSavedView(): LiveData<SavedViewsListResponseData>

 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedView(savedViewList: List<SavedViewsListResponseData>)

    @Query("delete from SavedViews")
    suspend fun removeSavedSelectedSavedView()

    @Query("SELECT * FROM SavedViews")
    fun getSavedViewList(): LiveData<List<SavedViewsListResponseData>>

    @Query("SELECT * FROM SavedViews WHERE activeSavedView = 1")
    fun getSelectedSavedViewLiveData(): LiveData<SavedViewsListResponseData>

    @Query("SELECT * FROM SavedViews WHERE activeSavedView = 1")
    fun getSelectedSavedViewLiveDataList(): LiveData<List<SavedViewsListResponseData>>

    @Query("SELECT * FROM SavedViews WHERE activeSavedView = 1")
    suspend fun getSelectedSavedView(): SavedViewsListResponseData

    @Query("SELECT * FROM SavedViews  where dataSourceName<>'NULL' group by dataSourceId order by savedViewId desc")
    fun getDataSourceListLiveData(): LiveData<List<SavedViewsListResponseData>>

    @Query("SELECT COUNT(*) FROM SavedViews")
    suspend fun checkSavedViewTableExist(): Int

    @Query("UPDATE SavedViews set activeSavedView = case when savedViewId=:id then 1 else 0 end")
    suspend fun updateSavedView(id: String)

}