package com.inmoment.moments.framework.manager.network

import android.content.Context
import com.inmoment.moments.framework.common.NetworkHelper
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject


/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

class NetworkConnectionInterceptor @Inject constructor(
    val context: Context, private val
    networkHelper: NetworkHelper
) : Interceptor {

    @Throws(NoConnectivityException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!networkHelper.isNetworkConnected()) {
            throw NoConnectivityException()
        }

        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

}

class NoConnectivityException : IOException() {

    override val message: String
        get() = "No Internet Connection"

}