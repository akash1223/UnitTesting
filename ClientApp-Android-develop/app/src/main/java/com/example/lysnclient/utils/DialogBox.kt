package com.example.lysnclient.utils

import android.app.AlertDialog
import android.content.Context
import com.example.lysnclient.R
import timber.log.Timber

// This utility class used to show dialog throughout application.
class DialogBox(context: Context) : AlertDialog(context) {

    // Invokes when any screen is requesting to show alertdialog with Yes and No action
    fun showAlert(title: String, message: String, alertCallback: () -> Unit) {
        val alertDialog = Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setIcon(R.mipmap.ic_launcher_round)
        alertDialog.setPositiveButton(context.getString(R.string.alert_positive_button_text)) { _, _ ->
            Timber.e(context.getString(R.string.alert_positive_button_text))
            alertCallback()
        }
        alertDialog.setNegativeButton(context.getString(R.string.alert_negative_button_text)) { dialog, _ ->
            Timber.e(context.getString(R.string.alert_negative_button_text))
            dialog.cancel()
        }
        alertDialog.show()
    }

    // Invokes when any screen is requesting to show alertdialog with OK action
    fun showMessage(title: String, message: String, messageCallback: () -> Unit) {
        val alertDialog = Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setIcon(R.mipmap.ic_launcher_round)
        alertDialog.setPositiveButton(context.getString(R.string.alert_positive_button_OK_text)) { _, _ ->
            Timber.e(context.getString(R.string.alert_positive_button_OK_text))
            messageCallback()
        }
        alertDialog.show()
    }
}
