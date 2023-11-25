package com.lysn.clinician.http
import com.lysn.clinician.utils.NetworkManager
import com.lysn.clinician.utils.NoInternetException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

@lombok.Generated
class NetworkConnectionInterceptor(private val networkManager: NetworkManager) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {

        if (!networkManager.isNetworkAvailable) {

            throw NoInternetException()
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())

    }
}