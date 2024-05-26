# **Movie App With Open API.**

## **📝 프로젝트 개요**

> **프로젝트:** 오픈 API를 이용한 영화 앱
>
> **기획 및 제작:** 오태근
>
> **분류:** 개인 모바일 프로젝트 (Android Kotlin Ver.)
>
> **제작 기간:** 24.05.25 ~ 24.05.26
>
> **주요 기능:**
  - **검색어를 이용한 영화 목록 조회**
  
  - **영화 상세 내용 조회**
>
> **문의:** no2955922@naver.com

<br />

## **🛠 기술 및 도구**
- **Framework :**
  <img align="center" src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white">
- **Language :**
  &nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white">
- **SCM :**
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Github-181717?style=flat&logo=github&logoColor=white">
- **Build :**
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white">
<br />

## **👨🏻‍💻 기능 구현(각 기능에 대한 이미지는 'View 접기/펼치기 버튼을 클릭하여 확인하실 수 있습니다.)**



### **1. 구현 화면**
***
  <details>
  <summary>View 접기/펼치기</summary>

  <img width="300" height="600" alt="메인 화면" src="https://github.com/60162143/The_Clap/assets/33407087/613482fd-31bf-441c-8265-57882ed61b92" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="검색 화면" src="https://github.com/60162143/The_Clap/assets/33407087/862ad838-eb64-45b0-8a2a-6a1555f81b75" />  &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="상 화면" src="https://github.com/60162143/The_Clap/assets/33407087/c5335b5f-56c9-4f2e-a6bf-d4e259ef41ec" />
  
  </div>
  </details>

- **Open API**를 활용한 데이터 조회 기능 구현
  
- **상단 검색어 입력후 조회** 기능 구현
- **Viewpager2**를 활용한 데이터 미리보기 기능 구현
- **Progress Dialog**를 이용한 로딩 처리 기능 구현
- **Activity Options**를 활용한 Transition Animation 구현

<br />

### **2. 기타**

  - #### **1. 사용 라이브러리**

    - **Glide Library** : 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
      <details>
      <summary>적용 코드 접기/펼치기</summary>
      
      ```kotlin
          // Glide Library
          implementation 'com.github.bumptech.glide:glide:4.12.0'
    
          // 영화 포스터
              Glide.with(vpMoviePoster.context)
                  .load(movieData.poster)
                  .into(vpMoviePoster)
      ```
      
      </div>
      </details>
      <br>

    - **Retrofit2 Library** : API 통신을 위해 구현된 OkHTTP의 HTTP 통신을 간편하게 만들어주는 라이브러리를 뜻함, Async Task가 없이 Background 쓰레드를 실행 -> CallBack을 통하여 Main Thread에서 UI를 업데이트, 동일 Squareup사의 OkHttp 라이브러리의 상위 구현체
      <details>
      <summary>적용 코드 접기/펼치기</summary>
   
      ```kotlin
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
      ```
   
      ```kotlin
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
      ```
      
      ```kotlin
        // Retrofit2 Library
        implementation 'com.squareup.retrofit2:retrofit:2.9.0'
        implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
      
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
      ```
      
      </div>
      </details>
      <br />
