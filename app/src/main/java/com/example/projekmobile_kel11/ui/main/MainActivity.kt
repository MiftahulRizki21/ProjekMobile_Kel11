package com.example.projekmobile_kel11.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ActivityMainBinding
import com.example.projekmobile_kel11.ui.prediction.PredictionFormActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.projekmobile_kel11.ui.main.HomeFragment
import com.example.projekmobile_kel11.ui.profile.ProfileFragment
import com.example.projekmobile_kel11.ui.education.EducationFragment
import com.example.projekmobile_kel11.ui.consultation.ConsultationFragment
import com.example.projekmobile_kel11.ui.clinic.ClinicLocationFragment
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }


        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // Load HomeFragment pertama kali
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.navView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> loadFragment(HomeFragment())
            R.id.nav_prediction -> {
                startActivity(Intent(this, PredictionFormActivity::class.java))
            }
            R.id.nav_profile -> loadFragment(ProfileFragment())
            R.id.nav_education -> loadFragment(EducationFragment())
            R.id.nav_consultation -> loadFragment(ConsultationFragment())
            R.id.nav_clinic -> loadFragment(ClinicLocationFragment())
            R.id.nav_logout -> {
                // TODO: Implementasi logout
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "REMINDER_CHANNEL",
                "Reminder Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }


}
