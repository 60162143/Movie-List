package com.example.gocafeincodingtest.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.gocafeincodingtest.R
import java.util.*

object CustomToast {
    // 토스트 팝업 커스텀
    fun showCustomToast(msg: String, context: Context) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.toast_custom, null)
        val tvText = toastView.findViewById<TextView>(R.id.tv_toast)
        tvText.text = msg

        val toast = Toast(context).apply {
            view = toastView
            setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 48)

        }.apply { show() }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                toast.cancel()
            }
        }, 2000) // 2초 뒤 토스트 종료
    }
}