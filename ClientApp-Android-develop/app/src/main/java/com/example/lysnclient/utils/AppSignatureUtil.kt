package com.example.lysnclient.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.Arrays

// This class is used only for generating hashcode once.
// It need to remove when application goes to production.
class AppSignatureUtil(context: Context) {
    /**
     * Get all the app signatures for the current package
     */
    val appSignatures: ArrayList<String>
    val appSignature: String?
        get() = appSignatures.firstOrNull()

    init {
        val appCodes = ArrayList<String>()
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val signatures =
                if (Build.VERSION.SDK_INT >= 28) {
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    ).signingInfo.apkContentsSigners
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNATURES
                    ).signatures
                }
            // For each signature create a compatible hash
            for (signature in signatures) {
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }
                Timber.d(TAG, "Hash $hash")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.d(TAG, "PackageManager error", e)
        }
        appSignatures = appCodes
    }

    /**
     * Generates the hash by running sha on the string 'package hash'.
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            }
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64
            var base64Hash =
                Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)
            // Log.d(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash))
            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Timber.d(TAG, "hash:NoSuchAlgorithm", e)
        }
        return null
    }

    companion object {
        val TAG = AppSignatureUtil::class.java.simpleName
        private const val HASH_TYPE = "SHA-256"
        const val NUM_HASHED_BYTES = 9
        const val NUM_BASE64_CHAR = 11
    }
}