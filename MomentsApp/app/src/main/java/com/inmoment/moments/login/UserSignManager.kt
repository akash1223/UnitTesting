package com.inmoment.moments.login

import android.content.Context
import android.net.Uri
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.inmoment.moments.R
import com.inmoment.moments.framework.datamodel.RequestParam
import com.inmoment.moments.framework.datamodel.UserDataFromOAuth
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.database.MomentDB
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.ACCESS_TOKEN_PREFERENCE_KEY
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_AUTH_TOKEN_SESSION
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_LOGIN_TIME
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_STRING_DEFAULT
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_USER_EMAIL_ID
import com.inmoment.moments.login.model.AccessToken
import com.inmoment.moments.login.model.UserDetails
import kotlinx.coroutines.CoroutineScope
import net.openid.appauth.*
import okio.Buffer
import okio.buffer
import okio.source
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import javax.inject.Inject

class UserSignManager @Inject constructor(
    private val context: Context,
    private val restApiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf,
    private val momentDB: MomentDB
) : TaskManager() {

    fun getSignInConfig(userDetails: UserDetails, coroutineScope: CoroutineScope) = execute(
        userDetails,
        ::getSignInConfiguration,
        coroutineScope
    )

    fun getSignInAuthConfig(userDetails: UserDetails, coroutineScope: CoroutineScope) = execute(
        userDetails,
        ::getSignInAuthConfiguration,
        coroutineScope
    )

    fun getUserInfoFromOAuth(coroutineScope: CoroutineScope) = execute(
        ::getUserDataFromOAuth,
        coroutineScope
    )

    fun saveAccessTokenToSharedPref(accessToken: AccessToken, coroutineScope: CoroutineScope) =
        execute(
            accessToken,
            ::saveAccessTokenToSharedPreferences,
            coroutineScope
        )

    @Suppress("RedundantSuspendModifier")
    @VisibleForTesting
    suspend fun getSignInConfiguration(requestParam: RequestParam): OperationResult<UserSignConfigWrapper> {
        val op = OperationResult<UserSignConfigWrapper>()
        op.result = UserSignConfigWrapper(
            createTokenRequest(
                requestParam, getUserConfiguration(
                    context
                )
            )
        )
        return op
    }

    suspend fun deleteDatabase() {
        momentDB.clearAllTables()
    }

    @Suppress("RedundantSuspendModifier")
    @VisibleForTesting
    suspend fun getSignInAuthConfiguration(requestParam: RequestParam): OperationResult<UserSignConfigAuthRequestWrapper> {
        val op = OperationResult<UserSignConfigAuthRequestWrapper>()
        op.result = UserSignConfigAuthRequestWrapper(
            createAuthRequest(
                requestParam, getUserConfiguration(
                    context
                )
            )
        )
        return op
    }

    @Suppress("RedundantSuspendModifier")
    @VisibleForTesting
    suspend fun getUserDataFromOAuth(): OperationResult<UserDataFromOAuth> {
        return restApiHelper.getUserDataFromOAuth(getUserConfiguration(context).userInfoEndpointUri)
    }

    @Suppress("RedundantSuspendModifier")
    @VisibleForTesting
    suspend fun saveAccessTokenToSharedPreferences(requestParam: RequestParam): OperationResult<String> {
        val mAccessToken = requestParam as AccessToken
        sharedPrefsInf.put(
            ACCESS_TOKEN_PREFERENCE_KEY,
            mAccessToken.token
        )
        sharedPrefsInf.put(
            PREF_AUTH_TOKEN_SESSION,
            mAccessToken.accessTokenExpirationTime
        )
        sharedPrefsInf.put(
            PREF_LOGIN_TIME,
            mAccessToken.accessTokenExpirationTime
        )
        val op = OperationResult<String>()
        op.result = sharedPrefsInf.get(ACCESS_TOKEN_PREFERENCE_KEY, PREF_STRING_DEFAULT)
        return op
    }

    private fun createTokenRequest(
        requestParam: RequestParam,
        userSignConfiguration: UserSignConfiguration
    ): TokenRequest {

        val additionalParameter: MutableMap<String, String> =
            HashMap()
        val userDetails = requestParam as UserDetails
        additionalParameter["username"] = userDetails.firstParam
        additionalParameter["password"] = userDetails.secondParam
        val authorizationServiceConfiguration = AuthorizationServiceConfiguration(
            userSignConfiguration.getWebUri(userSignConfiguration.authorizationEndpointUri)!!,
            userSignConfiguration.getWebUri(userSignConfiguration.tokenEndpointUri)!!
        )

        return TokenRequest(
            authorizationServiceConfiguration,
            Objects.requireNonNull(userSignConfiguration.clientId).toString(),
            "",
            "password",
            userSignConfiguration.getWebUri(userSignConfiguration.redirectUri),
            userSignConfiguration.authorizationScope,
            "",
            "",
            "",
            additionalParameter
        )
    }

    fun getAuthResponse(response: TokenResponse): AuthorizationResponse {
        val mAuthorizationResponseBuilder = AuthorizationResponse.Builder(
            createAuthRequestForRefreshToken(
                getUserConfiguration(
                    context
                )
            ).build()
        )
            .setState(response.request.scope)
            .setAccessToken(response.accessToken)
            .setTokenType(AuthorizationResponse.TOKEN_TYPE_BEARER)
            .setIdToken(response.idToken)
            .setAccessTokenExpiresIn(response.accessTokenExpirationTime)
            .setScope(response.scope)

        return mAuthorizationResponseBuilder.build()
    }

    fun getLoginSessionDetails(): Pair<String, Long> {

        val email = sharedPrefsInf.get(PREF_USER_EMAIL_ID, PREF_STRING_DEFAULT)
        var loginSession = sharedPrefsInf.get(PREF_LOGIN_TIME, 0L)
        if (loginSession == 0L) {
            sharedPrefsInf.put(PREF_LOGIN_TIME, Calendar.getInstance().timeInMillis)
            loginSession = Calendar.getInstance().timeInMillis
        }
        return Pair(email, loginSession)
    }
}

