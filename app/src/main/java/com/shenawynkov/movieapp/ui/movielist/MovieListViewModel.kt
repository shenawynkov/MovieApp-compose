package com.shenawynkov.movieapp.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shenawynkov.movieapp.R
import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.model.MoviesByYear
import com.shenawynkov.movieapp.domain.usecase.GetPopularMoviesUseCase
import com.shenawynkov.movieapp.domain.usecase.SearchMoviesUseCase
import com.shenawynkov.movieapp.domain.usecase.ToggleWatchlistUseCase
import com.shenawynkov.movieapp.ui.common.utils.StringResourceProvider
import com.shenawynkov.movieapp.ui.common.utils.toUserFriendlyMessage
import com.shenawynkov.movieapp.utils.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_PERIOD_MS = 350L
private const val MIN_QUERY_LENGTH = 3

data class MovieListScreenState(
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val moviesByYear: List<MoviesByYear> = emptyList(),
    val errorMessage: String? = null,
    val currentQuery: String = "",
    val isSearchActive: Boolean = false,
    val currentPage: Int = 1,
    val canLoadMore: Boolean = true,
    val infoMessageResId: Int? = null,
    val infoMessageArg: String? = null,
)

/**
 * ViewModel for the movie list/search screen. Handles popular movies, search, pagination, and watchlist toggling.
 * Uses debounced search input and exposes state via StateFlow.
 */
