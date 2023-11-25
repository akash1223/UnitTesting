package com.lysn.clinician.http

import com.lysn.clinician.utils.PreferenceUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 *  This class use for add header in each rest Api
 */
@lombok.Generated
class AuthTokenInterceptor(private val preferenceUtil: PreferenceUtil) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        return if (preferenceUtil.isUserLogin()) {
            val requestBuilder = chain.request().newBuilder()
                .addHeader(
                    HttpConstants.HEADER_PARAM_AUTHORIZATION,
                    HttpConstants.BEARER + preferenceUtil
                        .getValue(PreferenceUtil.ACCESS_TOKEN_PREFERENCE_KEY, "")
                )
            val request = requestBuilder.build()
            chain.proceed(request)
        } else {
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
    }
}