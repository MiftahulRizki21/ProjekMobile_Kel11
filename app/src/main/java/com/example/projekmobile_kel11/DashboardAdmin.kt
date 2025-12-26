package com.example.projekmobile_kel11

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.ActivityDashboardAdminBinding
import com.example.projekmobile_kel11.fragments.DashboardFragment
import com.example.projekmobile_kel11.fragments.KelolaDokterFragment
import com.example.projekmobile_kel11.fragments.KelolaReminderFragment

class DashboardAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_kelola_user -> KelolaUserFragment()
                R.id.nav_kelola_dokter -> KelolaDokterFragment()
                R.id.nav_kelola_reminder -> KelolaReminderFragment()
                else -> DashboardFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()

            true
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
        }
    }
}
