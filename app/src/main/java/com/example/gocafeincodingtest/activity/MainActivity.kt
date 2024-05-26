package com.example.gocafeincodingtest.activity

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.gocafeincodingtest.BuildConfig
import com.example.gocafeincodingtest.adapter.MovieViewPagerAdapter
import com.example.gocafeincodingtest.data.MovieData
import com.example.gocafeincodingtest.data.MovieResData
import com.example.gocafeincodingtest.databinding.ActivityMainBinding
import com.example.gocafeincodingtest.dialog.LoadingDialog
import com.example.gocafeincodingtest.globalFunction.ApiObject
import com.example.gocafeincodingtest.toast.CustomToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.abs
import android.util.Pair
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding   // 뷰 바인딩

    private lateinit var movieViewPagerAdapter: MovieViewPagerAdapter   // 영화 뷰페이저 어뎁터
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(this) }  // 로딩 다이얼로그

    private var movieList: ArrayList<MovieData> = ArrayList()   // 영화 목록 데이터

    private var schWord = "star" // 검색어
    private var curMoviePage = 1 // 현재 영화 게시글 페이지
    private var totalMovieResults = 0   // 데이터 조회 가능 총 개수

    private var backPressedTime: Long = 0   // 뒤로가기 버튼을 누른 시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){
            // 뷰페이저 어뎁터 설정
            movieViewPagerAdapter = MovieViewPagerAdapter()
            movieViewpager.adapter = movieViewPagerAdapter // 어댑터 생성
            movieViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
            movieViewpager.offscreenPageLimit = 3   // 화면에 보여지는 데이터 개수 제한
            movieViewpager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER    // 스크롤 끝 효과 제거

            // CompositePageTransformer를 사용하여 좌, 우 프리 뷰 설정
            val transform = CompositePageTransformer()
            transform.addTransformer(MarginPageTransformer(30)) // 프리뷰 margine 설정

            // 좌, 우로 스크롤 시 투명도와 크기 변화 설정
            transform.addTransformer(ViewPager2.PageTransformer{ view: View, fl: Float ->
                val v = 1 - abs(fl)
                view.alpha = 0.5f + v * 0.5f
                view.scaleY = 0.8f + v * 0.2f
            })

            movieViewpager.setPageTransformer(transform)    // 설정한 transform 적용

            getMovieData(schWord, curMoviePage)   // 영화 목록 조회

            // 뷰 페이저 화면 변화 리스너
            movieViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {    // 화면의 변화가 완료되었을 때 호출
                    // 현재의 마지막 item 이면서 조회 가능한 데이터가 존재할 경우
                    if(position == movieList.size - 1 && position < totalMovieResults - 1){
                        getMovieData(schWord, curMoviePage)   // 영화 목록 추가 조회
                    }
                }
            })

            // 영화 목록 클릭 리스너
            movieViewPagerAdapter.clickListener = object : MovieViewPagerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String, iv: ImageView) {
                    if(type == "TOTAL"){   // 영화 목록 클릭 시
                        val newList = movieViewPagerAdapter.differ.currentList.toMutableList()    // 영화 데이터

                        // 영화 상세 내용 화면으로 화면 전환
                        val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                        intent.putExtra("imdbID", newList[position].imdbID)  // 영화 ID
                        intent.putExtra("movieTitle", newList[position].title)  // 영화 제목
                        intent.putExtra("moviePoster", newList[position].poster)  // 영화 포스터 이미지

                        // Transition Animation 설정
                        val Pair1 = Pair<View, String> (iv, "movie_poster") // 적용할 컴포넌트의 동일한 transitionName 사용
                        var options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, Pair1)   // Animation을 적용할 컴포넌트 적용

                        startActivity(intent, options.toBundle())   // 화면전환
                    }
                }
            }

            // 검색 버튼 클릭 리스너
            btnSch.setOnClickListener(View.OnClickListener {
                with(binding){
                    if(editSch.text.isNotEmpty()){  // 검색어가 있을 경우
                        // 데이터 초기화
                        schWord = editSch.text.toString()   // 검색어 초기화
                        curMoviePage = 1        // 현재 페이지 초기화
                        totalMovieResults = 0   // 전체 데이터 개수 초기화
                        movieViewPagerAdapter.differ.submitList(null)  // 영화 목록 초기화
                        movieList.clear()   // 영화 목록 초기화

                        editSch.text.clear()    // 입력한 검색어 초기화

                        getMovieData(schWord, curMoviePage)   // 영화 목록 조회
                    }else{  // 검색어가 없을 경우
                        CustomToast.showCustomToast("검색어를 입력해주세요.", this@MainActivity)
                    }
                }
            })

            // 검색어 입력후 키보드의 완료 버튼 클릭 시
            editSch.setOnEditorActionListener { v, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnSch.performClick()   // 클릭 버튼 강제 실행
                    handled = true
                }
                handled
            }
        }
    }

    // 영화 데이터 조회
    private fun getMovieData(searchWord: String, curPage: Int) {
        val apiObjectCall = ApiObject.getMovieService.getMovie(apiKey = BuildConfig.API_KEY
            , s = searchWord, page = curPage, type = "movie")

        loadingDialog.show()    // 로딩 다이얼로그 띄우기

        apiObjectCall.enqueue(object: Callback<MovieResData> {
            override fun onResponse(call: Call<MovieResData>, response: Response<MovieResData>) {
                if(response.isSuccessful) { // 응답 성공 시
                    if(response.body()?.response!!){    // 데이터가 있을경우
                        setLayout(true)    // 레이아웃 SET

                        val newMovieList = response.body()?.search!!  // 새로 페이징된 영화 목록

                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = movieViewPagerAdapter.differ.currentList.toMutableList()

                        if(newList.isNotEmpty()){   // 기존 페이징된 영화 목록이 있을 경우
                            // 기존 페이징된 영화 목록에 새로 페이징된 영화 목록 추가
                            movieViewPagerAdapter.differ.submitList(newList.toMutableList() + newMovieList.toMutableList())
                        }else{  // 기존 페이징된 영화 목록이 없을 경우
                            movieViewPagerAdapter.differ.submitList(newMovieList.toMutableList())  // 새로 페이징된 영화 목록 추가
                        }

                        movieList += newMovieList // 기존 페이징된 영화 목록에 새로 페이징된 영화 목록 추가

                        totalMovieResults = response.body()?.totalResults!!  // 조회 가능한 데이터 개수

                        // 마지막 페이지가 아닐 경우 페이지 +1
                        if(movieList.size < totalMovieResults){
                            curMoviePage += 1 // 다음 페이지 +1
                        }

                    }else{  // 데이터가 없을 경우
                        setLayout(false)    // 레이아웃 SET

                        CustomToast.showCustomToast("조회된 내용이 없습니다.", this@MainActivity)
                    }
                }else{
                    setLayout(false)    // 레이아웃 SET

                    CustomToast.showCustomToast("영화 목록을 불러오지 못했습니다.", this@MainActivity)
                }

                loadingDialog.dismiss()    // 로딩 다이얼로그 닫기
            }

            override fun onFailure(call: Call<MovieResData>, t: Throwable) {
                loadingDialog.dismiss()    // 로딩 다이얼로그 닫기

                setLayout(false)    // 레이아웃 SET

                CustomToast.showCustomToast("인터넷 연결을 확인해 주세요.", this@MainActivity)
            }
        })
    }

    // 화면 터치시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

    // 데이터 유무에 따른 화면 Visible 설정
    private fun setLayout(isVisibleData: Boolean){
        if(isVisibleData){
            binding.layoutVp.visibility = View.VISIBLE    // 영화목록 뷰페이저 보이기
            binding.textEmpty.visibility = View.GONE    // 데이터가 없다는 텍스트 숨기기
        }else{
            binding.layoutVp.visibility = View.GONE    // 영화목록 뷰페이저 숨기기
            binding.textEmpty.visibility = View.VISIBLE    // 데이터가 없다는 텍스트 보이기
        }
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 현재 시간보다 크면 종료
            if(backPressedTime + 2000 > System.currentTimeMillis()){
                finish()    // 액티비티 종료
            }else{
                CustomToast.showCustomToast("한번 더 뒤로가기 버튼을 누르면 종료됩니다.", this@MainActivity)
            }

            // 현재 시간 담기
            backPressedTime = System.currentTimeMillis()
        }
    }
}