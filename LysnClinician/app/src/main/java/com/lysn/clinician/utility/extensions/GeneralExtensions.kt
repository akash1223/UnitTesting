package com.lysn.clinician.utility.extensions


import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spannable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.URLSpan
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.http.Resource


/**
 * Wrapping try/catch to ignore catch block
 */
inline fun <T> justTry(block: () -> T) = try {
    block()
} catch (e: Throwable) {
    //ignore catch block
}

/**
 * App's debug mode
 */
inline fun debugMode(block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}

fun EditText.clearTextInput() {
    this.setText("")
}

fun EditText.getTextInput(): String {
    return this.text.toString()
}


fun TabLayout.setupWithViewPagerAndKeepIcons(viewPager: ViewPager?) {
    val icons = mutableListOf<Drawable?>()
    val tags = mutableListOf<String?>()
    repeat(tabCount) {
        icons.add(getTabAt(it)?.icon)
        tags.add(getTabAt(it)?.tag.toString())
    }
    setupWithViewPager(viewPager)

    repeat(tabCount) {
        getTabAt(it)?.icon = icons[it]
        getTabAt(it)?.tag = tags[it]
    }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun View.addOnSizeListener(
    heightChange: ((Int) -> Unit)? = null,
    WidthChange: ((Int) -> Unit)? = null
) {
    this.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas ->
        val widthWas = rightWas - leftWas // Right exclusive, left inclusive
        if (v.width != widthWas) {
            WidthChange?.let {
                WidthChange.invoke(widthWas)
            }
        }
        val heightWas = bottomWas - topWas // Bottom exclusive, top inclusive
        if (v.height != heightWas) {
            heightChange?.let {
                heightChange.invoke(widthWas)
            }
        }
    })
}

fun <T> LiveData<Resource<T>>.observeNetworkCall(
    lifecycleOwner: LifecycleOwner,
    observer: Observer<Resource<T>>
) {
    observe(lifecycleOwner, object : Observer<Resource<T>> {
        override fun onChanged(resource: Resource<T>) {

            if (resource.status != Resource.Status.LOADING) {
                observer.onChanged(resource)
                removeObserver(this)
            } else {
                observer.onChanged(resource)
            }
        }
    })
}

fun RecyclerView.scrollToBottom() {
    this.adapter?.itemCount?.minus(1)?.let {
        this.scrollToPosition(it)
    }
}

fun RecyclerView.scrollToTop() {
    if(this.adapter?.itemCount!! >0)
        this.scrollToPosition(0)
}

class URLSpanNoUnderline(spanText: String?) : URLSpan(spanText) {
    override fun updateDrawState(spanDrawState: TextPaint) {
        super.updateDrawState(spanDrawState)
        spanDrawState.isUnderlineText = false
    }
}

fun Spannable.removeUnderlines() {
    val spans: Array<URLSpan> = this.getSpans(0, this.length, URLSpan::class.java)

    for (spanText in spans) {
        var editableSpan = spanText
        val start: Int = this.getSpanStart(editableSpan)
        val end: Int = this.getSpanEnd(editableSpan)
        this.removeSpan(editableSpan)
        editableSpan = URLSpanNoUnderline(editableSpan.url)
        this.setSpan(editableSpan, start, end, 0)
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //This class use for only for afterTextChanged
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //This class use for only for afterTextChanged
        }
    })
}

fun EditText.validate(
    message: String,
    editTextWrapper: TextInputEditText,
    validator: (String) -> Boolean
) {
    this.afterTextChanged {
        editTextWrapper.error = if (validator(it)) null else message
    }
    editTextWrapper.error = if (validator(this.text.toString())) null else message
}