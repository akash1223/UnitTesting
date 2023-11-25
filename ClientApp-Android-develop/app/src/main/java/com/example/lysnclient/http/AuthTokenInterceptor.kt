package com.example.lysnclient.http

import android.content.Context
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.HttpConstants
import com.example.lysnclient.utils.PreferenceUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 *  This class use for add header in each rest Api
 */
class AuthTokenInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val isFromLogoutAPI =
            chain.request().url().uri().toString()
                .endsWith(HttpConstants.METHOD_POST_LOGOUT)
        if (PreferenceUtil.getInstance(context)
                .getValue(PreferenceUtil.KEY_IS_USER_LOGGED_IN, false)
            && !isFromLogoutAPI
        ) {
            val requestBuilder = chain.request().newBuilder()
                .addHeader(
                    HttpConstants.HEADER_PARAM_AUTHORIZATION,
                    HttpConstants.BEARER + PreferenceUtil.getInstance(context)
                        .getValue(PreferenceUtil.KEY_ACCESS_TOKEN, AppConstants.EMPTY_VALUE)
                )
            val request = requestBuilder.build()
            val response = chain.proceed(request)
            return response
        } else {
            val request = chain.request().newBuilder().build()
            val response = chain.proceed(request)
            return response
        }
    }
}