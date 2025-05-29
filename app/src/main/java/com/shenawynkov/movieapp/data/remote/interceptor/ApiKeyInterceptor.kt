package com.shenawynkov.movieapp.data.remote.interceptor

import com.shenawynkov.movieapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor
    @Inject
    constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            // Only add API key if not already present
            val urlWithApiKey =
                if (originalUrl.queryParameter("api_key") == null) {
                    originalUrl
                        .newBuilder()
                        .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                        .build()
                } else {
                    originalUrl
                }

            val newRequest =
                originalRequest
                    .newBuilder()
                    .url(urlWithApiKey)
                    .build()

            return chain.proceed(newRequest)
        }
    }
