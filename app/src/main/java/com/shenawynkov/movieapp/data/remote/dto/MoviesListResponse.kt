package com.shenawynkov.movieapp.data.remote.dto

data class MoviesListResponse(
    val page: Int,
    val results: List<MovieDto>,
)
