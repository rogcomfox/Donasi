package com.nusantarian.donasi.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nusantarian.donasi.databinding.ActivitySplashBinding
import com.nusantarian.donasi.ui.fragment.landing.LoginFragment

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //test
        //FirebaseAuth.getInstance().signOut()

        val user = FirebaseAuth.getInstance().currentUser


        Handler(Looper.getMainLooper()).postDelayed({
            if (user != null)
                if (LoginFragment.ADMIN_UID.contains(user.uid))
                    startActivity(Intent(this, AdminMainActivity::class.java))
                else
                    startActivity(Intent(this, MainActivity::class.java))
            else
                startActivity(Intent(this, LandingActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2000)

    }
}