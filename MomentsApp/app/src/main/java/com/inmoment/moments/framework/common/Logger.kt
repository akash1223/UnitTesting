package com.inmoment.moments.framework.common

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

const val LOG_DIR = "MomentsAppLog"

object Logger {

    private const val PREFIX_TAG = "MomentsApp"
    private var isDebugBuild = true
    private var fh: FileHandler? = null
    private var fileLogger: Logger? = null
    private lateinit var fileName: String

    fun updateDebugBuildStatus(isDebugBuild: Boolean) {
        this.isDebugBuild = true
    }

    @VisibleForTesting
    fun getDebugStatus(): Boolean {
        return isDebugBuild
    }

    @VisibleForTesting
    fun getFileHandler(): FileHandler? {
        return fh
    }

    @VisibleForTesting
    fun getFileName(): String {
        return fileName
    }

    fun init(context: Context) {
        if (isDebugBuild) {
            fileLogger = Logger.getLogger(context.packageName)

            try {
                fileName = getFileName(context)
                fh = FileHandler(fileName, true)

                fh?.let {
                    val formatter = CustomFormatter()
                    it.formatter = formatter
                    fileLogger?.addHandler(it)
                }
            } catch (e: SecurityException) {
                Log.e(PREFIX_TAG, "" + e.message)
            } catch (e: IOException) {
                Log.e(PREFIX_TAG, "" + e.message)
            } catch (t: Throwable) {
                Log.e(PREFIX_TAG, "" + t.message)
            }
        }
    }
    @JvmStatic
    fun i(tag: String, message: String) {
        if (isDebugBuild) {
            Log.i(PREFIX_TAG + tag, "" + message)
            fileLogger?.info("$tag $message")
        }
    }

    @JvmStatic
    fun v(tag: String, message: String) {
        if (isDebugBuild) {
            Log.v(PREFIX_TAG + tag, "" + message)
            fileLogger?.info("$tag $message")
        }
    }

    @JvmStatic
    fun d(tag: String, message: String) {
        if (isDebugBuild) {
            Log.d(PREFIX_TAG + tag, "" + message)
            fileLogger?.info("$tag $message")
        }
    }

    @JvmStatic
    fun e(tag: String, message: String) {
        if (isDebugBuild) {
            Log.e(PREFIX_TAG + tag, "" + message)
            fileLogger?.info("$tag $message")
        }
    }

    private fun getFileName(context: Context): String {

        // Create Log Dir
        val logDir =
            File(context.externalCacheDir?.absolutePath + File.separator + LOG_DIR)

        if (!logDir.exists()) {
            val result = logDir.mkdirs()
            v(PREFIX_TAG, "Log dir created. -> $result")
        }

        val filePostFixName = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val file =
            File(context.externalCacheDir?.absolutePath + File.separator + LOG_DIR + File.separator + PREFIX_TAG + "_" + filePostFixName + ".log")

        return file.absolutePath
    }

    private fun getStackTraceFromException(th: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        th.printStackTrace(pw)
        return sw.toString()
    }
}

private class CustomFormatter : Formatter() {
    override fun format(record: LogRecord): String {
        val sb = StringBuilder()
        sb.append(Date().toString())
        sb.append(record.level).append(':')
        sb.append(record.message).append('\n')
        return sb.toString()
    }
}
