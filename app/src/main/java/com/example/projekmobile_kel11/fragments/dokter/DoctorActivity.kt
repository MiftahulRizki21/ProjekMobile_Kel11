package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavOptions
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ActivityDoctorBinding
import com.example.projekmobile_kel11.fragments.dokter.*

class DoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(0, 0, 0, bottomInset)
            insets
        }

        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                ?.let { it as androidx.navigation.fragment.NavHostFragment }
                ?.navController

        binding.bottomNav.setOnItemSelectedListener {
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.nav_graph, false)
                .build()

            when (it.itemId) {
                R.id.menu_dashboard ->
                    navController?.navigate(R.id.dokterDashboardFragment, null, options)

                R.id.menu_pasien ->
                    navController?.navigate(R.id.doctorPasienFragment, null, options)

                R.id.menu_chat ->
                    navController?.navigate(R.id.fragmentDoctorChatList, null, options)

                R.id.menu_schedule ->
                    navController?.navigate(R.id.dokterScheduleFragment, null, options)
            }
            true
        }

    }
}
