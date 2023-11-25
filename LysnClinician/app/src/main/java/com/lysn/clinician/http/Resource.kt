package com.lysn.clinician.http

/**
 * A generic class that holds a value with its loading status.
 *
 * Result is usually created by the Repository classes where they return
 * `LiveData<Result<T>>` to pass back the latest data to the UI with its fetch status.
 */

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        EMPTY_RESPONSE,
        ERROR,
        NO_INTERNET,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> empty(message: String, data: T? = null): Resource<T> {
            return Resource(Status.EMPTY_RESPONSE, data, message)
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }
        fun <T> noInternet(message: String,data: T? = null): Resource<T> {
            return Resource(Status.NO_INTERNET, data, message)
        }
        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}