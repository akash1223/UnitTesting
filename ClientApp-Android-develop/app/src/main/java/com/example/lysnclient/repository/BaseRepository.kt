package com.example.lysnclient.repository

import androidx.lifecycle.MutableLiveData
import com.example.lysnclient.http.NoInternetException
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.model.*
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.HttpConstants
import com.example.lysnclient.utils.LocalizeTextProvider
import com.google.gson.JsonParseException
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.ArrayList

open class BaseRepository(
    private val localizeProvider: LocalizeTextProvider
) {
    var listOfAssessmentType: List<AssessmentType> = ArrayList()
    var mWBTQuestionList: List<WBTQuestion> = ArrayList()
    var mWBTOutputObservation: WBTOutputObservation? = null

    fun getWBTInterpretation(): List<String> {
        return mWBTOutputObservation?.insightsMessages ?: ArrayList()
    }

    fun getWBTQuestionList(): List<WBTQuestion> {
        return mWBTQuestionList
    }

    fun getAssessmentById(assessmentId: Int): AssessmentType? {
        for (item in listOfAssessmentType) {
            if (item.id == assessmentId) return item
        }
        return null
    }

    fun getAssessmentQuestionsById(assessmentId: Int): ArrayList<AssessmentQuestion> {
        for (item in listOfAssessmentType) {
            if (item.id == assessmentId) return item.listOfQuestions
        }
        return ArrayList()
    }

    open fun <T> executeAPI(callback: Call<T>): MutableLiveData<BaseResponse<T>> {
        val apiResponse = MutableLiveData<BaseResponse<T>>()
        callback.enqueue(object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>
            ) {
                apiResponse.value = parseSuccessResponse(response)
                closeResponse(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                apiResponse.value = parseFailureResponse(t)
            }
        })
        return apiResponse
    }

    /* This method used for parsing error response body, and get list of error message
   */
    private fun parseErrorBody(response: ResponseBody?): String {
        var errorMessage = localizeProvider.getSomethingWrongMessage()
        response?.let {
            try {
                val data = it.charStream().readText()
                val jsonObj = JSONObject(data)
                val keys: Iterator<String> = jsonObj.keys()
                val errorList = ArrayList<String>()
                while (keys.hasNext()) {
                    val jsonArrayOfError: JSONArray? =
                        jsonObj.optJSONArray(keys.next())
                    jsonArrayOfError?.let {
                        errorMessage = AppConstants.EMPTY_VALUE
                        for (i in 0 until jsonArrayOfError.length()) {
                            errorList.add(jsonArrayOfError[i].toString())
                        }
                    }
                    for (item in errorList) {
                        errorMessage = errorMessage + "\n" + item
                    }
                    break
                }
            } catch (e: Exception) {
                Timber.d("Json Parsing error ")
            }
        }
        return errorMessage.trim()
    }

    fun <T> parseSuccessResponse(response: Response<T>): BaseResponse<T> {
        return when (response.code()) {
            HttpConstants.STATUS_CODE_OK_200 -> {
                if (response.raw().request().url().toString()
                        .endsWith(HttpConstants.METHOD_GET_ASSESSMENTS_LIST)
                ) {
                    val data: List<AssessmentType>? = response.body() as List<AssessmentType>
                    listOfAssessmentType = data ?: ArrayList()
                } else if (response.raw().request().url().toString()
                        .endsWith(HttpConstants.METHOD_GET_WBT_QUESTIONS)
                ) {
                    val data: ConfigurationData? = response.body() as ConfigurationData
                    mWBTQuestionList =
                        data?.wellBeingTrackerData?.mWBTQuestionList ?: ArrayList()
                } else if (response.raw().request().url().toString()
                        .endsWith(HttpConstants.METHOD_GET_WBT_OUTPUT_SCREEN_LIST_TEMP)
                ) {
                    val data: WBTOutputScreenResponse? = response.body() as WBTOutputScreenResponse
                    mWBTOutputObservation = data?.mWBTOutputObservation
                }

                BaseResponse(
                    ResponseStatus.SUCCESS,
                    AppConstants.EMPTY_VALUE,
                    response.body()
                )

            }
            HttpConstants.STATUS_CODE_201 -> {
                BaseResponse(
                    ResponseStatus.SUCCESS_201,
                    AppConstants.EMPTY_VALUE,
                    response.body()
                )
            }
            HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400 -> {
                BaseResponse(
                    ResponseStatus.BAD_PARAMS,
                    parseErrorBody(response.errorBody())
                )
            }
            HttpConstants.STATUS_CODE_205 -> {
                BaseResponse(
                    ResponseStatus.LOGOUT,
                    AppConstants.EMPTY_VALUE,
                    response.body()
                )
            }
            HttpConstants.STATUS_CODE_UNAUTHORIZED_401 -> {
                var errorMessage = localizeProvider.getLogoutUserMessage()

                if (response.raw().request().url().toString()
                        .endsWith(HttpConstants.METHOD_POST_LOGIN)
                ) {
                    errorMessage = parseErrorResObject(response.errorBody())
                }
                BaseResponse(
                    ResponseStatus.UNAUTHORIZED_TOKEN_EXPIRED, errorMessage.trim()
                )
            }
            HttpConstants.STATUS_CODE_CONFLICT_INPUT_409 -> {
                var errorMessage = AppConstants.EMPTY_VALUE
                if (response.raw().request().url().toString()
                        .endsWith(HttpConstants.METHOD_POST_REQUEST_FOR_OTP)
                ) {
                    errorMessage = parseErrorBody(response.errorBody())
                } else if (response.raw().request().url().toString()
                        .endsWith(HttpConstants.METHOD_POST_REGISTER)
                ) {
                    errorMessage = parseErrorBody(response.errorBody())
                }
                BaseResponse(
                    ResponseStatus.CONFLICT_USER_INPUTS,
                    errorMessage.trim()
                )
            }
            HttpConstants.STATUS_CODE_500 -> {
                BaseResponse(
                    ResponseStatus.BAD_INPUT_500,
                    localizeProvider.getSomethingWrongMessage(),
                    response.body()
                )
            }
            else -> {
                BaseResponse(
                    ResponseStatus.FAILURE,
                    localizeProvider.getSomethingWrongMessage(),
                    response.body()
                )
            }
        }
    }

    fun <T> parseFailureResponse(t: Throwable): BaseResponse<T> {
        return when (t) {
            is NoInternetException -> {
                BaseResponse(
                    ResponseStatus.NO_INTERNET,
                    AppConstants.EMPTY_VALUE
                )
            }
            else -> {
                BaseResponse(
                    ResponseStatus.FAILURE,
                    localizeProvider.getServerNotReachableMessage()
                )
            }
        }
    }

    private fun <T> closeResponse(response: Response<T>) {
        if (response.code() != 200) {
            response.errorBody()?.close()
        }
    }

    private fun parseErrorResObject(response: ResponseBody?): String {
        var errorMessage = localizeProvider.getSomethingWrongMessage()
        response?.let {
            try {
                val jsonObj = JSONObject(it.charStream().readText())
                val keys: Iterator<String> = jsonObj.keys()
                while (keys.hasNext()) {
                    errorMessage = jsonObj.getString(keys.next())
                    break
                }
            } catch (e: JsonParseException) {
                Timber.d("Json Parsing error ")
            }
        }
        return errorMessage.trim()
    }
}
