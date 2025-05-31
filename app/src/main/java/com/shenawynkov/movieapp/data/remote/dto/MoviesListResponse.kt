package com.shenawynkov.movieapp.data.remote.dto

// import com.google.gson.annotations.SerializedName // Reverted

data class MoviesListResponse(
    // @SerializedName("page") // Reverted
    val page: Int,

    // @SerializedName("results") // Reverted
    val results: List<MovieDto>,

    // @SerializedName("total_pages") // Reverted
    // val totalPages: Int, // Reverted

    // @SerializedName("total_results") // Reverted
    // val totalResults: Int // Reverted
)
