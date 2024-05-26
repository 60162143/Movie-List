package com.example.gocafeincodingtest.api

import com.example.gocafeincodingtest.data.MovieDetailData
import com.example.gocafeincodingtest.data.MovieResData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieAPI {
    // 영화 목록 조회
    @GET(".")
    fun getMovie(
        @Query("apiKey") apiKey: String,    // API Key
        @Query("s") s: String,              // 검색어
        @Query("page") page: Int,           // 검색 페이지
        @Query("type") type: String = "movie"   // 검색 Type
    ): Call<MovieResData>

    // 영화 상세 내용 조회
    @GET(".")
    fun getMovieDetail(
        @Query("apiKey") apiKey: String,    // API Key
        @Query("i") i: String               // 영화 ID
    ): Call<MovieDetailData>
}