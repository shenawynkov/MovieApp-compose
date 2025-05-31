package com.shenawynkov.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CastMemberDto(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val popularity: Double,
    @SerializedName("known_for_department") val knownForDepartment: String?,
)
