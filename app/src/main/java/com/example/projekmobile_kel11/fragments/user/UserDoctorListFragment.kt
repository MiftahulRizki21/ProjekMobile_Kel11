package com.example.projekmobile_kel11.fragments.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.UserDoctorAdapter
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.FragmentUserDoctorListBinding
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class UserDoctorListFragment : Fragment(R.layout.fragment_user_doctor_list) {

    private lateinit var binding: FragmentUserDoctorListBinding
    private lateinit var adapter: UserDoctorAdapter
    private val list = mutableListOf<Doctor>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentUserDoctorListBinding.bind(view)

        adapter = UserDoctorAdapter(
            list,
            onDoctorClick = { doctor ->
                val bundle = Bundle().apply {
                    putString("doctorId", doctor.userId)
                    putString("doctorName", doctor.nama)
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, UserScheduleFragment::class.java, bundle)
                    .addToBackStack(null)
                    .commit()
            },
            onCancelBooking = { slot ->
                cancelBooking(slot)
            }
        )


        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter

        loadDoctors()
    }

    private fun loadDoctors() {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .orderByChild("role")
            .equalTo("doctor")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()

                    for (child in snapshot.children) {
                        val doctor = child.getValue(Doctor::class.java)
                        if (doctor != null) {
                            list.add(
                                doctor.copy(userId = child.key ?: "")
                            )
                        }
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
    private fun cancelBooking(slot: TimeSlot) {
        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(slot.doctorId)
            .collection("schedules")
            .document(slot.id)
            .update(
                mapOf(
                    "status" to "available",
                    "patientId" to null
                )
            )
    }

}
