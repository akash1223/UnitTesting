package com.inmoment.moments.framework.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.inmoment.moments.R
import com.inmoment.moments.framework.dto.Error.Companion.NO_INTERNET_CONNECTION

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */
fun convertDpToPixel(context: Context, dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp,
        context.resources.displayMetrics
    ).toInt()
}

fun getColorWrapper(context: Context, id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getColor(id)
    } else {
        @Suppress("DEPRECATION")
        context.resources.getColor(id)
    }
}

// Alert Dialogs
interface SingleButtonDialogInf {
    fun onPositiveButtonClick(identifier: Int)
}

interface DoubleButtonDialogInf : SingleButtonDialogInf {
    fun onNegativeButtonClick(identifier: Int)
}

class AlertParams(
    val title: String,
    val messageId: Int,
    val yesText: String,
)

fun showAlertDialog(
    context: Context,
    alertParams: AlertParams,
    okBtnCallback: (() -> Unit)? = null
) {
    val builder = AlertDialog.Builder(context)

    builder.setTitle(alertParams.title)

    // set message for alert dialog
    builder.setMessage(context.getString(alertParams.messageId))

    builder.setPositiveButton(alertParams.yesText) { dialogInterface, _ ->
        okBtnCallback?.invoke()
        dialogInterface.dismiss()
    }

    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.show()
}

fun getErrorDialog(errorCode: Int): Int {
    return when (errorCode) {
        NO_INTERNET_CONNECTION -> {
            R.string.no_connection
        }
        else -> R.string.something_went_wrong
    }
}

fun checkIntentIsReSolvable(context: Context, intent: Intent): Boolean {
    return intent.resolveActivity(context.packageManager) != null
}

fun blockUserTouchEvents(context: Activity) {
    context.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    );
}

fun unBlockUserTouchEvents(context: Activity) {
    context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun replaceFragment(fragmentActivity: FragmentActivity?, fragment: Fragment) {
    fragmentActivity?.supportFragmentManager?.beginTransaction()
        ?.replace(R.id.container, fragment)
        ?.commitNow()
}

fun pressButtonProgrammaticallyWithAnimation(view: View) {
    if (view.isEnabled) {
        view.apply {
            performClick()
            isPressed = true
            invalidate()
            isPressed = false
            invalidate()
        }

    }
}

