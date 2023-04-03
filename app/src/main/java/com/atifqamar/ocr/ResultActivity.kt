package com.atifqamar.ocr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    private lateinit var brSpeak : Button
    private lateinit var tvData : TextView
    private var textToSpeechSystem: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        initUI()
    }

    private fun initUI() {
        tvData = findViewById(R.id.tvData)
        brSpeak = findViewById(R.id.brSpeak)
        getData()
    }

    private fun getData() {
        val intent = intent
        val readText = intent.getStringExtra("readText")
        Log.d("MainActivity", "data ${readText}")
        tvData.text = readText
        brSpeak.setOnClickListener {
            textToSpeech(readText)
        }
    }

    fun textToSpeech(readText: String?) {
        textToSpeechSystem = TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechSystem!!.speak(readText, TextToSpeech.QUEUE_ADD, null)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechSystem?.stop()
    }
}