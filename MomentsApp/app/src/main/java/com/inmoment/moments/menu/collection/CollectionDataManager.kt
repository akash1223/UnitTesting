package com.inmoment.moments.menu.collection

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.inmoment.moments.framework.datamodel.*
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.manager.network.RestApiInterfaceDao
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_DEFAULT_DATA_SOURCE_ID
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_STRING_DEFAULT
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_USER_PROGRAM_ID
import com.inmoment.moments.home.MomentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await
import javax.inject.Inject

@Suppress("RedundantSuspendModifier")
class CollectionDataManager @Inject constructor(
    private val apiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf,
    private val restApiInterfaceDao: RestApiInterfaceDao
) : TaskManager() {

    fun getCollection(coroutineScope: CoroutineScope) = execute(::getCollectionInfo, coroutineScope)

    fun createCollection(
        collectionParam: CreateCollectionRequestParam,
        coroutineScope: CoroutineScope
    ) =
        execute(collectionParam, ::createCollectionInfo, coroutineScope)

    fun updateCollection(
        collectionParam: CreateCollectionRequestParam,
        coroutineScope: CoroutineScope
    ) =
        execute(collectionParam, ::updateCollectionInfo, coroutineScope)

    fun deleteCollection(collectionId: String, coroutineScope: CoroutineScope) =
        execute(SingleParamRequest(collectionId), ::deleteCollectionInfo, coroutineScope)

    @VisibleForTesting
    suspend fun getCollectionInfo(): OperationResult<CollectionsResponseData> {

        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        val userProgramId = sharedPrefsInf.get(PREF_USER_PROGRAM_ID, PREF_STRING_DEFAULT)
        val dataSourceId = sharedPrefsInf.get(PREF_DEFAULT_DATA_SOURCE_ID, PREF_STRING_DEFAULT)

        val graphqlVariables =
            "{\"where\":{\"datasourceId\":\"${dataSourceId}\",\"userProgramIds\":[\"${userProgramId}\"]}}"
        val graphqlQuery =
            "query getCollections(\$where: CollectionSearchInput!) { collections(where: \$where) { id  label  sortOrder records datasource{name} collectionType createdAt updatedAt __typename}}"

        return apiHelper.getAllCollections(
            xiContextHeader,
            CollectionsRequestData("getCollections", graphqlVariables, graphqlQuery)
        )
    }

    @VisibleForTesting
    fun getCollectionForAllDataSourceInfo(
        coroutineScope: CoroutineScope,
        dataSourceList: List<String>,
        myFavCollection: String
    ): OperationResult<CollectionsResponseData> {
        val opAggregator =
            OperationResult<CollectionsResponseData>(MutableLiveData(), MutableLiveData())
        val operationResult = OperationResult<CollectionsResponseData>()
        try {
            val xiContextHeader = sharedPrefsInf.getXiContextHeader()
            val userProgramId = sharedPrefsInf.get(PREF_USER_PROGRAM_ID, PREF_STRING_DEFAULT)
            //  val dataSourceId = sharedPrefsInf.get(PREF_DEFAULT_DATA_SOURCE_ID, PREF_STRING_DEFAULT)


            val graphqlQuery =
                "query getCollections(\$where: CollectionSearchInput!) { collections(where: \$where) { id  label  sortOrder records datasource{name} collectionType createdAt updatedAt __typename}}"


            coroutineScope.launch(Dispatchers.IO) {
                val deferredList: MutableList<CollectionsResponseData> = mutableListOf()
                dataSourceList.forEachIndexed { index, id ->

                    val graphqlVariables =
                        "{\"where\":{\"datasourceId\":\"${id}\",\"userProgramIds\":[\"${userProgramId}\"]}}"
                    val deferredApiCall = restApiInterfaceDao.getCollections(
                        xiContextHeader,
                        CollectionsRequestData("getCollections", graphqlVariables, graphqlQuery)
                    )
                    deferredList.add(deferredApiCall.await())
                }

                val getFirstCollectionObject = CollectionsResponseData(
                    CollectionsResponseData.Data(
                        mutableListOf()
                    )
                )
                deferredList.forEach {
                    it.data.collections?.let { it1 ->
                        getFirstCollectionObject.data.collections?.addAll(
                            sortingAsPerDataSource(it1, myFavCollection)
                        )
                    }
                }
                operationResult.result = getFirstCollectionObject
                setResult(opAggregator, operationResult)
            }

        } catch (ex: Exception) {
            setResult(opAggregator, operationResult)
        }
        return opAggregator
    }

    private fun sortingAsPerDataSource(
        collectionList: MutableList<CollectionModel>,
        myFavCollection: String
    ): Collection<CollectionModel> {
        collectionList.sortBy { it.label.toLowerCase() }
        val myFav: CollectionModel? = collectionList.find { it1 -> it1.label == myFavCollection }
        if (myFav != null) {
            collectionList.remove(myFav)
            collectionList.add(0, myFav)
        }

        return collectionList
    }

    @VisibleForTesting
    suspend fun createCollectionInfo(requestParam: RequestParam): OperationResult<CollectionOperationResponseData> {

        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        val param = (requestParam as CreateCollectionRequestParam)

        val userProgramId = sharedPrefsInf.get(PREF_USER_PROGRAM_ID, PREF_STRING_DEFAULT)
        val dataSourceId = sharedPrefsInf.get(PREF_DEFAULT_DATA_SOURCE_ID, PREF_STRING_DEFAULT)

        val graphqlQuery =
            "mutation createCollection(\$data: CreateCollectionInput!) {createCollection(data: \$data) { id label sortOrder records datasource{name} collectionType createdAt updatedAt __typename}}"

        val graphqlVariables = if (param.record.isNotEmpty())
            """
            {"data":{"label":"${param.name}","sortOrder":"updatedAt_DESC", "records":${
                Gson().toJson(
                    param.record
                )
            },"userProgramIds":["$userProgramId"],"datasourceId":"$dataSourceId","collectionType":"PRIVATE"}}
        """.trimIndent()
        else
            """
            {"data":{"label":"${param.name}","sortOrder":"updatedAt_DESC","records":[],"userProgramIds":["$userProgramId"],"datasourceId":"$dataSourceId","collectionType":"PRIVATE"}}
        """.trimIndent()

        val collectionRequest =
            CollectionsRequestData("createCollection", graphqlVariables, graphqlQuery)

        val requestData = Gson().toJson(collectionRequest)
        return apiHelper.createCollections(xiContextHeader, collectionRequest)
    }

    @VisibleForTesting
    suspend fun updateCollectionInfo(requestParam: RequestParam): OperationResult<CollectionOperationResponseData> {

        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        val param = (requestParam as CreateCollectionRequestParam)

        val graphqlQuery =
            "mutation createCollection(\$where: IdInput!, \$data: UpdateCollectionInput!) {updateCollection(where: \$where, data: \$data) { id label sortOrder records datasource{name} collectionType createdAt updatedAt __typename}}"

        val graphqlVariables = """
          {"where":{"id":"${param.id}"},"data":{"label":"${param.name}","records":${
            Gson().toJson(
                param.record
            )
        }}}
                  """.trimIndent()

        return apiHelper.createCollections(
            xiContextHeader,
            CollectionsRequestData("createCollection", graphqlVariables, graphqlQuery)
        )
    }

    @VisibleForTesting
    suspend fun deleteCollectionInfo(requestParam: RequestParam): OperationResult<CollectionOperationResponseData> {

        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        val collectionId = (requestParam as SingleParamRequest<*>).value

        val graphqlQuery = """
            mutation {deleteCollection(where: {id: "$collectionId"}) {id}}
        """.trimIndent()

        return apiHelper.deleteCollections(
            xiContextHeader,
            DeleteCollectionsRequestData(graphqlQuery)
        )
    }

    fun storeCollectionData(data: CollectionModel?): Boolean {
        // return if(data.id != sharedPrefsInf.get(SharedPrefsInf.PREF_DEFAULT_COLLECTION_ID,SharedPrefsInf.PREF_STRING_DEFAULT)) {

        data?.let {
            sharedPrefsInf.put(
                SharedPrefsInf.PREF_DEFAULT_COLLECTION_ID,
                data.id
            )
            sharedPrefsInf.put(SharedPrefsInf.PREF_DEFAULT_COLLECTION_NAME, data.label)
            sharedPrefsInf.put(SharedPrefsInf.PREF_MOMENT_TYPE, MomentType.COLLECTION.value)
        } ?: kotlin.run {
            sharedPrefsInf.put(
                SharedPrefsInf.PREF_DEFAULT_COLLECTION_ID, PREF_STRING_DEFAULT
            )
            sharedPrefsInf.put(SharedPrefsInf.PREF_DEFAULT_COLLECTION_NAME, PREF_STRING_DEFAULT)
            sharedPrefsInf.put(SharedPrefsInf.PREF_MOMENT_TYPE, MomentType.SAVED_VIEWS.value)
        }
        /*  true
      } else {
          false
      }*/
        return true
    }

    fun validateCollectionName(collections: List<CollectionModel>?, name: String): Boolean {
        return collections?.any { it.label.equals(name, true) } ?: false
    }

    fun getStoreCollectionData(listData: List<CollectionModel>): CollectionModel? {
        val collectionId = sharedPrefsInf.get(
            SharedPrefsInf.PREF_DEFAULT_COLLECTION_ID,
            PREF_STRING_DEFAULT
        )
        return listData.find { it.id == collectionId }
    }

    fun getMomentType(): String {
        return sharedPrefsInf.get(
            SharedPrefsInf.PREF_MOMENT_TYPE,
            MomentType.SAVED_VIEWS.value
        )
    }

}