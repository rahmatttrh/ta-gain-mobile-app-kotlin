package com.example.ta_gain.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import com.example.ta_gain.R
import com.example.ta_gain.helper.SharedPref
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    lateinit var handler: Handler
    private lateinit var s:SharedPref




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        s = SharedPref(this)

        val alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha)
        splashLogo.startAnimation(alphaAnim)

        if (s.getStatusLogin()){
            handler = Handler()
            handler.postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 4000)
        } else {
            handler = Handler()
            handler.postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 4000)
        }

    }
}