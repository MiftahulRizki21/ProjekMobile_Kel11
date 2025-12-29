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

        // default fragment
        loadFragment(DokterDashboardFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_dashboard -> loadFragment(DokterDashboardFragment())
                R.id.menu_pasien -> loadFragment(DoctorPasienFragment())
                R.id.menu_chat -> loadFragment(FragmentDoctorChatList())
            }
            true
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.doctor_container, fragment)
            .commit()
    }
}
