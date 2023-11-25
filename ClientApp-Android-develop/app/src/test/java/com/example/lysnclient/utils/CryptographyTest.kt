package com.example.lysnclient.utils


import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Test
import org.junit.Assert.*


class CryptographyTest {

    private val dataForEncryption="Test Value"
    private var decryptedDate="XYZ"
    private var originalValue: String? = AppConstants.EMPTY_VALUE

    @Test
    fun `verify_encryptData()_decryptData()`() {
        mockkObject(Cryptography)

        every { Cryptography.encryptData(dataForEncryption) } returns decryptedDate
        every { Cryptography.decryptData(decryptedDate) } returns dataForEncryption

        decryptedDate = Cryptography.encryptData(dataForEncryption).toString()
        originalValue = Cryptography.decryptData(decryptedDate).toString()

        verify(exactly = 1) { Cryptography.encryptData(dataForEncryption).toString() }
        verify(exactly = 1) { Cryptography.decryptData(decryptedDate).toString() }
        assertEquals(dataForEncryption, originalValue)
    }

}