private fun getUserConfiguration(context: Context): UserSignConfiguration {
    val configSource = context.resources.openRawResource(R.raw.auth_config).source().buffer()
    val configData = Buffer()
    configSource.readAll(configData)
    val jsonData = configData.readString(Charset.forName(StandardCharsets.UTF_8.name()))
    return Gson().fromJson(jsonData, UserSignConfiguration::class.java)
}

private fun createAuthRequest(
    requestParam: RequestParam,
    userSignConfiguration: UserSignConfiguration
): AuthorizationRequest.Builder {
    val userDetails = requestParam as UserDetails
    val additionalParams = HashMap<String, String>()
    additionalParams["acr_values"] = userDetails.firstParam
    additionalParams["acr_params"] = userDetails.secondParam
    val authorizationServiceConfiguration = AuthorizationServiceConfiguration(
        userSignConfiguration.getWebUri(userSignConfiguration.authorizationEndpointUri)!!,
        userSignConfiguration.getWebUri(userSignConfiguration.tokenEndpointUri)!!
    )
    return AuthorizationRequest.Builder(
        authorizationServiceConfiguration,
        userSignConfiguration.clientId,
        ResponseTypeValues.CODE,
        userSignConfiguration.getWebUri(userSignConfiguration.redirectUri)!!
    ).setScope(userSignConfiguration.authorizationScope).setAdditionalParameters(additionalParams)
}

private fun createAuthRequestForRefreshToken(
    userSignConfiguration: UserSignConfiguration
): AuthorizationRequest.Builder {

    val authorizationServiceConfiguration = AuthorizationServiceConfiguration(
        userSignConfiguration.getWebUri(userSignConfiguration.authorizationEndpointUri)!!,
        userSignConfiguration.getWebUri(userSignConfiguration.tokenEndpointUri)!!
    )
    return AuthorizationRequest.Builder(
        authorizationServiceConfiguration,
        userSignConfiguration.clientId,
        ResponseTypeValues.CODE,
        userSignConfiguration.getWebUri(userSignConfiguration.redirectUri)!!
    ).setScope(userSignConfiguration.authorizationScope).setAdditionalParameters(mapOf())
}

class UserSignConfigWrapper(
    val tokenRequest: TokenRequest
)

class UserSignConfigAuthRequestWrapper(
    val authorizationRequest: AuthorizationRequest.Builder
)

data class UserSignConfiguration(
    @SerializedName("authorization_endpoint_uri")
    val authorizationEndpointUri: String,
    @SerializedName("authorization_scope")
    val authorizationScope: String,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("discovery_uri")
    val discoveryUri: String?,
    @SerializedName("https_required")
    val httpsRequired: Boolean,
    @SerializedName("redirect_uri")
    val redirectUri: String,
    @SerializedName("registration_endpoint_uri")
    val registrationEndpointUri: String,
    @SerializedName("token_endpoint_uri")
    val tokenEndpointUri: String,
    @SerializedName("user_info_endpoint_uri")
    val userInfoEndpointUri: String
) {
    fun getWebUri(uri: String): Uri? {
        return Uri.parse(uri)
    }
}