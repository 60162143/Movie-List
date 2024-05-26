package com.example.gocafeincodingtest.data

import com.google.gson.annotations.SerializedName

data class MovieData(
    @SerializedName("Title") var title: String = "",    // 영화 제목
    @SerializedName("Year") var year: String = "",      // 영화 개봉 연도
    @SerializedName("imdbID") var imdbID: String = "",  // 영화 아이디
    @SerializedName("Type") var type: String = "",      // 영화 타입
    @SerializedName("Poster") var poster: String = ""   // 영화 포스터
)
