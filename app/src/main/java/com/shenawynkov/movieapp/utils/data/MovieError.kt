package com.shenawynkov.movieapp.utils.data

sealed class MovieError : Exception() {
    data object NetworkError : MovieError()
    data object NoConnection : MovieError()
    data class ServerError(val code: Int,  val remoteMessage: String) : MovieError()
    data object NotFound : MovieError()
    data object Unauthorized : MovieError()
    data class ValidationError(val field: String) : MovieError()
    data class UnknownError(val cause: Throwable) : MovieError()
}
