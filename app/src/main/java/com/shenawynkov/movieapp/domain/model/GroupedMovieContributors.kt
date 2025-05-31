package com.shenawynkov.movieapp.domain.model

data class GroupedMovieContributors(
    val actors: List<MovieContributor>,
    val directors: List<MovieContributor>
) 