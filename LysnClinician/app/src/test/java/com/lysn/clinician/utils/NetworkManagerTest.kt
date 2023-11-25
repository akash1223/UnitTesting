package com.lysn.clinician.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * This class contains unit test for internet connectivity check
 */
@Suppress("DEPRECATION")
@RunWith(JUnit4::class)
class NetworkManagerTest {
    private lateinit var context: Context
    private lateinit var netManager: NetworkManager
    private lateinit var cManager: ConnectivityManager
    private lateinit var networkInfo: NetworkInfo

    @Before
    fun setup() {
        context = mock()
        netManager = NetworkManager(context)
        cManager = mock()
        networkInfo = mock()
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(cManager)
    }

    @Test
    fun checkIsInternetConnectionAvailable() {
        Mockito.`when`(cManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(true)
        val actualResult = netManager.isNetworkAvailable
        Assert.assertEquals(true, actualResult)
    }

    @Test
    fun checkIsInternetConnectionNotAvailable() {
        Mockito.`when`(cManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(false)
        val actualResult = netManager.isNetworkAvailable
        Assert.assertEquals(false, actualResult)
    }

    @Test
    fun checkIsInternetConnectionNotAvailableWhenNetworkInfoIsNull() {
        Mockito.`when`(cManager.activeNetworkInfo).thenReturn(null)
        val actualResult = netManager.isNetworkAvailable
        Assert.assertNull(cManager.activeNetworkInfo)
        Assert.assertEquals(false, actualResult)
    }
}