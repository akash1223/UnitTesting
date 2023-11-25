package com.example.lysnclient.utils

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SingletonHolderTest
{

    @Mock
    lateinit var context: Context

    //check created both instance same memory location
    @Test
    fun verify_single_instance()
    {
       val actualInstance=Manager.getInstance(context)
        val resultInstance=Manager.getInstance(context)
        assertEquals(actualInstance,resultInstance)
    }

    class Manager private constructor(context: Context) {
        companion object : SingletonHolder<Manager, Context>(::Manager)
    }
}
