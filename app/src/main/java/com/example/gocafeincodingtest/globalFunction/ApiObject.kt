package com.example.gocafeincodingtest.globalFunction

import com.example.gocafeincodingtest.BuildConfig
import com.example.gocafeincodingtest.api.MovieAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiObject {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val getRetrofit: Retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getMovieService : MovieAPI by lazy { getRetrofit.create(MovieAPI::class.java) } // 영화 API
}