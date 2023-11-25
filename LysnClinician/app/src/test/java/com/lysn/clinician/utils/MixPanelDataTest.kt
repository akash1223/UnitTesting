package com.lysn.clinician.utils

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MixPanelDataTest
{
    @Mock
    lateinit var mixPanelData: MixPanelData

    var eventName="session_start"
    @Test
    fun verify_add_event()
    {
        mixPanelData.addEvent(eventName)
        verify(mixPanelData, times(1)).addEvent(eventName)
    }
    @Test
    fun verify_add_event_key_value_pair()
    {
        mixPanelData.addEvent("screen","HomeScreen",eventName)
        verify(mixPanelData, times(1)).addEvent("screen","HomeScreen",eventName)
    }
    @Test
    fun verify_add_event_JSON()
    {
        val properties = mock<JSONObject>()
        mixPanelData.addEvent(properties,eventName)
        verify(mixPanelData, times(1)).addEvent(properties,eventName)
    }

    @Test
    fun verify_flushMixPanel()
    {
        mixPanelData.flushMixPanel()
        verify(mixPanelData, times(1)).flushMixPanel()
    }

    @Test
    fun verify_createProfile_by_using_email_phone()
    {
        mixPanelData.createProfile("jhon@welysn.com","12345678")
        verify(mixPanelData, times(1)).createProfile("jhon@welysn.com","12345678")
    }
    @Test
    fun verify_createProfile_by_using_email()
    {
        mixPanelData.createProfile("jhon@welysn.com")
        verify(mixPanelData, times(1)).createProfile("jhon@welysn.com")
    }
}