package com.shenawynkov.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenreDto(
    val id: Int?,
    val name: String?
)

data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?, // Used for the image
    val tagline: String?,
    val revenue: Long?,
    @SerializedName("release_date") val releaseDate: String?,
    val status: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    val genres: List<GenreDto>?,
    val runtime: Int? // typically in minutes
)
