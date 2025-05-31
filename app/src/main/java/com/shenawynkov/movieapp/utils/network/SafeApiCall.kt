package com.shenawynkov.movieapp.utils.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.shenawynkov.movieapp.utils.data.MovieError
import com.shenawynkov.movieapp.utils.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

// Data class to parse TMDB-like API error responses
private data class ApiErrorResponseBody(
    @SerializedName("status_message")
    val statusMessage: String?,
)

/**
 * Utility function to safely execute API calls, wrapping responses/errors into Result<T>.
 * Maps network and HTTP exceptions to domain-specific MovieError types,
 * attempting to parse a status_message from HTTP error bodies.
 */
suspend fun <T : Any> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T, // Assumes Retrofit suspend functions return T directly
): Result<T> =
    withContext(dispatcher) {
        try {
            Result.Success(apiCall())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> Result.Error(MovieError.NetworkConnectionProblem(throwable))
                is HttpException -> {
                    val code = throwable.code()
                    val errorBodyString = throwable.response()?.errorBody()?.string()
                    var apiMessage: String? = null
                    if (!errorBodyString.isNullOrBlank()) {
                        try {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBodyString, ApiErrorResponseBody::class.java)
                            apiMessage = errorResponse?.statusMessage
                        } catch (e: Exception) {
                            Log.w("SafeApiCall", "Failed to parse API error response body: $errorBodyString", e)
                        }
                    }
                    Result.Error(MovieError.HttpError(code, errorBodyString, apiMessage, throwable))
                }
                else -> {
                    Result.Error(MovieError.UnexpectedDataSourceError(throwable))
                }
            }
        }
    }
