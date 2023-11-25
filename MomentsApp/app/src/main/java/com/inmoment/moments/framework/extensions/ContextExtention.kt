package com.lysn.clinician.utility.extensions

import android.R
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.IOException
import java.io.InputStream


fun Context.color(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)
fun Fragment.getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(requireContext(), colorRes)

fun Context.convertDpToPixel(dp: Float): Float {
    return dp * (this.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.readFileFromAssets(fileName: String?): String? {
    val jsonString: String
    jsonString = try {
        val inputStream: InputStream = this.assets.open(fileName!!)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, charset("UTF-8"))
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return jsonString
}

fun Activity.getToolbarHeight(): Int {
    return try {
        val tv = TypedValue()
        var actionBarHeight = 0
        if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight =
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        actionBarHeight
    } catch (ex: Exception) {
        48.dp
    }
}

fun RecyclerView.addLineDividerDecorator(lineColor: Int = 0) {
    this.layoutManager?.let {
        val mDividerItemDecoration =
            DividerItemDecoration(
                this.context,
                it.layoutDirection
            )
        if (lineColor != 0) {
            this.context.getDrawable(lineColor)
                ?.let { it1 -> mDividerItemDecoration.setDrawable(it1) }
        }
        this.addItemDecoration(mDividerItemDecoration)
    }
}

fun Activity.getRootView(): View {
    return findViewById(R.id.content)
}

fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = Math.round(this.convertDpToPx(50F))
    return heightDiff > marginOfError
}

fun View.getBackgroundByShape(layerId: Int): GradientDrawable {
    val layerDrawable = this.background as LayerDrawable
    return layerDrawable.findDrawableByLayerId(layerId) as GradientDrawable
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

/**
 * Inflate the layout specified by [layoutRes].
 */

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}


fun ImageView.loadFromUrl(url: String, context: Context) {
    Glide.with(context).load(url).into(this)
}

fun Context.getResString(stringRes: Int): String {
    return this.resources.getString(stringRes)
}

fun Context.getHtmlSpannedString(@StringRes id: Int): Spanned = getString(id).toHtmlSpan()

fun Context.getHtmlSpannedString(@StringRes id: Int, vararg formatArgs: Any): Spanned =
    getString(id, *formatArgs).toHtmlSpan()

fun TextView.setStyle(@StyleRes id: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        this.setTextAppearance(this.context, id)
    } else {
        this.setTextAppearance(id)
    }
}


fun String.toHtmlSpan(): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}

fun Context.hideKeyboard() {
    try {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if ((this as Activity).currentFocus == null || this.currentFocus?.windowToken == null) {
            return
        }
        inputManager.hideSoftInputFromWindow(
            this.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    } catch (exception: Exception) {

    }
}

fun View.hideKeyboard(context: Context?) {
    val inputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Context.toast(msg: Int) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.longToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
fun Context.longToast(msg: Int) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()