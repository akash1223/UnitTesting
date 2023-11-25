package com.inmoment.moments.framework.dto

import com.inmoment.moments.framework.manager.network.NoConnectivityException
import java.net.SocketTimeoutException

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

class Error {
    var errorCode: Int = -1 // Something went wrong price try again.

    companion object {

        const val NO_INTERNET_CONNECTION = 4000
        private const val SOCKET_TIMEOUT_EXCEPTION = 4001
        const val RETURN_FROM_VIEW_MODEL = 21

        fun getError(errorCode: Int): Error {
            val error = Error()
            error.errorCode = errorCode
            return error
        }

        fun getError(t: Throwable): Error {
            val error = Error()

            if (t is NoConnectivityException) {
                error.errorCode = NO_INTERNET_CONNECTION
            }
            if (t is SocketTimeoutException) {
                error.errorCode = SOCKET_TIMEOUT_EXCEPTION
            }

            return error
        }
    }
}
