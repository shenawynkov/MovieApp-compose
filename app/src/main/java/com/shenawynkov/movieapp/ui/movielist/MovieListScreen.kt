package com.shenawynkov.movieapp.ui.movielist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shenawynkov.movieapp.R
import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.model.MoviesByYear
import com.shenawynkov.movieapp.ui.common.utils.ImageUrlBuilder

@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.statusBars.asPaddingValues()),
            ) {
                SearchBar(
                    currentInputValue = uiState.currentQuery,
                    onQueryChanged = { newQuery ->
                        viewModel.onSearchQueryChanged(newQuery)
                    },
                    onSearchPerformed = {
                        focusManager.clearFocus()
                        viewModel.onSearchSubmitted(uiState.currentQuery.trim())
                    },
                    onClearQuery = {
                        viewModel.onSearchQueryChanged("")
                    },
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            if (uiState.isLoading && uiState.moviesByYear.isEmpty() && uiState.errorMessage == null && uiState.infoMessageResId == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessage?.let {
                ErrorStateView(message = it, onRetry = {
                    if (uiState.isSearchActive) {
                        viewModel.onSearchSubmitted(uiState.currentQuery)
                    } else {
                        viewModel.loadPopularMovies(reset = true)
                    }
                })
            }

            if (!(uiState.isLoading && uiState.moviesByYear.isEmpty()) && uiState.errorMessage == null) {
                MovieListContent(
                    moviesByYearList = uiState.moviesByYear,
                    onMovieClick = onMovieClick,
                    onToggleWatchlist = { movie -> viewModel.toggleWatchlistStatus(movie) },
                    onLoadMore = { viewModel.loadNextPage() },
                    isLoadingNextPage = uiState.isLoadingNextPage,
                    canLoadMore = uiState.canLoadMore,
                    infoMessageResId = uiState.infoMessageResId,
                    infoMessageArg = uiState.infoMessageArg,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    currentInputValue: String,
    onQueryChanged: (String) -> Unit,
    onSearchPerformed: () -> Unit,
    onClearQuery: () -> Unit,
) {
    OutlinedTextField(
        value = currentInputValue,
        onValueChange = onQueryChanged,
        label = { Text(stringResource(R.string.search_bar_label)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_icon_desc)) },
        trailingIcon = {
            if (currentInputValue.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear_search_desc))
                }
            }
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchPerformed() }),
    )
}

@Composable
fun MovieListContent(
    moviesByYearList: List<MoviesByYear>,
    onMovieClick: (Int) -> Unit,
    onToggleWatchlist: (Movie) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingNextPage: Boolean,
    canLoadMore: Boolean,
    infoMessageResId: Int?,
    infoMessageArg: String?,
) {
    if (infoMessageResId != null && moviesByYearList.isEmpty() && !isLoadingNextPage) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            val message =
                if (infoMessageArg != null) {
                    stringResource(infoMessageResId, infoMessageArg)
                } else {
                    stringResource(infoMessageResId)
                }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
            )
        }
        return
    }

    if (moviesByYearList.isEmpty() && !isLoadingNextPage) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.movies_empty_generic),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        moviesByYearList.forEach { moviesByYear ->
            item(key = "header_${moviesByYear.year}") {
                Text(
                    text = moviesByYear.year,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                )
            }
            itemsIndexed(
                items = moviesByYear.movies,
                key = { _, movie -> "movie_${movie.id}" },
            ) { index, movie ->
                MovieListItem(
                    movie = movie,
                    onMovieClick = onMovieClick,
                    onToggleWatchlist = onToggleWatchlist,
                )
                val isLastGroup = moviesByYearList.indexOf(moviesByYear) == moviesByYearList.size - 1
                val isNearEndOfGroup = index >= moviesByYear.movies.size - 3

                if (isLastGroup && isNearEndOfGroup && canLoadMore && !isLoadingNextPage) {
                    LaunchedEffect(key1 = movie.id) {
                        onLoadMore()
                    }
                }
            }
        }

        if (isLoadingNextPage) {
            item(key = "loading_indicator_bottom") {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun MovieListItem(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    onToggleWatchlist: (Movie) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onMovieClick(movie.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(ImageUrlBuilder.buildPosterUrl(movie.posterPath) ?: "")
                        .crossfade(true)
                        .build(),
                contentDescription = movie.title,
                modifier =
                    Modifier
                        .width(90.dp)
                        .height(135.dp)
                        .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    movie.overview ?: stringResource(R.string.overview_not_available),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { onToggleWatchlist(movie) }) {
                Icon(
                    imageVector = if (movie.isInWatchlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription =
                        if (movie.isInWatchlist) {
                            stringResource(
                                R.string.remove_from_watchlist,
                            )
                        } else {
                            stringResource(R.string.add_to_watchlist)
                        },
                    tint = if (movie.isInWatchlist) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                )
            }
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 12.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry_button))
            }
        }
    }
} 
