package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.FragmentDashboardDokterBinding
import com.google.firebase.database.FirebaseDatabase

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
