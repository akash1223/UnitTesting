package com.inmoment.moments.framework.manager.network

import android.content.Context
import androidx.annotation.MainThread
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.manager.HttpConstants
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.lysn.clinician.utility.extensions.justTry
import net.openid.appauth.*
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenInterceptor @Inject constructor(
    private val sharedPrefsInf: SharedPrefsInf,
    val context: Context,
    val baseUrl: String
) :
    Interceptor {
    val  TAG = this.javaClass.simpleName
    override fun intercept(chain: Interceptor.Chain): Response {

        var request: Request?
        if (isTokenExpired()) {
            refreshToken()
        }
        request = authTokenRequest(chain)
        return chain.proceed(request)

    }
    private fun isTokenExpired(): Boolean {
        var tokenExpired = false
        val tokenExpiration = sharedPrefsInf.get(
            SharedPrefsInf.PREF_AUTH_TOKEN_SESSION,
            0L
        )

        var authState = getAuthState()
        if (authState != null) {
            val current = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val tokenCalendarInstance = Calendar.getInstance()
            tokenCalendarInstance.timeInMillis = tokenExpiration
            Logger.d(TAG,"Token Expired Time"+SimpleDateFormat("dd-MM hh:mm a",Locale.US).format(tokenCalendarInstance.time))
            Logger.d(TAG,"Current Time"+SimpleDateFormat("dd-MM hh:mm a",Locale.US).format(current.time))

            if (tokenExpiration < current.timeInMillis) {
                tokenExpired = true
            }
        }
        return tokenExpired
    }


    private fun authTokenRequest(chain: Interceptor.Chain): Request {

        val requestBuilder = chain.request().newBuilder()
            .addHeader(
                HttpConstants.HEADER_PARAM_AUTHORIZATION,
                HttpConstants.BEARER + sharedPrefsInf.get(
                    SharedPrefsInf.ACCESS_TOKEN_PREFERENCE_KEY,
                    SharedPrefsInf.PREF_STRING_DEFAULT
                )
            )
        Logger.i(TAG,sharedPrefsInf.get(
            SharedPrefsInf.ACCESS_TOKEN_PREFERENCE_KEY,
            SharedPrefsInf.PREF_STRING_DEFAULT
        ))
        return requestBuilder.build()
    }

    private fun refreshToken() {

        justTry {

            val countDownLatch = CountDownLatch(1)
            val authStateJsonString = sharedPrefsInf.get(
                SharedPrefsInf.AUTH_STATE_PREFERENCE_KEY,
                SharedPrefsInf.PREF_STRING_DEFAULT
            )

            val authState = AuthState.jsonDeserialize(authStateJsonString)
            if (authState != null) {

                 performTokenRequest(
                    authState,
                    authState.createTokenRefreshRequest(),
                    countDownLatch
                )
            }

            countDownLatch.await(5, TimeUnit.SECONDS)

        }
    }

   /* private fun showError() {
        Logger.v("AuthTokenInterceptor", "Error")
        showAlertDialog(
            context, AlertParams(
                context.getString(R.string.session_expired),
                R.string.login_again_msg,
                context.getString(
                    R.string.ok
                )
            )
        ) {
            launchLoginActivity()
        }
    }
      private fun launchLoginActivity() {
        sharedPrefsInf.clearLoginDetails()
        val intent: Intent? = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }


    */

    private fun getAuthState(): AuthState? {
        return try {
            val authStateJsonString = sharedPrefsInf.get(
                SharedPrefsInf.AUTH_STATE_PREFERENCE_KEY,
                SharedPrefsInf.PREF_STRING_DEFAULT
            )
            AuthState.jsonDeserialize(authStateJsonString)
        } catch (ex: Exception) {
            null
        }
    }


    @MainThread
    private fun performTokenRequest(
        authState: AuthState,
        request: TokenRequest,
        countDownLatch: CountDownLatch
    ) {
        justTry {
            val clientAuthentication: ClientAuthentication =
                authState.clientAuthentication

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
                    authState.accessTokenExpirationTime?.let {
                        sharedPrefsInf.put(
                            SharedPrefsInf.PREF_AUTH_TOKEN_SESSION,
                            it
                        )
                    }
                } else {

                    sharedPrefsInf.clearLoginDetails()
                }
                countDownLatch.countDown()
            }
        }
    }

    private fun createAuthorizationService(): AuthorizationService {
        val builder: AppAuthConfiguration.Builder = AppAuthConfiguration.Builder()
        builder.setConnectionBuilder(DefaultConnectionBuilder.INSTANCE)
        return AuthorizationService(context, builder.build())
    }
}