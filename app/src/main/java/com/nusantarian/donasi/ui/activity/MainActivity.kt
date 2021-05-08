package com.nusantarian.donasi.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.ActivitySplashBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}