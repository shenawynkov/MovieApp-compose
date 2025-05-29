package com.shenawynkov.movieapp.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val tagline: String?,
    val revenue: Long,
    val releaseDate: String,
    val status: String,
    val isInWatchlist: Boolean = false,
)