@HiltViewModel
class MovieListViewModel
    @Inject
    constructor(
        private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
        private val searchMoviesUseCase: SearchMoviesUseCase,
        private val toggleWatchlistUseCase: ToggleWatchlistUseCase,
        private val stringResourceProvider: StringResourceProvider,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(MovieListScreenState())
        val uiState: StateFlow<MovieListScreenState> = _uiState.asStateFlow()

        private val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        private val searchQueryFlow = MutableStateFlow("")
        private val unknownYearString by lazy { stringResourceProvider.getString(R.string.unknown_year) }

        init {
            loadPopularMovies(reset = true)
            setupDebouncedSearch()
        }

        /**
         * Sets up the debounced search logic for the search query flow.
         */
        @OptIn(FlowPreview::class)
        private fun setupDebouncedSearch() {
            searchQueryFlow
                .debounce(SEARCH_DEBOUNCE_PERIOD_MS)
                .distinctUntilChanged()
                .onEach { handleDebouncedQuery(it) }
                .launchIn(viewModelScope)
        }

        /**
         * Handles the debounced search query.
         */
        private fun handleDebouncedQuery(query: String) {
            _uiState.update { it.copy(infoMessageResId = null, infoMessageArg = null) } // Clear messages on new query handling
            when {
                query.isBlank() -> {
                    if (_uiState.value.isSearchActive) {
                        loadPopularMovies(reset = true)
                    }
                }
                query.length >= MIN_QUERY_LENGTH -> {
                    performSearch(query)
                }
                else -> {
                    updateStateForShortQuery(query)
                }
            }
        }

        /**
         * Updates the UI state for a query that is too short to trigger a search.
         */
        private fun updateStateForShortQuery(query: String) {
            _uiState.update {
                it.copy(
                    moviesByYear = emptyList(),
                    isLoading = false,
                    errorMessage = null,
                    canLoadMore = false,
                    isSearchActive = true,
                    currentQuery = query,
                    currentPage = 1,
                    infoMessageResId = R.string.search_query_too_short,
                    infoMessageArg = null,
                )
            }
        }

        fun onSearchQueryChanged(newQuery: String) {
            searchQueryFlow.value = newQuery
            // Info messages will be set by handleDebouncedQuery or direct search/load calls
            // We can clear previous movies if the query becomes blank and was active, or too short
            if (newQuery.isBlank() && _uiState.value.isSearchActive) {
                _uiState.update {
                    it.copy(
                        currentQuery = newQuery,
                        moviesByYear = emptyList(),
                        infoMessageResId = null,
                        infoMessageArg = null,
                    )
                }
            } else if (newQuery.isNotEmpty() && newQuery.length < MIN_QUERY_LENGTH && _uiState.value.isSearchActive) {
                _uiState.update {
                    it.copy(
                        currentQuery = newQuery,
                        moviesByYear = emptyList(),
                        infoMessageResId = R.string.search_query_too_short,
                        infoMessageArg = null,
                    )
                }
            } else {
                _uiState.update { it.copy(currentQuery = newQuery, isSearchActive = newQuery.isNotBlank()) }
            }
        }

        private fun performSearch(query: String) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        moviesByYear = emptyList(),
                        currentPage = 1,
                        isSearchActive = true,
                        currentQuery = query,
                        errorMessage = null,
                        canLoadMore = true,
                        infoMessageResId = null,
                        infoMessageArg = null,
                    )
                }
                fetchMoviesForSearch(query = query, pageToLoad = 1)
            }
        }

        fun onSearchSubmitted(submittedQuery: String) {
            val query = submittedQuery.trim()
            searchQueryFlow.value = query // Update flow to trigger debounced logic
            // Immediate UI update for certain cases handled by handleDebouncedQuery
            // or specific logic below if needed before debounce.
            if (query.isNotBlank() && query.length < MIN_QUERY_LENGTH) {
                updateStateForShortQuery(query) // Show short query message immediately
            } else if (query.isBlank() && _uiState.value.isSearchActive) {
                loadPopularMovies(reset = true) // If submitted blank, load popular
            }
            // If query is valid, debounced handleDebouncedQuery -> performSearch will take over
        }

        fun loadPopularMovies(
            pageToLoad: Int = 1,
            reset: Boolean = false,
        ) {
            viewModelScope.launch {
                _uiState.update {
                    if (reset) {
                        it.copy(
                            isLoading = true,
                            moviesByYear = emptyList(),
                            currentPage = 1,
                            isSearchActive = false,
                            currentQuery = "",
                            errorMessage = null,
                            canLoadMore = true,
                            infoMessageResId = null,
                            infoMessageArg = null,
                        )
                    } else {
                        it.copy(isLoadingNextPage = true, errorMessage = null, infoMessageResId = null, infoMessageArg = null)
                    }
                }

                when (val result = getPopularMoviesUseCase(page = pageToLoad)) {
                    is Result.Success -> {
                        val newMovies = result.data
                        _uiState.update { currentState ->
                            val currentMovies = if (reset) emptyList() else currentState.moviesByYear.flatMap { it.movies }
                            val updatedMovies = currentMovies + newMovies
                            val groupedMovies = groupMoviesByYear(updatedMovies)

                            val finalInfoMessageResId =
                                if (groupedMovies.isEmpty() && !currentState.isSearchActive && pageToLoad == 1) {
                                    R.string.popular_movies_empty
                                } else {
                                    null // No specific message if movies are present or it's not the first page of popular
                                }

                            currentState.copy(
                                isLoading = false,
                                isLoadingNextPage = false,
                                moviesByYear = groupedMovies,
                                currentPage = pageToLoad,
                                canLoadMore = newMovies.isNotEmpty(),
                                isSearchActive = if (reset) false else currentState.isSearchActive,
                                errorMessage = null,
                                infoMessageResId = finalInfoMessageResId,
                                infoMessageArg = null,
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoadingNextPage = false,
                                errorMessage = result.exception.toUserFriendlyMessage(stringResourceProvider),
                                canLoadMore = false,
                                infoMessageResId =
                                    if (it.moviesByYear.isEmpty() &&
                                        !it.isSearchActive
                                    ) {
                                        R.string.popular_movies_empty
                                    } else {
                                        null
                                    },
                                infoMessageArg = null,
                            )
                        }
                    }
                }
            }
        }

        private fun fetchMoviesForSearch(
            query: String,
            pageToLoad: Int,
        ) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isLoading = (pageToLoad == 1 && it.moviesByYear.isEmpty()),
                        isLoadingNextPage = (pageToLoad > 1),
                        errorMessage = null,
                        infoMessageResId = null,
                        infoMessageArg = null,
                    )
                }

                when (val result = searchMoviesUseCase(query = query, page = pageToLoad)) {
                    is Result.Success -> {
                        val newMovies = result.data
                        _uiState.update { currentState ->
                            val currentMovies = if (pageToLoad == 1) emptyList() else currentState.moviesByYear.flatMap { it.movies }
                            val updatedMovies = currentMovies + newMovies
                            val groupedMovies = groupMoviesByYear(updatedMovies)

                            val finalInfoMessageResId =
                                if (groupedMovies.isEmpty() && pageToLoad == 1) {
                                    R.string.search_no_results
                                } else {
                                    null // No message if results found or not first page
                                }
                            val finalInfoMessageArg = if (finalInfoMessageResId != null) query else null

                            currentState.copy(
                                isLoading = false,
                                isLoadingNextPage = false,
                                moviesByYear = groupedMovies,
                                currentPage = pageToLoad,
                                canLoadMore = newMovies.isNotEmpty(),
                                errorMessage = null,
                                infoMessageResId = finalInfoMessageResId,
                                infoMessageArg = finalInfoMessageArg,
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoadingNextPage = false,
                                errorMessage = result.exception.toUserFriendlyMessage(stringResourceProvider),
                                canLoadMore = false,
                                infoMessageResId = if (it.moviesByYear.isEmpty() && pageToLoad == 1) R.string.search_no_results else null,
                                infoMessageArg = if (it.moviesByYear.isEmpty() && pageToLoad == 1) query else null,
                            )
                        }
                    }
                }
            }
        }

        fun loadNextPage() {
            if (_uiState.value.isLoading || _uiState.value.isLoadingNextPage || !_uiState.value.canLoadMore) {
                return
            }
            _uiState.update { it.copy(infoMessageResId = null, infoMessageArg = null) } // Clear info messages before loading next page
            val nextPage = _uiState.value.currentPage + 1
            if (_uiState.value.isSearchActive) {
                fetchMoviesForSearch(query = _uiState.value.currentQuery, pageToLoad = nextPage)
            } else {
                loadPopularMovies(pageToLoad = nextPage, reset = false)
            }
        }

        private fun groupMoviesByYear(movies: List<Movie>): List<MoviesByYear> {
            val getYearFromDate = { dateString: String? ->
                if (dateString.isNullOrBlank()) {
                    null
                } else {
                    try {
                        val parsedDate = yearMonthDayFormat.parse(dateString)
                        if (parsedDate != null) {
                            val calendar = Calendar.getInstance()
                            calendar.time = parsedDate
                            calendar.get(Calendar.YEAR).toString()
                        } else {
                            null
                        }
                    } catch (e: ParseException) {
                        null
                    }
                }
            }
            return movies
                .distinctBy { it.id }
                .groupBy { getYearFromDate(it.releaseDate) ?: unknownYearString }
                .map { (year, movieList) -> MoviesByYear(year, movieList.sortedByDescending { movie -> movie.releaseDate }) }
                .sortedByDescending { it.year }
        }

        fun toggleWatchlistStatus(movieToToggle: Movie) {
            viewModelScope.launch {
                val newWatchlistStatus = toggleWatchlistUseCase(movieToToggle.id)
                _uiState.update { currentState ->
                    val updatedMoviesByYear =
                        currentState.moviesByYear.map { moviesByYearGroup ->
                            moviesByYearGroup.copy(
                                movies =
                                    moviesByYearGroup.movies.map { movie ->
                                        if (movie.id == movieToToggle.id) {
                                            movie.copy(isInWatchlist = newWatchlistStatus)
                                        } else {
                                            movie
                                        }
                                    },
                            )
                        }
                    // Clear info messages as this action might change the list state implicitly
                    currentState.copy(moviesByYear = updatedMoviesByYear, infoMessageResId = null, infoMessageArg = null)
                }
            }
        }
    }
