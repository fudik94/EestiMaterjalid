package com.example.merjumaterjalid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    // длительность заставки в миллисекундах
    private val splashDelayMs = 2500L // 2.5 секунды — поменяй при желании

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Запуск задержки в lifecycleScope (корутины)
        lifecycleScope.launch {
            delay(splashDelayMs)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
