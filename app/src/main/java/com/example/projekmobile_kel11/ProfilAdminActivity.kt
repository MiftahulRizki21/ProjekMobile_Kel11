package com.example.projekmobile_kel11 // Sesuaikan dengan package Anda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.ActivityProfilAdminBinding

class ProfilAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set listener untuk item yang dipilih di bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            // Logika untuk mengganti fragment berdasarkan menu yang diklik
            when (item.itemId) {
                R.id.nav_kelola_user -> {
                    // TODO: Buat KelolaUserFragment() dan hapus komentar di bawah
                    // selectedFragment = KelolaUserFragment()
                }
                R.id.nav_kelola_dokter -> {
                    // TODO: Buat KelolaDokterFragment()
                    // selectedFragment = KelolaDokterFragment()
                }
                R.id.nav_kelola_reminder -> {
                    // TODO: Buat KelolaReminderFragment()
                    // selectedFragment = KelolaReminderFragment()
                }
                R.id.nav_profil -> {
                    // TODO: Buat ProfilFragment()
                    // selectedFragment = ProfilFragment()
                }
            }

            // Jika fragment dipilih, ganti konten di FrameLayout
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        // Menampilkan halaman default (misalnya Kelola User) saat Activity pertama kali dibuka
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_kelola_user
        }
    }
}
