package com.shenawynkov.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?, // Used for the image
    val tagline: String?,
    val revenue: Long?,
    @SerializedName("release_date") val releaseDate: String?,
    val status: String?,
)
