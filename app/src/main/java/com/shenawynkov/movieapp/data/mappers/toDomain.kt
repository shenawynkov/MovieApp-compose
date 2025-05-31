package com.shenawynkov.movieapp.data.mappers

import com.shenawynkov.movieapp.data.local.datasource.MovieLocalDataSource
import com.shenawynkov.movieapp.data.remote.dto.CastMemberDto
import com.shenawynkov.movieapp.data.remote.dto.CrewMemberDto
import com.shenawynkov.movieapp.data.remote.dto.MovieDetailDto
import com.shenawynkov.movieapp.data.remote.dto.MovieDto
import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.model.MovieContributor
import com.shenawynkov.movieapp.domain.model.MovieDetail
import com.shenawynkov.movieapp.domain.model.Role

suspend fun MovieDto.toDomain(localDataSource: MovieLocalDataSource): Movie =
    Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage ?: 0.0,
        isInWatchlist = localDataSource.isOnWatchlist(id),
    )

suspend fun MovieDetailDto.toDomain(localDataSource: MovieLocalDataSource): MovieDetail =
    MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        tagline = tagline,
        revenue = revenue ?: 0,
        releaseDate = releaseDate,
        status = status,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        genres = genres?.mapNotNull { it.name } ?: emptyList(),
        runtimeMinutes = runtime,
        isInWatchlist = localDataSource.isOnWatchlist(id),
    )

fun CastMemberDto.toMovieContributor(): MovieContributor {
    val role = Role.ACTOR
    return MovieContributor(
        id = id,
        name = name,
        profileImageUrl = profilePath?.let { "https://image.tmdb.org/t/p/w500$it" },
        popularity = popularity,
        role = role,
    )
}

fun CrewMemberDto.toMovieContributor(): MovieContributor {
    val role =
        if (
            department == "Directing" &&
            job?.equals("Director", ignoreCase = true) == true
        ) {
            Role.DIRECTOR
        } else {
            Role.OTHER
        }
    return MovieContributor(
        id = id,
        name = name,
        profileImageUrl = profilePath?.let { "https://image.tmdb.org/t/p/w500$it" },
        popularity = popularity,
        role = role,
    )
}
