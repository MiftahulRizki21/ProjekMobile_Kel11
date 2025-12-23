package com.example.projekmobile_kel11

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.ActivityDashboardAdminBinding
import com.example.projekmobile_kel11.fragments.DashboardFragment
import com.example.projekmobile_kel11.fragments.FragmentKelolaDokter
import com.example.projekmobile_kel11.fragments.KelolaReminderFragment
import com.example.projekmobile_kel11.KelolaUserFragment

class DashboardAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. WAJIB: Panggil super.onCreate() terlebih dahulu.
        super.onCreate(savedInstanceState)

        // 2. Inisialisasi binding.
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)

        // 3. Set content view dari binding.
        setContentView(binding.root)

        // 4. SEKARANG aman untuk menggunakan binding.
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    selectedFragment = DashboardFragment()
                }
                R.id.nav_kelola_user -> {
                    selectedFragment = KelolaUserFragment()
                }
                R.id.nav_kelola_dokter -> {
                    selectedFragment = FragmentKelolaDokter()
                }
                R.id.nav_kelola_reminder -> {
                    selectedFragment = KelolaReminderFragment()
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        // Menampilkan halaman default saat Activity pertama kali dibuka
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
        }
    }
}
