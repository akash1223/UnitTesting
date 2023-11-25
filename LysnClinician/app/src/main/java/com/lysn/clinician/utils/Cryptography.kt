package com.lysn.clinician.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec


object Cryptography {

    private val ANDROID_KEY_STORE_NAME = "AndroidKeyStore"
    private val AES_MODE_M_OR_GREATER = "AES/GCM/NoPadding"
    private val KEY_ALIAS = "welysnAlias"
    private val CHARSET_NAME = "UTF-8"
    private val FIXED_IV = byteArrayOf(55, 54, 53, 52, 51, 50, 49, 48, 47, 46, 45, 44)

    fun encryptData(stringDataToEncrypt: String?): String? {
        initKeys()
        requireNotNull(stringDataToEncrypt) { "Data to be decrypted must be non null" }
        val cipher: Cipher = Cipher.getInstance(AES_MODE_M_OR_GREATER)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            getSecretKeyAPIMorGreater(), GCMParameterSpec(128,
                FIXED_IV
            )
        )

        val encodedBytes: ByteArray =
            cipher.doFinal(stringDataToEncrypt.toByteArray(charset(CHARSET_NAME)))
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }

    fun decryptData(encryptedData: String?): String? {
        initKeys()
        val encryptedDecodedData: ByteArray = Base64.decode(encryptedData, Base64.DEFAULT)
        val cipher = Cipher.getInstance(AES_MODE_M_OR_GREATER)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getSecretKeyAPIMorGreater(),
                GCMParameterSpec(128,
                    FIXED_IV
                )
            )

        val decodedBytes: ByteArray? = cipher?.doFinal(encryptedDecodedData)
        return decodedBytes?.let { String(it, charset(CHARSET_NAME)) }
    }

    private fun getSecretKeyAPIMorGreater(): Key? {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_NAME)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null)
    }

    @Throws(EncryptionException::class)
    private fun initKeys() {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_NAME)
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKeysForAPIMOrGreater()
        } else {
            val keyEntry = keyStore.getEntry(KEY_ALIAS, null)
            if (keyEntry !is KeyStore.SecretKeyEntry) {
                generateKeysForAPIMOrGreater()
            }
        }
    }

    @Throws(EncryptionException::class)
    private fun generateKeysForAPIMOrGreater() {
        val keyGenerator: KeyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEY_STORE_NAME
            )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE) // NOTE no Random IV. According to above this is less secure but acceptably so.
                .setRandomizedEncryptionRequired(false)
                .build()
        )
        keyGenerator.generateKey()
    }

}