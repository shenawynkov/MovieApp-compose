package com.shenawynkov.movieapp.domain.model

data class CastMember(
    val id: Int,
    val name: String,
    val profilePath: String?,
    val popularity: Double,
    val department: String,
)
