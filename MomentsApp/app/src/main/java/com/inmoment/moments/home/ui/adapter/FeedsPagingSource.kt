package com.inmoment.moments.home.ui.adapter

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.inmoment.moments.framework.common.CAUGHT_UP
import com.inmoment.moments.framework.datamodel.HomeDashBoardRequestParam
import com.inmoment.moments.home.DashboardService
import com.inmoment.moments.home.model.Feed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class FeedsPagingSource constructor(
    private val dashboardService: DashboardService,
    var dataSourceType: MutableLiveData<String>,
    val feedDadaList: MutableLiveData<MutableList<Feed>>
) :
    PagingSource<Int, Feed>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val requestParam = HomeDashBoardRequestParam(
                currentLoadingPageKey,
                25
            )

            val response = coroutineScope {
                withContext(Dispatchers.Default) {
                    dashboardService.getAllMoments(requestParam)
                }
            }

            val responseData = mutableListOf<Feed>()
           /* responseData.addAll(response)
            feedDadaList.value?.addAll(response)*/
            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey.minus(1)
            val nextKey =
                if (responseData.isEmpty()) null else if (currentLoadingPageKey == 1 && responseData.size == 1) null else currentLoadingPageKey.plus(
                    1
                )

            if (!(currentLoadingPageKey == 1 && responseData.size == 1) && nextKey == null) {
                val listEndView = Feed()
                listEndView.rowType = CAUGHT_UP
                responseData.add(listEndView)
            }
            return LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}