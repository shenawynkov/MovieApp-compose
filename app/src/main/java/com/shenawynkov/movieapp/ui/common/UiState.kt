package com.shenawynkov.movieapp.ui.common // Or a more general 'ui.utils' or similar

/**
 * A generic class that holds a value with its loading status for the UI layer.
 * @param <T> The type of the data.
 */
sealed interface UiState<out T> {
    data class Success<T>(
        val data: T,
    ) : UiState<T>

    data class Error(
        val message: String,
    ) : UiState<Nothing> // UI-friendly error message

    data object Loading : UiState<Nothing>
} 
