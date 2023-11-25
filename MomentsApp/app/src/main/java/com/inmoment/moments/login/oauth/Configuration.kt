package com.inmoment.moments.login.oauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.Nullable
import com.inmoment.moments.R
import net.openid.appauth.connectivity.ConnectionBuilder
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import okio.Buffer
import okio.IOException
import okio.buffer
import okio.source
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset


class Configuration {

    private var mConfigJson: JSONObject? = null
    private var mConfigHash: String? = null
    private var mClientSecret: String? = null
    private var mClientId: String? = null
    private var mScope: String? = null
    private var mRedirectUri: Uri? = null
    private var mDiscoveryUri: Uri? = null
    private var mAuthEndpointUri: Uri? = null
    private var mTokenEndpointUri: Uri? = null
    private var mRegistrationEndpointUri: Uri? = null
    private var mUserInfoEndpointUri: Uri? = null
    private var mHttpsRequired = false

    @Nullable
    fun getClientId(): String? {
        return mClientId
    }

    fun getScope(): String {
        return mScope!!
    }

    fun getRedirectUri(): Uri? {
        return mRedirectUri
    }

    @Nullable
    fun getAuthEndpointUri(): Uri? {
        return mAuthEndpointUri
    }

    @Nullable
    fun getTokenEndpointUri(): Uri? {
        return mTokenEndpointUri
    }

    fun getConnectionBuilder(): ConnectionBuilder? {
        return DefaultConnectionBuilder.INSTANCE
    }

    @Throws(InvalidConfigurationException::class)
    fun readConfiguration(mContext: Context) {
        val configSource =
            mContext.resources.openRawResource(R.raw.auth_config).source().buffer()
        val configData = Buffer()
        try {
            configSource.readAll(configData)
            mConfigJson = JSONObject(configData.readString(Charset.forName("UTF-8")))
        } catch (ex: IOException) {
            throw InvalidConfigurationException(
                "Failed to read configuration: " + ex.message
            )
        } catch (ex: JSONException) {
            throw InvalidConfigurationException(
                "Unable to parse configuration: " + ex.message
            )
        }
        mConfigHash = configData.sha256().base64()
        mClientId = getConfigString("client_id")
        mScope = getRequiredConfigString("authorization_scope")
        mRedirectUri = getRequiredConfigUri("redirect_uri")
        mClientSecret = getConfigString("client_secret")
        if (!isRedirectUriRegistered(mContext)) {
            throw InvalidConfigurationException(
                "redirect_uri is not handled by any activity in this app! "
                        + "Ensure that the appAuthRedirectScheme in your build.gradle file "
                        + "is correctly configured, or that an appropriate intent filter "
                        + "exists in your app manifest."
            )
        }
        if (getConfigString("discovery_uri") == null) {
            mAuthEndpointUri = getRequiredConfigWebUri("authorization_endpoint_uri")
            mTokenEndpointUri = getRequiredConfigWebUri("token_endpoint_uri")
            mUserInfoEndpointUri = getRequiredConfigWebUri("user_info_endpoint_uri")
            if (mClientId == null) {
                mRegistrationEndpointUri = getRequiredConfigWebUri("registration_endpoint_uri")
            }
        } else {
            mDiscoveryUri = getRequiredConfigWebUri("discovery_uri")
            mAuthEndpointUri = getRequiredConfigWebUri("authorization_endpoint_uri")
            mTokenEndpointUri = getRequiredConfigWebUri("token_endpoint_uri")
        }
        mHttpsRequired = mConfigJson!!.optBoolean("https_required", true)
    }

    @Nullable
    fun getConfigString(propName: String?): String? {
        var value = mConfigJson!!.optString(propName) ?: return null
        value = value.trim { it <= ' ' }
        return if (TextUtils.isEmpty(value)) {
            null
        } else value
    }

    @Throws(InvalidConfigurationException::class)
    private fun getRequiredConfigString(propName: String): String {
        return getConfigString(propName)!!
    }

    @Throws(InvalidConfigurationException::class)
    fun getRequiredConfigUri(propName: String): Uri {
        val uriStr = getRequiredConfigString(propName)
        val uri: Uri
        uri = try {
            Uri.parse(uriStr)
        } catch (ex: Throwable) {
            throw InvalidConfigurationException("$propName could not be parsed", ex)
        }
        if (!uri.isHierarchical || !uri.isAbsolute) {
            throw InvalidConfigurationException(
                "$propName must be hierarchical and absolute"
            )
        }
        if (!TextUtils.isEmpty(uri.encodedUserInfo)) {
            throw InvalidConfigurationException("$propName must not have user info")
        }
        if (!TextUtils.isEmpty(uri.encodedQuery)) {
            throw InvalidConfigurationException("$propName must not have query parameters")
        }
        if (!TextUtils.isEmpty(uri.encodedFragment)) {
            throw InvalidConfigurationException("$propName must not have a fragment")
        }
        return uri
    }

    @Throws(InvalidConfigurationException::class)
    fun getRequiredConfigWebUri(propName: String): Uri? {
        val uri: Uri = getRequiredConfigUri(propName)
        val scheme: String? = uri.scheme
        if (TextUtils.isEmpty(scheme) || !("http" == scheme || "https" == scheme)) {
            throw InvalidConfigurationException(
                "$propName must have an http or https scheme"
            )
        }
        return uri
    }

    private fun isRedirectUriRegistered(mContext: Context): Boolean {
        // ensure that the redirect URI declared in the configuration is handled by some activity
        // in the app, by querying the package manager speculatively
        val redirectIntent = Intent()
        redirectIntent.setPackage(mContext.packageName)
        redirectIntent.action = Intent.ACTION_VIEW
        redirectIntent.addCategory(Intent.CATEGORY_BROWSABLE)
        redirectIntent.data = mRedirectUri
        return !mContext.packageManager?.queryIntentActivities(redirectIntent, 0)?.isEmpty()!!
    }

    class InvalidConfigurationException : Exception {
        internal constructor(reason: String?) : super(reason)
        internal constructor(reason: String?, cause: Throwable?) : super(
            reason,
            cause
        )
    }
}