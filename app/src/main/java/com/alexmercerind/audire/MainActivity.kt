package com.alexmercerind.audire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alexmercerind.audire.native.ShazamSignature

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("audire", ShazamSignature().create(ShortArray(16000 * 12)))
    }
}