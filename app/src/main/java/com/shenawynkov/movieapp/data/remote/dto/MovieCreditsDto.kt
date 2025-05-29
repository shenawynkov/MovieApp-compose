package com.shenawynkov.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieCreditsDto(
    val cast: List<CastMemberDto>,
    val crew: List<CrewMemberDto>,
)

data class CastMemberDto(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val popularity: Double,
)

data class CrewMemberDto(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val popularity: Double,
    val job: String?,
    val department: String?,
)
