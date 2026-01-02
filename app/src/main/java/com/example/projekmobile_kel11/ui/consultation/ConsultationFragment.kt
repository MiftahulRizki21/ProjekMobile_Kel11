package com.example.projekmobile_kel11.ui.consultation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.adapters.DoctorListUserAdapter
import com.example.projekmobile_kel11.data.model.Consultation
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.databinding.FragmentConsultationBinding
import com.example.projekmobile_kel11.fragments.dokter.FragmentChatDoctor
import com.example.projekmobile_kel11.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.projekmobile_kel11.R


class ConsultationFragment : Fragment() {

    private var _binding: FragmentConsultationBinding? = null
    private val binding get() = _binding!!

    private val doctorList = mutableListOf<Doctor>()
    private lateinit var adapter: DoctorListUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.rvDoctor.layoutManager = LinearLayoutManager(requireContext())
        adapter = DoctorListUserAdapter(doctorList) { doctor ->
            startOrOpenConsultation(doctor)
        }
        binding.rvDoctor.adapter = adapter

        // Load list dokter
        FirebaseDatabase.getInstance()
            .getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    doctorList.clear()
                    for (child in snapshot.children) {
                        val role = child.child("role").getValue(String::class.java)
                        if (role == "doctor") {
                            val doctor = child.getValue(Doctor::class.java)
                            doctor?.let {
                                it.userId = child.key ?: ""
                                doctorList.add(it)
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun startOrOpenConsultation(doctor: Doctor) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("consultations")

        // cek consultation lama
        ref.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (child in snapshot.children) {
                        val c = child.getValue(Consultation::class.java)
                        if (c != null && c.doctorId == doctor.userId) {
                            openChat(child.key!!)
                            return
                        }
                    }

                    // buat baru jika belum ada
                    val newRef = ref.push()
                    val consultationId = newRef.key!!
                    val data = Consultation(
                        consultationId = consultationId,
                        userId = userId,
                        doctorId = doctor.userId,
                        lastMessage = "",
                        lastTimestamp = System.currentTimeMillis()
                    )

                    newRef.setValue(data).addOnSuccessListener {
                        openChat(consultationId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // 🔹 open chat di fragment container MainActivity
    private fun openChat(consultationId: String) {
        val fragment = FragmentChatDoctor.newInstance(consultationId)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        // pastikan bottom nav tetap terlihat
        (activity as? MainActivity)?.showBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
