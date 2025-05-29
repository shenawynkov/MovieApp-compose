package com.shenawynkov.movieapp.domain.model

data class MoviesByYear(
    val year: String,
    val movies: List<Movie>,
)
