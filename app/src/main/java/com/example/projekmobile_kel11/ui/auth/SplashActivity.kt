package com.example.projekmobile_kel11.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobile_kel11.DashboardAdmin
import com.example.projekmobile_kel11.databinding.ActivitySplashBinding
import com.example.projekmobile_kel11.fragments.dokter.DoctorActivity
import com.example.projekmobile_kel11.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            checkLogin()
        }, 2000)
    }

    private fun checkLogin() {
        val user = auth.currentUser

        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val uid = user.uid
            val db = FirebaseDatabase.getInstance().reference

            db.child("users").child(uid).child("role").get()
                .addOnSuccessListener {
                    when (it.value.toString()) {
                        "admin" -> startActivity(Intent(this, DashboardAdmin::class.java))
                        "doctor" -> startActivity(Intent(this, DoctorActivity::class.java))
                        "user" -> startActivity(Intent(this, MainActivity::class.java))
                        else -> startActivity(Intent(this, LoginActivity::class.java))
                    }
                    finish()
                }
        }
    }
}
