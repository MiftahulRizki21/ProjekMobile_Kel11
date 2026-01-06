package com.example.projekmobile_kel11.fragments.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.FragmentDashboardAdminBinding
import com.example.projekmobile_kel11.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class FragmentDashboard : Fragment(R.layout.fragment_dashboard_admin) {

    private lateinit var binding: FragmentDashboardAdminBinding
    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardAdminBinding.bind(view)

        setDate()
        loadStatistics()
        setupAction()
    }

    private fun setDate() {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id"))
        binding.tvDate.text = sdf.format(Date())
    }

    private fun loadStatistics() {

        // TOTAL USER (PASlEN)
        db.child("users").get().addOnSuccessListener { snapshot ->
            var userCount = 0
            var doctorCount = 0

            for (data in snapshot.children) {
                val role = data.child("role").getValue(String::class.java)

                if (role == "user") userCount++
                if (role == "doctor") doctorCount++
            }

            binding.tvUserCount.text = userCount.toString()
            binding.tvDoctorCount.text = doctorCount.toString()
        }

        // TOTAL KONSULTASI
        db.child("consultations").get().addOnSuccessListener {
            binding.tvConsultationCount.text = it.childrenCount.toString()
        }
    }


    private fun setupAction() {
        binding.cardKelolaDokter.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, KelolaDokterFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.cardLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}
