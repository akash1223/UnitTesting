package com.lysn.clinician.utility

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.lysn.clinician.utils.MixPanelData
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer


class CustomizedExceptionHandler : Thread.UncaughtExceptionHandler {
    private val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val stringBuffSync: Writer = StringWriter()
        val printWriter = PrintWriter(stringBuffSync)
        throwable.printStackTrace(printWriter)
        val stacktrace: String = stringBuffSync.toString()
        printWriter.close()
    }

}
