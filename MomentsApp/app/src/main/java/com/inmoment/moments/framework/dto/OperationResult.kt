package com.inmoment.moments.framework.dto

import androidx.lifecycle.MutableLiveData

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

class OperationResult<T>() {

    var successLiveData: MutableLiveData<T>? = null
    var errorLiveData: MutableLiveData<Error>? = null

    constructor(
        successLiveData: MutableLiveData<T>,
        errorLiveData: MutableLiveData<Error>
    ) : this() {
        this.successLiveData = successLiveData
        this.errorLiveData = errorLiveData
    }

    var result: T? = null
    var error: Error? = null

    var exception: Exception? = null

    fun generateError(errorCode: Int = Error.RETURN_FROM_VIEW_MODEL): OperationResult<T> {
        if (this.errorLiveData == null) {
            this.errorLiveData = MutableLiveData()
        }
        this.errorLiveData?.postValue(Error.getError(errorCode))
        return this
    }
}
