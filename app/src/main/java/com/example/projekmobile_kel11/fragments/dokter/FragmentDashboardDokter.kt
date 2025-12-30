package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.databinding.FragmentDashboardDokterBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.projekmobile_kel11.R

class DokterDashboardFragment : Fragment() {

    private var _binding: FragmentDashboardDokterBinding? = null
    private val binding get() = _binding!!
    private val doctorId = "doctorId"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadStatistics()
        setupAvailability()
    }

    private fun loadStatistics() {
        val db = FirebaseDatabase.getInstance()

        // Total pasien (unik)
        db.getReference("consultations")
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .get()
            .addOnSuccessListener { snapshot ->
                val pasienSet = mutableSetOf<String>()
                var aktif = 0

                snapshot.children.forEach {
                    val userId = it.child("userId").value?.toString()
                    val status = it.child("status").value?.toString()

                    userId?.let { id -> pasienSet.add(id) }
                    if (status == "ongoing") aktif++
                }

                binding.tvTotalPasien.text = pasienSet.size.toString()
                binding.tvKonsultasiAktif.text = aktif.toString()
            }
    }

    private fun setupAvailability() {
        val ref = FirebaseDatabase.getInstance()
            .getReference("doctors")
            .child(doctorId)
            .child("available")

        ref.get().addOnSuccessListener {
            binding.switchAvailability.isChecked = it.getValue(Boolean::class.java) ?: false
        }

        binding.switchAvailability.setOnCheckedChangeListener { _, isChecked ->
            ref.setValue(isChecked)
        }
    }
    private fun loadDoctorProfile() {
        val doctorId = "doc_001" // nanti bisa dari FirebaseAuth

        FirebaseDatabase.getInstance()
            .getReference("doctors")
            .child(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val doctor = snapshot.getValue(Doctor::class.java) ?: return

                    binding.tvDoctorName.text = doctor.nama
                    binding.tvDoctorSpecialist.text = doctor.spesialisasi

                    Glide.with(requireContext())
                        .load(doctor.fotoUrl)
                        .placeholder(R.drawable.ic_doctor_default)
                        .into(binding.imgDoctor)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
