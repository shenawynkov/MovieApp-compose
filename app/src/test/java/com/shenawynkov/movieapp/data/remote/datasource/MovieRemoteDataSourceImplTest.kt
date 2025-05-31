package com.shenawynkov.movieapp.data.remote.datasource

import com.shenawynkov.movieapp.data.remote.api.MovieApiService
import com.shenawynkov.movieapp.data.remote.dto.MovieCreditsDto
import com.shenawynkov.movieapp.data.remote.dto.MoviesListResponse
import com.shenawynkov.movieapp.utils.data.MovieError
import com.shenawynkov.movieapp.utils.data.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MovieRemoteDataSourceImplTest {
    @Mock
    private lateinit var apiService: MovieApiService

    private lateinit var movieRemoteDataSourceImpl: MovieRemoteDataSourceImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        movieRemoteDataSourceImpl = MovieRemoteDataSourceImpl(apiService, testDispatcher)
    }

    @Test
    fun getPopularMoviesReturnsSuccessWhenApiCallSucceeds() =
        testScope.runTest {
            val mockResponse = MoviesListResponse(1, emptyList())
            whenever(apiService.getPopularMovies(page = 1)).thenReturn(mockResponse)

            val result = movieRemoteDataSourceImpl.getPopularMovies(1)

            assertTrue(result is Result.Success)
            assertEquals(mockResponse, (result as Result.Success).data)
        }

    @Test
    fun getPopularMoviesReturnsErrorWhenApiCallFails() =
        testScope.runTest {
            whenever(apiService.getPopularMovies(page = 1)).thenThrow(RuntimeException())

            val result = movieRemoteDataSourceImpl.getPopularMovies(1)

            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).exception.cause is RuntimeException)
        }

    @Test
    fun searchMoviesReturnsErrorWhenQueryIsBlank() =
        testScope.runTest {
            val result = movieRemoteDataSourceImpl.searchMovies("", 1)

            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).exception is MovieError.ApiValidationProblem)
        }

    @Test
    fun searchMoviesReturnsSuccessWhenApiCallSucceeds() =
        testScope.runTest {
            val query = "test"
            val mockResponse = MoviesListResponse(1, emptyList())
            whenever(apiService.searchMovies(query = query, page = 1)).thenReturn(mockResponse)

            val result = movieRemoteDataSourceImpl.searchMovies(query, 1)

            assertTrue(result is Result.Success)
            assertEquals(mockResponse, (result as Result.Success).data)
        }

    @Test
    fun getMovieDetailsReturnsErrorWhenMovieIdIsInvalid() =
        testScope.runTest {
            val result = movieRemoteDataSourceImpl.getMovieDetails(-1)

            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).exception is MovieError.ApiValidationProblem)
        }

    @Test
    fun getSimilarMoviesReturnsSuccessWhenApiCallSucceeds() =
        testScope.runTest {
            val mockResponse = MoviesListResponse(1, emptyList())
            whenever(apiService.getSimilarMovies(movieId = 1, page = 1)).thenReturn(mockResponse)

            val result = movieRemoteDataSourceImpl.getSimilarMovies(1, 1)

            assertTrue(result is Result.Success)
            assertEquals(mockResponse, (result as Result.Success).data)
        }

    @Test
    fun getMovieCreditsReturnsSuccessWhenApiCallSucceeds() =
        testScope.runTest {
            val mockResponse = MovieCreditsDto(emptyList(), emptyList())
            whenever(apiService.getMovieCredits(movieId = 1)).thenReturn(mockResponse)

            val result = movieRemoteDataSourceImpl.getMovieCredits(1)

            assertTrue(result is Result.Success)
            assertEquals(mockResponse, (result as Result.Success).data)
        }
}
