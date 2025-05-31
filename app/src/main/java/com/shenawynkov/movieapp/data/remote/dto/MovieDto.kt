package com.shenawynkov.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?, // Used for the image
    @SerializedName("release_date") val releaseDate: String?, // Used for grouping by year
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    @SerializedName("popularity") val popularity: Double?
)
