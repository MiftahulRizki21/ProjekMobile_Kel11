package com.example.projekmobile_kel11.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ActivityMainBinding
import com.example.projekmobile_kel11.fragments.user.UserScheduleFragment
import com.example.projekmobile_kel11.ui.consultation.ConsultationFragment
import com.example.projekmobile_kel11.ui.consultation.ScheduleActivity
import com.example.projekmobile_kel11.ui.education.EducationFragment
import com.example.projekmobile_kel11.ui.prediction.PredictionFormFragment
import com.example.projekmobile_kel11.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomNav.selectedItemId = R.id.nav_home
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_education -> loadFragment(EducationFragment())
                R.id.nav_consultation -> loadFragment(ConsultationFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                R.id.nav_prediction -> loadFragment(PredictionFormFragment())
                R.id.nav_schedule -> loadFragment(UserScheduleFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // 🔹 Fungsi publik untuk show/hide bottom nav
    fun showBottomNav() {
        binding.bottomNav.visibility = android.view.View.VISIBLE
    }

    fun hideBottomNav() {
        binding.bottomNav.visibility = android.view.View.GONE
    }
}
