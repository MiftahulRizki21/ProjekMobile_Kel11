package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.ScheduleDayAdapter
import com.example.projekmobile_kel11.adapters.WeeklyScheduleAdapter
import com.example.projekmobile_kel11.data.model.DaySchedule
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.FragmentDokterScheduleBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class DokterScheduleFragment : Fragment() {

    private lateinit var binding: FragmentDokterScheduleBinding
    private val doctorId by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDokterScheduleBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadWeeklySchedule()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_to_addSchedule)
        }
    }

    private fun loadWeeklySchedule() {
        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(doctorId)
            .collection("schedules")
            .get()
            .addOnSuccessListener { snapshot ->

                val slots = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TimeSlot::class.java)?.apply {
                        id = doc.id
                    }
                }

                // 🔥 GROUP BY DAY / DATE
                val grouped = slots.groupBy { it.date }

                setupRecycler(grouped)
            }
    }



    private fun setupRecycler(data: Map<String, List<TimeSlot>>) {
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = ScheduleDayAdapter(
            data,
            onApprove = { approveSlot(it) },
            onEdit = { editSlot(it) },
            onDelete = { deleteSlot(it) }
        )
    }

    private fun approveSlot(slot: TimeSlot) {
        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(slot.doctorId)
            .collection("schedules")
            .document(slot.id)
            .update("status", "booked")
    }


    private fun deleteSlot(slot: TimeSlot) {
        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(slot.doctorId)
            .collection("schedules")
            .document(slot.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Jadwal dihapus", Toast.LENGTH_SHORT).show()
                loadWeeklySchedule()
            }
    }

    private fun editSlot(slot: TimeSlot) {
        val bundle = Bundle().apply {
            putString("slotId", slot.id)
            putString("day", slot.date)
            putString("start", slot.startTime)
            putString("end", slot.endTime)
        }

        findNavController().navigate(
            R.id.addScheduleFragment,
            bundle
        )
    }
    fun requestBooking(slotId: String, doctorId: String, patientId: String) {
        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(doctorId)
            .collection("schedules")
            .document(slotId)
            .update(
                mapOf(
                    "status" to "requested",
                    "patientId" to patientId
                )
            )
    }
    private fun approveBooking(slot: TimeSlot) {
        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(slot.doctorId)
            .collection("schedules")
            .document(slot.id)
            .update("status", "booked")
    }
    private fun rejectBooking(slot: TimeSlot) {
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
