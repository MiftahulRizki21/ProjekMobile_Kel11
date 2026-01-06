package com.example.projekmobile_kel11.fragments.dokter

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.databinding.FragmentDashboardDokterBinding
import com.example.projekmobile_kel11.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DokterDashboardFragment : Fragment(R.layout.fragment_dashboard_dokter) {

    private var _binding: FragmentDashboardDokterBinding? = null
    private val binding get() = _binding!!

    private lateinit var doctorId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardDokterBinding.bind(view)

        doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        loadDoctorProfile()
        loadStatistics()
        setupAvailability()

        binding.cardLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    // ===================== PROFIL DOKTER =====================
    private fun loadDoctorProfile() {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(doctorId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                val user = snapshot.getValue(Doctor::class.java) ?: return@addOnSuccessListener

                binding.tvDoctorName.text = user.nama
                binding.tvDoctorSpecialist.text = user.spesialisasi
            }
    }

    // ===================== STATISTIK =====================
    private fun loadStatistics() {
        FirebaseDatabase.getInstance()
            .getReference("consultations")
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .get()
            .addOnSuccessListener { snapshot ->
                val pasienSet = mutableSetOf<String>()
                var konsultasiAktif = 0

                snapshot.children.forEach { data ->
                    val userId = data.child("userId").value?.toString()
                    val status = data.child("status").value?.toString()

                    userId?.let { pasienSet.add(it) }
                    if (status == "ongoing") konsultasiAktif++
                }

                binding.tvTotalPasien.text = pasienSet.size.toString()
                binding.tvKonsultasiAktif.text = konsultasiAktif.toString()
            }
    }

    // ===================== AVAILABILITY =====================
    private fun setupAvailability() {
        val ref = FirebaseDatabase.getInstance()
            .getReference("doctors")
            .child(doctorId)
            .child("available")

        ref.get().addOnSuccessListener {
            binding.switchAvailability.isChecked =
                it.getValue(Boolean::class.java) ?: false
        }

        binding.switchAvailability.setOnCheckedChangeListener { _, isChecked ->
            ref.setValue(isChecked)
        }
    }

    // ===================== LOGOUT =====================
    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                FirebaseDatabase.getInstance()
                    .getReference("doctors")
                    .child(doctorId)
                    .child("available")
                    .setValue(false)

                FirebaseAuth.getInstance().signOut()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
