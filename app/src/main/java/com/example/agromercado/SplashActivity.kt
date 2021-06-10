package com.example.agromercado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val backgrounding : ImageView = findViewById(R.id.lechuguita)
        val sideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide)
        backgrounding.startAnimation(sideAnimation)

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, 2200)

    }
}
