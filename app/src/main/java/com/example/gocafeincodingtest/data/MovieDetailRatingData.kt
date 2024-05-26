package com.example.gocafeincodingtest.data

import com.google.gson.annotations.SerializedName

data class MovieDetailRatingData(
    @SerializedName("Source") var source: String = "",  // 평점 기관
    @SerializedName("Value") var value: String = "" // 평점
)
