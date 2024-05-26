package com.example.gocafeincodingtest.data

import com.google.gson.annotations.SerializedName

data class MovieDetailData(
    @SerializedName("Title") var title: String = "",    // 영화 제목
    @SerializedName("Year") var year: String = "",      // 영화 개봉 연도
    @SerializedName("Rated") var rated: String = "",    // 영화 등급
    @SerializedName("Released") var released: String = "",  // 영화 개봉 일자
    @SerializedName("Runtime") var runtime: String = "",    // 영화 상영 시간
    @SerializedName("Genre") var genre: String = "",        // 영화 장르
    @SerializedName("Director") var director: String = "",  // 영화 감독
    @SerializedName("Writer") var writer: String = "",      // 영화 작가
    @SerializedName("Actors") var actors: String = "",      // 영화 배우
    @SerializedName("Plot") var plot: String = "",          // 영화 줄거리
    @SerializedName("Language") var language: String = "",  // 영화 언어
    @SerializedName("Country") var country: String = "",    // 영화 국가
    @SerializedName("Awards") var awards: String = "",      // 영화 수상 내역
    @SerializedName("Poster") var poster: String = "",      // 영화 포스터
    @SerializedName("Ratings") var ratings: ArrayList<MovieDetailRatingData> = ArrayList(), // 영화 평점
    @SerializedName("Metascore") var metascore: String = "",    // 영화 평점
    @SerializedName("imdbRating") var imdbRating: String = "",  // 영화 평점
    @SerializedName("imdbVotes") var imdbVotes: String = "",    // 영화 평점을 진행한 수
    @SerializedName("imdbID") var imdbID: String = "",      // 영화 아이디
    @SerializedName("Type") var type: String = "",      // 영화 타입
    @SerializedName("DVD") var dvd: String = "",        // 영화 DVD
    @SerializedName("BoxOffice") var boxOffice: String = "",    // 영화 박스오피스 결과
    @SerializedName("Production") var production: String = "",  // 영화 배급사
    @SerializedName("Website") var website: String = "",    // 영화 웹사이트
    @SerializedName("Response") var response: Boolean   // 응답 여부
)
