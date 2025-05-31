package com.shenawynkov.movieapp.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shenawynkov.movieapp.domain.model.GroupedMovieContributors
import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.model.MovieDetail
import com.shenawynkov.movieapp.domain.usecase.GetCreditsForSimilarMoviesUseCase
import com.shenawynkov.movieapp.domain.usecase.GetMovieDetailUseCase
import com.shenawynkov.movieapp.domain.usecase.GetSimilarMoviesUseCase
import com.shenawynkov.movieapp.domain.usecase.ToggleWatchlistUseCase
import com.shenawynkov.movieapp.ui.common.UiState
import com.shenawynkov.movieapp.ui.common.utils.StringResourceProvider
import com.shenawynkov.movieapp.ui.common.utils.toUserFriendlyMessage
import com.shenawynkov.movieapp.utils.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MOVIE_ID_SAVED_STATE_KEY = "movieId"

data class MovieDetailScreenState(
    val movieDetail: UiState<MovieDetail> = UiState.Loading,
    val similarMovies: UiState<List<Movie>> = UiState.Loading,
    val similarMovieCredits: UiState<GroupedMovieContributors> = UiState.Loading,
    val isOverallLoading: Boolean = true,
)

@HiltViewModel
class MovieDetailViewModel
    @Inject
    constructor(
        private val getMovieDetailUseCase: GetMovieDetailUseCase,
        private val getSimilarMoviesUseCase: GetSimilarMoviesUseCase,
        private val getCreditsForSimilarMoviesUseCase: GetCreditsForSimilarMoviesUseCase,
        private val toggleWatchlistUseCase: ToggleWatchlistUseCase,
        private val stringResourceProvider: StringResourceProvider,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(MovieDetailScreenState())
        val uiState: StateFlow<MovieDetailScreenState> = _uiState.asStateFlow()

        private val movieId: Int =
            savedStateHandle.get<Int>(MOVIE_ID_SAVED_STATE_KEY)
                ?: throw IllegalArgumentException(
                    "Movie ID not found in SavedStateHandle. Ensure it is passed correctly during navigation.",
                )

        init {
            fetchAllMovieInfo()
        }

        fun fetchAllMovieInfo() {
            viewModelScope.launch {
                // Set initial loading states for each section and overall loading
                _uiState.value =
                    MovieDetailScreenState(
                        movieDetail = UiState.Loading,
                        similarMovies = UiState.Loading,
                        similarMovieCredits = UiState.Loading,
                        isOverallLoading = true,
                    )

                val movieDetailDeferred = async { getMovieDetailUseCase(movieId = movieId) }
                val similarMoviesDeferred = async { getSimilarMoviesUseCase(movieId = movieId) }

                // Process movie detail result
                when (val detailResult = movieDetailDeferred.await()) {
                    is Result.Success -> {
                        _uiState.update { it.copy(movieDetail = UiState.Success(detailResult.data)) }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                movieDetail = UiState.Error(detailResult.exception.toUserFriendlyMessage(stringResourceProvider)),
                            )
                        }
                    }
                }

                // Process similar movies result
                val similarMoviesDataResult = similarMoviesDeferred.await()
                when (similarMoviesDataResult) {
                    is Result.Success -> {
                        _uiState.update { it.copy(similarMovies = UiState.Success(similarMoviesDataResult.data)) }
                        // Fetch credits only if similar movies were successfully fetched and are not empty
                        if (similarMoviesDataResult.data.isNotEmpty()) {
                            _uiState.update { it.copy(similarMovieCredits = UiState.Loading) } // Set credits to loading before fetch
                            when (
                                val creditsResult =
                                    getCreditsForSimilarMoviesUseCase(
                                        movieIds = similarMoviesDataResult.data.map { it.id },
                                    )
                            ) {
                                is Result.Success -> {
                                    _uiState.update { it.copy(similarMovieCredits = UiState.Success(creditsResult.data)) }
                                }
                                is Result.Error -> {
                                    _uiState.update {
                                        it.copy(
                                            similarMovieCredits =
                                                UiState.Error(
                                                    creditsResult.exception.toUserFriendlyMessage(stringResourceProvider),
                                                ),
                                        )
                                    }
                                }
                            }
                        } else {
                            // No similar movies, so credits section is success with empty GroupedMovieContributors
                            _uiState.update {
                                it.copy(
                                    similarMovieCredits = UiState.Success(GroupedMovieContributors(emptyList(), emptyList())),
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                similarMovies =
                                    UiState.Error(
                                        similarMoviesDataResult.exception.toUserFriendlyMessage(stringResourceProvider),
                                    ),
                            )
                        }
                        // If similar movies failed, credits section should also reflect an error
                        _uiState.update {
                            it.copy(
                                similarMovieCredits =
                                    UiState.Error(
                                        similarMoviesDataResult.exception.toUserFriendlyMessage(stringResourceProvider),
                                    ),
                            )
                        }
                    }
                }
                _uiState.update { it.copy(isOverallLoading = false) } // All initial fetches are done
            }
        }

        fun retrySection(section: DetailScreenSection) {
            viewModelScope.launch {
                when (section) {
                    DetailScreenSection.DETAILS -> {
                        _uiState.update { it.copy(movieDetail = UiState.Loading) }
                        when (val result = getMovieDetailUseCase(movieId = movieId)) {
                            is Result.Success -> _uiState.update { it.copy(movieDetail = UiState.Success(result.data)) }
                            is Result.Error ->
                                _uiState.update {
                                    it.copy(
                                        movieDetail = UiState.Error(result.exception.toUserFriendlyMessage(stringResourceProvider)),
                                    )
                                }
                        }
                    }
                    DetailScreenSection.SIMILAR_MOVIES_AND_CREDITS -> {
                        _uiState.update { it.copy(similarMovies = UiState.Loading, similarMovieCredits = UiState.Loading) }
                        when (val similarResult = getSimilarMoviesUseCase(movieId = movieId)) {
                            is Result.Success -> {
                                _uiState.update { it.copy(similarMovies = UiState.Success(similarResult.data)) }
                                if (similarResult.data.isNotEmpty()) {
                                    // Set credits to loading before fetch
                                    _uiState.update { it.copy(similarMovieCredits = UiState.Loading) }
                                    when (
                                        val creditsResult =
                                            getCreditsForSimilarMoviesUseCase(
                                                movieIds = similarResult.data.map { it.id },
                                            )
                                    ) {
                                        is Result.Success ->
                                            _uiState.update {
                                                it.copy(
                                                    similarMovieCredits = UiState.Success(creditsResult.data),
                                                )
                                            }
                                        is Result.Error ->
                                            _uiState.update {
                                                it.copy(
                                                    similarMovieCredits =
                                                        UiState.Error(
                                                            creditsResult.exception.toUserFriendlyMessage(stringResourceProvider),
                                                        ),
                                                )
                                            }
                                    }
                                } else {
                                    // No similar movies, so credits section is success with empty GroupedMovieContributors
                                    _uiState.update {
                                        it.copy(
                                            similarMovieCredits = UiState.Success(GroupedMovieContributors(emptyList(), emptyList())),
                                        )
                                    }
                                }
                            }
                            is Result.Error -> {
                                _uiState.update {
                                    it.copy(
                                        similarMovies =
                                            UiState.Error(
                                                similarResult.exception.toUserFriendlyMessage(stringResourceProvider),
                                            ),
                                    )
                                }
                                _uiState.update {
                                    it.copy(
                                        similarMovieCredits =
                                            UiState.Error(
                                                similarResult.exception.toUserFriendlyMessage(stringResourceProvider),
                                            ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        fun toggleWatchlistStatus() {
            viewModelScope.launch {
                val currentMovieDetailState = _uiState.value.movieDetail
                if (currentMovieDetailState is UiState.Success) {
                    val currentMovieDetail = currentMovieDetailState.data
                    val newWatchlistStatus = toggleWatchlistUseCase(currentMovieDetail.id)
                    // Update the state with the new watchlist status
                    _uiState.update {
                        it.copy(movieDetail = UiState.Success(currentMovieDetail.copy(isInWatchlist = newWatchlistStatus)))
                    }
                } // If movieDetail is not Success (e.g. Loading/Error), do nothing for toggle
            }
        }
    }

enum class DetailScreenSection {
    DETAILS,
    SIMILAR_MOVIES_AND_CREDITS,
} 
