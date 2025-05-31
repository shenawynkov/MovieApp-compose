package com.shenawynkov.movieapp.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val tagline: String?,
    val revenue: Long,
    val releaseDate: String?,
    val status: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<String>,
    val runtimeMinutes: Int?,
    val isInWatchlist: Boolean = false,
)
