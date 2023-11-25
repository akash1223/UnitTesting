package com.lysn.clinician.utils

import android.content.Context
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.lysn.clinician.R
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BindingAdaptersTest {

    lateinit var bindingAdapters: BindingAdapters

    @Mock
    lateinit var context: Context

    var dateTimeString="2020-08-20T09:30:00+05:30"
    @Before
    fun setUp() {
        bindingAdapters = BindingAdapters(context)
    }

    @Test
    fun notNullCheck() {
        assertNotNull(context)
        assertNotNull(bindingAdapters)
    }

    @Test
    fun verify_setError() {
        val errorMessage = "Invalid Filed"
        var textView = mock<TextInputLayout>()
        `when`(textView.error).thenReturn(errorMessage)
        BindingAdapters.setError(textView, errorMessage)
        assertEquals(textView.error, errorMessage)
    }

    @Test
    fun verify_bindServerDate() {
        var textView = mock<TextView>()
        val parsedDate = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
        val result=parsedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM, hh:mma", Locale.UK))
        `when`(textView.text).thenReturn(result)
        BindingAdapters.bindServerDate(textView, dateTimeString)
        assertEquals(textView.text, result)
    }

    @Test
     fun verify_bindCallType_video() {
        var textView = mock<TextView>()
        val videoDrawableId=intArrayOf(12345, 0,0, 0)
        `when`(textView.drawableState).thenReturn(videoDrawableId)
        BindingAdapters.bindCallType(textView, "video")
        assertEquals(textView.drawableState, videoDrawableId)
    }
    @Test
    fun verify_bindCallType_video_phone() {
        var textView = mock<TextView>()
        val phoneDrawableId=intArrayOf(67895, 0,0, 0)
       `when`(textView.drawableState).thenReturn(phoneDrawableId)
        BindingAdapters.bindCallType(textView, "phone")
        assertEquals(textView.drawableState, phoneDrawableId)

    }
    @Test
    fun verify_bindCallType_face_to_face() {
        var textView = mock<TextView>()
        val faceDrawableId=intArrayOf(23456, 0,0, 0)
        `when`(textView.drawableState).thenReturn(faceDrawableId)
        BindingAdapters.bindCallType(textView, "f2f")
        assertEquals(textView.drawableState, faceDrawableId)
    }
    @Test
    fun verify_empty_loadImage() {
        val imageView= mock<ImageView>()
        `when`(imageView.context).thenReturn(context)
        BindingAdapters.loadImage(imageView,AppConstants.EMPTY_VALUE)
        assertEquals(imageView.context,context)
    }
}