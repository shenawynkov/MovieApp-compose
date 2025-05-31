package com.shenawynkov.movieapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CrewMemberDto(
    val id: Int,
    val name: String,
    val job: String?,
    val department: String?,
    @SerializedName("profile_path") val profilePath: String?,
    val popularity: Double,
)
