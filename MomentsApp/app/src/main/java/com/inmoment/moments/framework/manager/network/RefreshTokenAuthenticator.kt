package com.inmoment.moments.framework.manager.network


import android.content.Context
import android.content.Intent
import androidx.annotation.MainThread
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.manager.HttpConstants
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.ACCESS_TOKEN_PREFERENCE_KEY
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_STRING_DEFAULT
import com.lysn.clinician.utility.extensions.justTry
import net.openid.appauth.*
import net.openid.appauth.ClientAuthentication.UnsupportedAuthenticationMethod
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RefreshTokenAuthenticator @Inject constructor(
    private val sharedPrefsInf: SharedPrefsInf,
    val context: Context,
    val baseUrl: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        justTry {

            if(response.code == 401 || response.code ==500) {
                var countDownLatch = CountDownLatch(1)
                val authStateJsonString = sharedPrefsInf.get(
                    SharedPrefsInf.AUTH_STATE_PREFERENCE_KEY,
                    PREF_STRING_DEFAULT
                )

                try {
                    val authState = AuthState.jsonDeserialize(authStateJsonString);
                    if (authState != null) {

                        performTokenRequest(
                            authState,
                            authState.createTokenRefreshRequest(),
                            countDownLatch
                        )
                    }
                }
                catch (ex:Exception)
                {
                    launchLoginActivity()
                }
                // Thread.sleep(2000)
                countDownLatch.await(5, TimeUnit.SECONDS)

                val accessToken = sharedPrefsInf.get(ACCESS_TOKEN_PREFERENCE_KEY,
                    PREF_STRING_DEFAULT)
                Logger.d("toke", accessToken)
                if (accessToken.isNotEmpty()) {
                    val request= response.request.newBuilder()
                        .header(
                            HttpConstants.HEADER_PARAM_AUTHORIZATION,
                            HttpConstants.BEARER + accessToken
                        )
                        .build()
                  return  chain.proceed(request)
                }
                else
                {
                    launchLoginActivity()
                }
            }
        }
        return response
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
        } catch (ex: UnsupportedAuthenticationMethod) {
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
                    ACCESS_TOKEN_PREFERENCE_KEY,
                    response?.accessToken!!
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