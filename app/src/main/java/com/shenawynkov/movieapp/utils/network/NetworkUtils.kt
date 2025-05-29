package com.shenawynkov.movieapp.utils.network

import com.shenawynkov.movieapp.utils.data.MovieError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend inline fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    crossinline apiCall: suspend () -> T,
): Result<T> =
    withContext(dispatcher) {
        try {
            Result.Success(apiCall())
        } catch (e: Exception) {
            Result.Error(mapTechnicalError(e))
        }
    }

fun mapTechnicalError(exception: Exception): MovieError =
    when (exception) {
        is HttpException -> {
            when (exception.code()) {
                401 -> MovieError.Unauthorized
                404 -> MovieError.NotFound
                in 500..599 -> MovieError.ServerError(exception.code(), exception.message())
                else -> MovieError.ServerError(exception.code(), exception.message())
            }
        }
        is UnknownHostException, is ConnectException -> MovieError.NoConnection
        is SocketTimeoutException, is IOException -> MovieError.NetworkError
        else -> MovieError.UnknownError(exception)
    }
