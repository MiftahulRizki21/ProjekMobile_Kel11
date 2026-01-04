package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.ItemDokterUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserDoctorAdapter(
    private val list: MutableList<Doctor>,
    private val onDoctorClick: (Doctor) -> Unit,
    private val onCancelBooking: (TimeSlot) -> Unit
) : RecyclerView.Adapter<UserDoctorAdapter.ViewHolder>()

 {

    inner class ViewHolder(val binding: ItemDokterUserBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDokterUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val doctor = list[position]

         holder.binding.tvDoctorName.text = doctor.nama
         holder.binding.tvDoctorSpecialization.text = doctor.spesialisasi

         // klik dokter
         holder.binding.root.setOnClickListener {
             onDoctorClick(doctor)
         }

         // list jadwal user pada dokter tsb
         val scheduleAdapter = MyScheduleAdapter(doctor.mySchedules) { slot ->
             onCancelBooking(slot)
         }

         holder.binding.rvDoctorSchedules.apply {
             layoutManager = LinearLayoutManager(holder.itemView.context)
             adapter = scheduleAdapter
         }

         loadDoctorSchedules(doctor, scheduleAdapter)
     }


     override fun getItemCount() = list.size

    private fun loadDoctorSchedules(
        doctor: Doctor,
        adapter: MyScheduleAdapter
    ) {
        val userId = FirebaseAuth.getInstance().uid ?: return

        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(doctor.userId)
            .collection("schedules")
            .whereEqualTo("patientId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                doctor.mySchedules.clear()

                for (doc in snapshot.documents) {
                    val slot = doc.toObject(TimeSlot::class.java)
                    if (slot != null) {
                        doctor.mySchedules.add(
                            slot.copy(
                                id = doc.id,
                                doctorId = doctor.userId
                            )
                        )
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }
}
