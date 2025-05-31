# MovieApp - Android Clean Architecture Showcase

## Overview

MovieApp is an Android application built to demonstrate modern Android development practices, including Clean Architecture, MVVM (with MVI-like reactive state management), and a comprehensive suite of Jetpack libraries. The app allows users to discover popular movies, search for specific titles, manage a personal watchlist, and view detailed information about movies, including cast, crew, and similar recommendations. It fetches data from [TheMovieDB (TMDB) API](https://www.themoviedb.org/documentation/api).

## Architecture

The project follows the principles of Clean Architecture, separating concerns into three main layers:

*   **Domain Layer:** Contains the core business logic, use cases (e.g., `GetPopularMoviesUseCase`, `SearchMoviesUseCase`, `ToggleWatchlistUseCase`), and domain models. It is independent of the Android framework and other layers.
*   **Data Layer:** Responsible for providing data to the Domain layer. It includes repository implementations (`MovieRepositoryImpl`), remote data sources (using Retrofit for TMDB API calls), and local data sources (using Room for managing watchlist movie IDs). It handles DTO-to-domain model mapping.
*   **Presentation (UI) Layer:** Built with Jetpack Compose for the user interface. It uses ViewModels (`MovieListViewModel`, `MovieDetailViewModel`) to manage UI state (exposed via Kotlin `StateFlow`) and handle user interactions. ViewModels interact with the Domain layer via Use Cases. Error messages are localized using string resources.

Dependency Injection is managed throughout the application using Hilt.

## Core Technologies & Libraries

*   **Kotlin:** Primary programming language.
*   **Jetpack Compose:** For building the declarative UI.
*   **Hilt:** For dependency injection.
*   **ViewModel:** For managing UI-related data in a lifecycle-conscious way.
*   **StateFlow & Coroutines:** For asynchronous programming and managing reactive UI state.
*   **Retrofit & OkHttp:** For networking and consuming TheMovieDB API. Includes OkHttp caching.
*   **Room:** For local persistence of watchlist movie IDs.
*   **Jetpack Navigation Compose:** For navigating between screens.
*   **Material 3:** For UI components and theming (supports light/dark/dynamic color).
*   **Clean Architecture:** Guiding architectural pattern.
*   **MVVM:** Architectural pattern for the presentation layer, with reactive state management inspired by MVI principles.

## Key Features

*   **Movie List:**
    *   Display popular movies with pagination.
    *   Search movies with debounced input.
    *   Group movies by release year.
    *   Add/remove movies from a local watchlist.
    *   Loading and error states for a smooth user experience.
*   **Movie Detail Screen:**
    *   Display detailed movie information (overview, release date, rating, etc.).
    *   Show a list of similar movies (clickable to navigate to their details).
    *   Display top cast and crew members for similar movies.
    *   Independent loading and error states for details, similar movies, and credits sections.
    *   Toggle watchlist status for the detailed movie.
*   **UI & UX:**
    *   Modern UI built with Jetpack Compose and Material 3.
    *   Support for Light, Dark, and Dynamic Theming.
    *   Centralized image URL construction.
    *   User-friendly and localized error messages.
*   **Code Quality:**
    *   SOLID principles applied (e.g., Single Responsibility, Dependency Inversion).
    *   Use Cases for business logic encapsulation.
    *   Clear separation of concerns between layers.
    *   Minification enabled for release builds.
    *   Tests for `MovieRemoteDataSourceImpl` covering success and error scenarios


## Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **API Key:** You will need an API key from [TheMovieDB](https://www.themoviedb.org/settings/api).
    *   Create a file named `apikey.properties` in the root directory of the project (the same level as `build.gradle.kts` and `gradle.properties`).
    *   Add your TMDB API key to this file like so:
        ```properties
        TMDB_API_KEY="YOUR_API_KEY_HERE"
        ```
3.  **Open in Android Studio:** Open the project in the latest stable version of Android Studio.
4.  **Build and Run:** Let Gradle sync and then build and run the application on an emulator or a physical device.

## Further Development & Potential Enhancements
*   Improve handling of grouped pagination to address movie list grouping issues.
*   More sophisticated filtering and sorting options for movie lists.
*   Actor/Crew detail screens.
*   Offline caching strategy for movie details and images beyond OkHttp's HTTP cache.
*   Unit and E2E tests.
