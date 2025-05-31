package com.shenawynkov.movieapp.domain.model

data class MovieContributor(
    val id: Int,
    val name: String,
    val profileImageUrl: String?,
    val popularity: Double,
    val role: Role,
)

enum class Role {
    ACTOR,
    DIRECTOR,
    OTHER,
}
