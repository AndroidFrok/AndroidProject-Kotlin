package com.jh.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ComponentActivity
import com.hjq.demo.ui.activity.SplashActivity

@Deprecated("仅作为中转")
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, SplashActivity::class.java))
        finish()/*setContent {
            AndroidProjectKotlinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android23")
                }
            }
        }*/
    }
}



