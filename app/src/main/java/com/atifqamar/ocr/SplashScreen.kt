package com.atifqamar.ocr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler


class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initUI()
    }

    private fun initUI() {
        //Splash Screen duration
        val secondsDelayed = 1
        Handler().postDelayed(Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, (secondsDelayed * 3000).toLong())
    }
}