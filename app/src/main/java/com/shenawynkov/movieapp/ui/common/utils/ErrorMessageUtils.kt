package com.shenawynkov.movieapp.ui.common.utils

import com.shenawynkov.movieapp.R
import com.shenawynkov.movieapp.utils.data.MovieError

/**
 * Converts a Throwable, especially a MovieError, into a user-friendly message
 * using a StringResourceProvider for localization.
 */
fun Throwable.toUserFriendlyMessage(stringResourceProvider: StringResourceProvider): String =
    when (this) {
        is MovieError.HttpError -> {
            // Map HTTP error codes to user-friendly strings defined in strings.xml
            when (this.code) {
                401, 403 -> stringResourceProvider.getString(R.string.error_access_denied)
                404 -> stringResourceProvider.getString(R.string.error_not_found)
                in 400..499 -> stringResourceProvider.getString(R.string.error_bad_request)
                in 500..599 -> stringResourceProvider.getString(R.string.error_server_unavailable)
                else -> stringResourceProvider.getString(R.string.error_service_unexpected)
            }
        }
        is MovieError.NetworkConnectionProblem -> stringResourceProvider.getString(R.string.error_no_connection)
        is MovieError.ApiValidationProblem -> stringResourceProvider.getString(R.string.error_api_validation) // Details are no longer passed
        is MovieError.UnexpectedDataSourceError -> stringResourceProvider.getString(R.string.error_unexpected_data)

        // Fallbacks for generic exceptions if they somehow bypass MovieError wrapping
        is retrofit2.HttpException -> {
            when (this.code()) {
                401, 403 -> stringResourceProvider.getString(R.string.error_access_denied_service)
                404 -> stringResourceProvider.getString(R.string.error_not_found_service)
                in 500..599 -> stringResourceProvider.getString(R.string.error_service_unavailable_fallback)
                else -> stringResourceProvider.getString(R.string.error_network_service_code) // Code is no longer passed
            }
        }
        is java.io.IOException -> {
            stringResourceProvider.getString(R.string.error_network_connection_fallback)
        }
        else -> this.localizedMessage ?: stringResourceProvider.getString(R.string.error_unknown)
    } 
