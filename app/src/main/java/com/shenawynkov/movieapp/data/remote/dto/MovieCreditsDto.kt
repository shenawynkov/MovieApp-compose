package com.shenawynkov.movieapp.data.remote.dto

data class MovieCreditsDto(
    val cast: List<CastMemberDto>,
    val crew: List<CrewMemberDto>,
)
