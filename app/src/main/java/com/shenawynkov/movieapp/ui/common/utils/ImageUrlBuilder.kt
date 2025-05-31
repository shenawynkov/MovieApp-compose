package com.shenawynkov.movieapp.ui.common.utils

object ImageUrlBuilder {
    private const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/"

    // Common image sizes, add more as needed
    const val POSTER_W342 = "w342"
    const val POSTER_W500 = "w500"
    const val PROFILE_W185 = "w185"

    fun buildPosterUrl(
        path: String?,
        size: String = POSTER_W342,
    ): String? = path?.let { "$BASE_IMAGE_URL$size$it" }

    fun buildProfileUrl(
        path: String?,
        size: String = PROFILE_W185,
    ): String? = path?.let { "$BASE_IMAGE_URL$size$it" }
}
