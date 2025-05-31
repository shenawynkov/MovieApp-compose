package com.shenawynkov.movieapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shenawynkov.movieapp.ui.details.MOVIE_ID_SAVED_STATE_KEY
import com.shenawynkov.movieapp.ui.details.MovieDetailScreen
// MovieDetailViewModel is not directly used here, but good to keep for context if needed later
// import com.shenawynkov.movieapp.ui.details.MovieDetailViewModel
import com.shenawynkov.movieapp.ui.movielist.MovieListScreen

sealed class Screen(
    val route: String,
) {
    data object MovieList : Screen("movie_list_screen") // More descriptive route

    data object MovieDetail : Screen("movie_detail_screen/{${MOVIE_ID_SAVED_STATE_KEY}}") {
        fun createRoute(movieId: Int) = "movie_detail_screen/$movieId"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.MovieList.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.MovieList.route) {
            MovieListScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                },
            )
        }
        composable(
            route = Screen.MovieDetail.route,
            arguments =
                listOf(
                    navArgument(MOVIE_ID_SAVED_STATE_KEY) {
                        type = NavType.IntType
                    },
                ),
        ) {
            // MovieDetailViewModel will get movieId from SavedStateHandle
            MovieDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieItemClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                },
            )
        }
    }
}
