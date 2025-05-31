package com.shenawynkov.movieapp.utils.data

/**
 * A sealed class representing categorized errors that can occur in the data layer.
 * Each error type can carry a specific message and optionally the original cause.
 */
sealed class MovieError(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message, cause) {
    /**
     * Represents errors related to HTTP communication (e.g., 4xx, 5xx responses).
     * @param code The HTTP status code.
     * @param errorBody The raw error body string from the response, for debugging or further parsing.
     * @param apiMessage An optional, parsed human-readable message from the API's error response.
     * @param specificCause The original Throwable, often a Retrofit HttpException.
     */
    data class HttpError(
        val code: Int,
        val errorBody: String?,
        val apiMessage: String?,
        val specificCause: Throwable?,
    ) : MovieError(message = apiMessage ?: "HTTP error $code", cause = specificCause)

    /**
     * Represents problems with network connectivity (e.g., no internet, DNS issues).
     * @param specificCause The original IOException or similar connectivity-related Throwable.
     */
    data class NetworkConnectionProblem(
        val specificCause: Throwable?,
    ) : MovieError(message = "Network connection problem", cause = specificCause)

    /**
     * Represents errors due to invalid data or parameters sent to an API,
     * often indicated by specific API error messages or codes (e.g., HTTP 400, 422).
     * @param details A descriptive message about the validation failure (ideally a non-localized key or resource ID).
     * @param specificCause Optional underlying cause.
     */
    data class ApiValidationProblem(
        val details: String,
        val specificCause: Throwable? = null,
    ) : MovieError(message = "API validation problem: $details", cause = specificCause)

    /**
     * For any other unexpected errors originating from data sources that don't fit
     * into the more specific categories above.
     * @param specificCause The original unexpected Throwable.
     */
    data class UnexpectedDataSourceError(
        val specificCause: Throwable?,
    ) : MovieError(message = "An unexpected data source error occurred", cause = specificCause)
}
