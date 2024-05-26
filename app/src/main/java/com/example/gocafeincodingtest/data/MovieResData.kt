package com.example.gocafeincodingtest.data

import com.google.gson.annotations.SerializedName

data class MovieResData(
    @SerializedName("Search") val search: ArrayList<MovieData>, // 검색 내용
    @SerializedName("totalResults") val totalResults: Int = 0,  // 조회 가능한 총 데이터 수
    @SerializedName("Response") val response: Boolean,  // 응답 결과
    @SerializedName("Error") val error: String  // 응답 에러 메시지
)
