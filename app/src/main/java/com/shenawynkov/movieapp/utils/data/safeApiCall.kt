package com.shenawynkov.movieapp.utils.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T,
): Result<T> =
    withContext(dispatcher) {
        try {
            Result.Success(apiCall())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
