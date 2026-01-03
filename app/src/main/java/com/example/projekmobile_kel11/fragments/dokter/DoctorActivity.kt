package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ActivityDoctorBinding
import com.example.projekmobile_kel11.fragments.dokter.*

class DoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                ?.let { it as androidx.navigation.fragment.NavHostFragment }
                ?.navController

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_dashboard ->
                    navController?.navigate(R.id.dokterDashboardFragment)

                R.id.menu_pasien ->
                    navController?.navigate(R.id.doctorPasienFragment)

                R.id.menu_chat ->
                    navController?.navigate(R.id.fragmentDoctorChatList)

                R.id.menu_schedule ->
                    navController?.navigate(R.id.dokterScheduleFragment)
            }
            true
        }
    }
}
