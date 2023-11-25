package com.inmoment.moments.framework.manager.network

import android.content.Context
import android.content.Intent
import androidx.annotation.MainThread
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.manager.HttpConstants
import com.inmoment.moments.framework.persist.SharedPrefsInf
import net.openid.appauth.*
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpAuthTokenInterceptor @Inject constructor(
    private val sharedPrefsInf: SharedPrefsInf,
    val context: Context,
    val baseUrl: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()


        var countDownLatch = CountDownLatch(1)
        val authStateJsonString = sharedPrefsInf.get(
            SharedPrefsInf.AUTH_STATE_PREFERENCE_KEY,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )

        var authState: AuthState? = null
        authState = AuthState.jsonDeserialize(authStateJsonString);
        if (authState != null) {

            performTokenRequest(
                authState,
                authState.createTokenRefreshRequest(),
                countDownLatch
            )
        }
        val token = authState.lastTokenResponse
        if (authState.isAuthorized)
        // Thread.sleep(2000)
            countDownLatch.await(5, TimeUnit.SECONDS)
        return if (sharedPrefsInf.isUserLogin()) {
            val requestBuilder = chain.request().newBuilder()
                .addHeader(
                    HttpConstants.HEADER_PARAM_AUTHORIZATION,
                    HttpConstants.BEARER + sharedPrefsInf.get(
                        SharedPrefsInf.ACCESS_TOKEN_PREFERENCE_KEY,
                        ""
                    )
                )
            val request = requestBuilder.build()
            Logger.d("token", sharedPrefsInf.get(SharedPrefsInf.ACCESS_TOKEN_PREFERENCE_KEY, ""))
            chain.proceed(request)
        } else {
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
    }


    private fun launchLoginActivity() {
        sharedPrefsInf.clearLoginDetails()
        val intent: Intent? = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    @MainThread
    private fun performTokenRequest(
        authState: AuthState,
        request: TokenRequest,
        countDownLatch: CountDownLatch
    ) {
        val clientAuthentication: ClientAuthentication = try {
            authState.clientAuthentication
        } catch (ex: ClientAuthentication.UnsupportedAuthenticationMethod) {
            return
        }
        createAuthorizationService().performTokenRequest(
            request,
            clientAuthentication,

            )
        { response, ex ->
            if (response != null) {
                authState.update(response, ex)
                authState.needsTokenRefresh = true
                val authJsonString = authState.jsonSerializeString()
                sharedPrefsInf.put(SharedPrefsInf.AUTH_STATE_PREFERENCE_KEY, authJsonString)
                sharedPrefsInf.put(
                    SharedPrefsInf.ACCESS_TOKEN_PREFERENCE_KEY,
                    response.accessToken!!
                )
            } else {
                sharedPrefsInf.clearLoginDetails()
            }
            countDownLatch.countDown()
        }
    }

    private fun createAuthorizationService(): AuthorizationService {
        val builder: AppAuthConfiguration.Builder = AppAuthConfiguration.Builder()
        builder.setConnectionBuilder(DefaultConnectionBuilder.INSTANCE)
        return AuthorizationService(context, builder.build())
    }
}