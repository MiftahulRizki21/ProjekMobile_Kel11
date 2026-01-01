package com.example.projekmobile_kel11.fragments.dokter

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.databinding.FragmentDashboardDokterBinding
import com.example.projekmobile_kel11.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DokterDashboardFragment : Fragment() {

    private var _binding: FragmentDashboardDokterBinding? = null
    private val binding get() = _binding!!

    private lateinit var doctorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // ⬅️ AKTIFKAN MENU LOGOUT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        loadDoctorProfile()
        loadStatistics()
        setupAvailability()
    }

    // ===================== PROFIL DOKTER =====================
    private fun loadDoctorProfile() {
        FirebaseDatabase.getInstance()
            .getReference("doctors")
            .child(doctorId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) return@addOnSuccessListener

                val doctor = snapshot.getValue(Doctor::class.java) ?: return@addOnSuccessListener

                binding.tvDoctorName.text = doctor.nama
                binding.tvDoctorSpecialist.text = doctor.spesialisasi

                Glide.with(requireContext())
                    .load(doctor.fotoUrl)
                    .placeholder(R.drawable.ic_doctor_default)
                    .error(R.drawable.ic_doctor_default)
                    .into(binding.imgDoctor)
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

    // ===================== MENU LOGOUT =====================
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_doctor_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
