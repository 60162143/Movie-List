package com.example.gocafeincodingtest.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.gocafeincodingtest.BuildConfig
import com.example.gocafeincodingtest.data.MovieDetailData
import com.example.gocafeincodingtest.databinding.ActiMovieDetailBinding
import com.example.gocafeincodingtest.dialog.LoadingDialog
import com.example.gocafeincodingtest.globalFunction.ApiObject
import com.example.gocafeincodingtest.toast.CustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActiMovieDetailBinding    // 뷰 바인딩

    private var imdbID = "" // 영화 ID
    private var title = "" // 영화 제목
    private var poster = "" // 영화 포스터

    private lateinit var movieDetailData: MovieDetailData   // 영화 상세 정보 데이터

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }  // 로딩 다이얼로그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            imdbID = intent.getStringExtra("imdbID")!!  // 영화 ID
            title = intent.getStringExtra("movieTitle")!!   // 영화 제목
            poster = intent.getStringExtra("moviePoster")!! // 영화 포스터

            // 데이터 SET
            movieTitle.text = title // 영화 제목 SET

            Glide.with(this@MovieDetailActivity)    // 영화 포스터 SET
                .load(poster)
                .into(moviePoster)

            // 영화 상세 내용 조회
            getMovieData(imdbID)

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                onBackPressedDispatcher.onBackPressed() // 뒤로가기 호출
            })
        }
    }

    // 영화 상세 내용 조회
    private fun getMovieData(imdbID: String) {
        val apiObjectCall = ApiObject.getMovieService.getMovieDetail(apiKey = BuildConfig.API_KEY, i = imdbID)

        loadingDialog.show()    // 로딩 다이얼로그 띄우기

        apiObjectCall.enqueue(object: Callback<MovieDetailData> {
            override fun onResponse(call: Call<MovieDetailData>, response: Response<MovieDetailData>) {

                if(response.isSuccessful) { // 응답 성공 시
                    if(response.body()?.response!!){    // 데이터가 있을경우
                        movieDetailData = response.body()!!

                        with(binding){
                            // 기본 정보
                            movieReleased.text = movieDetailData.released   // 영화 개봉 일자
                            movieRated.text = movieDetailData.rated // 영화 등급
                            movieRuntime.text = movieDetailData.runtime // 영화 상영 시간
                            movieGenre.text = movieDetailData.genre // 영화 장르
                            movieLanguage.text = movieDetailData.language   // 영화 언어
                            movieCountry.text = movieDetailData.country // 영화 국가
                            movieProduction.text = movieDetailData.production   // 영화 배급사
                            movieWebsite.text = movieDetailData.website // 영화 웹사이트
                            movieDvd.text = movieDetailData.dvd // 영화 DVD
                            movieBoxoffice.text = movieDetailData.boxOffice // 영화 박스오피스
                            movieAwards.text = movieDetailData.awards // 영화 수상

                            // 줄거리
                            moviePlot.text = movieDetailData.plot // 영화 줄거리

                            // 감독/출연
                            movieDirector.text = movieDetailData.director // 영화 감독
                            movieWriter.text = movieDetailData.writer   // 영화 작가
                            movieActors.text = movieDetailData.actors // 영화 배우

                            // 평점
                            if(movieDetailData.ratings.isNotEmpty()){   // 평점 정보가 있을 경우
                                var ratingStr = ""

                                // 평점을 "\n"로 구분해서 출력하기 위함
                                for(i in 0 until movieDetailData.ratings.size){
                                    val movieDetailRatingData = movieDetailData.ratings[i]  // 평점 데이터

                                    // 첫줄을 제외한 나머지 줄의 맨 앞에 "\n"을 더해서 저장
                                    ratingStr += if(i == 0){
                                        movieDetailRatingData.source + " : " + movieDetailRatingData.value
                                    }else{
                                        "\n" + movieDetailRatingData.source + " : " + movieDetailRatingData.value
                                    }

                                }

                                movieRatings.text = ratingStr // 평점
                            }
                        }
                    }else{  // 데이터가 없을 경우
                        CustomToast.showCustomToast("영화 상세 정보를 불러오지 못했습니다.", this@MovieDetailActivity)
                    }

                }else{
                    CustomToast.showCustomToast("영화 상세 정보를 불러오지 못했습니다.", this@MovieDetailActivity)
                }

                loadingDialog.dismiss()    // 로딩 다이얼로그 닫기
            }

            override fun onFailure(call: Call<MovieDetailData>, t: Throwable) {
                loadingDialog.dismiss()    // 로딩 다이얼로그 닫기

                CustomToast.showCustomToast("인터넷 연결을 확인해 주세요.", this@MovieDetailActivity)
            }
        })
    }
}