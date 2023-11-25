package com.lysn.clinician.repository

import com.lysn.clinician.http.HttpConstants
import com.lysn.clinician.http.Resource
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.NoInternetException
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

/**
 * This class handles API calls and parse json data according to response status code
 */
open class BaseRepository(
    private val localizeProvider: LocalizeTextProvider
) {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {

            val response = call()
            return if (response.code() == HttpConstants.STATUS_CODE_OK) {
                val body = response.body()
                if (body != null)
                    Resource.success(body)
                else
                    Resource.empty(response.message())
            } else if (response.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401) {
                Resource.error<T>(parseAuthError(response.errorBody()))
            } else if (response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400) {
                Resource.error<T>(parseBadRequest(response.errorBody()))
            }else if (response.code() == HttpConstants.STATUS_CODE_RESET_CONTENT_205) {
                Resource.empty<T>(localizeProvider.getLogoutUserMessage())
            }
            else {
                Resource.error<T>(parseAuthError(response.errorBody()))
            }
        } catch (e: Exception) {
            return if (e is NoInternetException)
                Resource.noInternet<T>(localizeProvider.getNoInternetMessage())
            else
                Resource.error<T>(localizeProvider.getSomethingWrongMessage())
        }
    }


    private fun parseAuthError(response: ResponseBody?): String {
        var errorMessage = localizeProvider.getSomethingWrongMessage()
        response?.let {
            val jsonObj = JSONObject(it.charStream().readText())
            try {
                if (jsonObj.has("detail")) {
                    errorMessage = jsonObj.optString("detail")
                }
            } catch (e: Exception) {
                Timber.d("JSON Parsing error ")
            }
        }
        return errorMessage.trim()
    }


    private fun parseBadRequest(response: ResponseBody?): String {
        var errorMessage = localizeProvider.getSomethingWrongMessage()
        response?.let {
            val jsonObj = JSONObject(it.charStream().readText())
            val keys: Iterator<String> = jsonObj.keys()
            val errorList = ArrayList<String>()
            try {
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
                Timber.d("JSON Parsing error ")
            }
        }
        return errorMessage.trim()
    }
}


