package com.inmoment.moments.framework.manager

import androidx.lifecycle.MutableLiveData
import com.inmoment.moments.framework.datamodel.RequestParam
import com.inmoment.moments.framework.dto.Error
import com.inmoment.moments.framework.dto.OperationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

open class TaskManager {

    var TAG = ""

    interface PerformTask<T> {
        fun performInBackground()
        fun performedInForeGround(op: OperationResult<T>)
    }

    open fun <P : RequestParam, T> execute(
        p: P,
        runInBgFun: KSuspendFunction1<RequestParam, OperationResult<T>>,
        coroutineScope: CoroutineScope
    ): OperationResult<T> {
        val opAggregator = OperationResult<T>(MutableLiveData(), MutableLiveData())
        val performTask = object : PerformTask<T> {
            override fun performInBackground() {
                coroutineScope.launch(Dispatchers.Main) {
                    val result = async(Dispatchers.IO) {
                        try {
                            runInBgFun(p)
                        } catch (e: Exception) {
                            exceptionHandel<T>(e)
                        }
                    }
                    performedInForeGround(result.await())
                }
            }

            override fun performedInForeGround(op: OperationResult<T>) {
                setResult(opAggregator, op)
            }
        }
        performTask.performInBackground()
        return opAggregator
    }

    fun <T> exceptionHandel(e: Exception): OperationResult<T> {
        val operationErrorResult = OperationResult<T>()
        operationErrorResult.exception = e
        operationErrorResult.error = Error.getError(e)
        return operationErrorResult
    }


    fun <T> execute(
        runInBgFunc: KSuspendFunction0<OperationResult<T>>,
        coroutineScope: CoroutineScope
    ): OperationResult<T> {
        val opAggregator = OperationResult<T>(MutableLiveData(), MutableLiveData())
        (object : PerformTask<T> {
            override fun performInBackground() {
                coroutineScope.launch(Dispatchers.Main) {

                    val result = async(Dispatchers.IO) {
                        try {
                           val callBack= runInBgFunc.invoke()
                            return@async callBack
                        } catch (e: Exception) {
                            exceptionHandel<T>(e)
                        }

                    }
                    performedInForeGround(result.await())
                }
            }

            override fun performedInForeGround(op: OperationResult<T>) {
                setResult(opAggregator, op)
            }
        }).performInBackground()
        return opAggregator
    }


    fun <T> setResult(opAggregator: OperationResult<T>, opInBetween: OperationResult<T>) {
        opInBetween.result?.let {
            opAggregator.successLiveData?.postValue(it)
        } ?: run {
            opAggregator.errorLiveData?.postValue(opInBetween.error)
        }
    }
}
