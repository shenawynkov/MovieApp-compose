package com.shenawynkov.movieapp.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shenawynkov.movieapp.domain.model.GroupedMovieContributors
import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.model.MovieContributor
import com.shenawynkov.movieapp.domain.model.MovieDetail
import com.shenawynkov.movieapp.ui.common.UiState
import com.shenawynkov.movieapp.ui.common.utils.ImageUrlBuilder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onMovieItemClick: (movieId: Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MovieDetailTopAppBar(
                movieDetailUiState = uiState.movieDetail,
                onNavigateBack = onNavigateBack,
                onToggleWatchlist = { viewModel.toggleWatchlistStatus() },
                onRefresh = { viewModel.fetchAllMovieInfo() },
            )
        },
    ) { paddingValues ->

        if (uiState.isOverallLoading && uiState.movieDetail !is UiState.Success) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
            ) {
                // Section 1: Movie Details
                item(key = "movie_details_section") {
                    HandleUiStateSection(
                        uiState = uiState.movieDetail,
                        onRetry = { viewModel.retrySection(DetailScreenSection.DETAILS) },
                        emptyContentMessage = "Movie details not available.",
                    ) { movieDetail ->
                        MovieDetailsSection(movieDetail = movieDetail)
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                // Section 2: Similar Movies
                item(key = "similar_movies_section") {
                    HandleUiStateSection(
                        uiState = uiState.similarMovies,
                        onRetry = { viewModel.retrySection(DetailScreenSection.SIMILAR_MOVIES_AND_CREDITS) },
                        emptyContentMessage = "No similar movies found.",
                    ) { similarMovies ->
                        if (similarMovies.isNotEmpty()) {
                            SimilarMoviesSection(
                                movies = similarMovies,
                                onMovieClick = onMovieItemClick,
                            )
                        } else {
                            Text(
                                "No similar movies found.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 16.dp),
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                // Section 3: Credits for Similar Movies
                item(key = "similar_movies_credits_section") {
                    HandleUiStateSection(
                        uiState = uiState.similarMovieCredits,
                        onRetry = { viewModel.retrySection(DetailScreenSection.SIMILAR_MOVIES_AND_CREDITS) },
                        emptyContentMessage = "Credits not available for similar movies.",
                    ) { groupedCredits ->
                        if (groupedCredits.actors.isNotEmpty() || groupedCredits.directors.isNotEmpty()) {
                            SimilarMoviesCreditsSection(groupedContributors = groupedCredits)
                        } else {
                            // Display empty message if needed, or rely on HandleUiStateSection's own emptyContentMessage
                            // For now, let HandleUiStateSection manage the empty message based on its logic
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailTopAppBar(
    movieDetailUiState: UiState<MovieDetail>,
    onNavigateBack: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onRefresh: () -> Unit,
) {
    val title = (movieDetailUiState as? UiState.Success)?.data?.title ?: "Movie Details"
    val isInWatchlist = (movieDetailUiState as? UiState.Success)?.data?.isInWatchlist ?: false
    val showWatchlistAction = movieDetailUiState is UiState.Success

    TopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
            }
        },
        actions = {
            if (showWatchlistAction) {
                IconButton(onClick = onToggleWatchlist) {
                    Icon(
                        imageVector = if (isInWatchlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isInWatchlist) "Remove from Watchlist" else "Add to Watchlist",
                        tint = if (isInWatchlist) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                    )
                }
            }
            IconButton(onClick = onRefresh) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh Details")
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    )
}

@Composable
fun <T> HandleUiStateSection(
    uiState: UiState<T>,
    onRetry: () -> Unit,
    emptyContentMessage: String = "No data available.",
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
    successContent: @Composable (data: T) -> Unit,
) {
    when (uiState) {
        is UiState.Loading -> {
            loadingContent()
        }

        is UiState.Success -> {
            val data = uiState.data
            // Adjusted empty check for GroupedMovieContributors specifically if T is GroupedMovieContributors
            val isEmpty =
                when (data) {
                    is List<*> -> data.isEmpty()
                    is GroupedMovieContributors -> data.actors.isEmpty() && data.directors.isEmpty()
                    else -> false // For other types, assume not empty or handle specifically if needed
                }
            if (isEmpty) {
                Text(
                    emptyContentMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            } else {
                successContent(data)
            }
        }

        is UiState.Error -> {
            GeneralErrorSection(message = uiState.message, onRetry = onRetry)
        }
    }
}

@Composable
fun DefaultLoadingContent() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun GeneralErrorSection(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun MovieDetailsSection(movieDetail: MovieDetail) {
    Column {
        // Poster and Basic Info Row
        Row(verticalAlignment = Alignment.Top) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(
                            ImageUrlBuilder.buildPosterUrl(
                                movieDetail.posterPath,
                                ImageUrlBuilder.POSTER_W500,
                            ) ?: "",
                        ).crossfade(true)
                        // .placeholder(R.drawable.placeholder_poster)
                        // .error(R.drawable.error_poster)
                        .build(),
                contentDescription = movieDetail.title,
                modifier =
                    Modifier
                        .width(150.dp)
                        .height(225.dp)
                        .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    movieDetail.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Release: ${movieDetail.releaseDate ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Rating: ${
                        String.format(
                            "%.1f",
                            movieDetail.voteAverage,
                        )
                    }/10 (${movieDetail.voteCount} votes)",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Runtime: ${movieDetail.runtimeMinutes ?: "N/A"} min",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(movieDetail.genres) { genre ->
                        SuggestionChip(
                            onClick = { /* No action for now */ },
                            label = { Text(genre) },
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Overview", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            movieDetail.overview ?: "Overview not available.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun SimilarMoviesSection(
    movies: List<Movie>,
    onMovieClick: (movieId: Int) -> Unit,
) {
    Column {
        Text("Similar Movies", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(movies, key = { it.id }) { movie ->
                SimilarMovieItem(
                    movie = movie,
                    onMovieClick = onMovieClick,
                )
            }
        }
    }
}

@Composable
fun SimilarMovieItem(
    movie: Movie,
    onMovieClick: (movieId: Int) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .width(130.dp)
                .clickable { onMovieClick(movie.id) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(
                            ImageUrlBuilder.buildPosterUrl(
                                movie.posterPath,
                                ImageUrlBuilder.POSTER_W342,
                            ) ?: "",
                        ).crossfade(true)
                        .build(),
                contentDescription = movie.title,
                modifier =
                    Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp)
                        .padding(8.dp),
                contentAlignment = Alignment.TopStart,
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun SimilarMoviesCreditsSection(groupedContributors: GroupedMovieContributors) {
    Column {
        if (groupedContributors.actors.isNotEmpty()) {
            Text("Top Actors in Similar Movies", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(groupedContributors.actors, key = { "actor_${it.id}" }) { actor ->
                    ContributorItem(contributor = actor)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (groupedContributors.directors.isNotEmpty()) {
            Text("Top Directors in Similar Movies", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(groupedContributors.directors, key = { "director_${it.id}" }) { director ->
                    ContributorItem(contributor = director)
                }
            }
        }
    }
}

@Composable
fun ContributorItem(contributor: MovieContributor) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp),
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(
                        ImageUrlBuilder.buildProfileUrl(
                            contributor.profileImageUrl,
                            ImageUrlBuilder.PROFILE_W185,
                        ),
                    )
                    // .placeholder(R.drawable.placeholder_profile)
                    // .error(R.drawable.error_profile)
                    .build(),
            contentDescription = contributor.name,
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
            // CircleShape for profiles is common
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            contributor.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
