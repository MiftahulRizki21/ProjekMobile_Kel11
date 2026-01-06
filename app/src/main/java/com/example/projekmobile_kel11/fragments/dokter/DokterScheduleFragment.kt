package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.ScheduleDayAdapter
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.FragmentDokterScheduleBinding
import com.google.firebase.auth.FirebaseAuth
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
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener

                val slots = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TimeSlot::class.java)?.apply {
                        id = doc.id
                        doctorId = this@DokterScheduleFragment.doctorId
                    }
                }

                val grouped = slots.groupBy { it.date }
                setupRecycler(grouped)
            }
    }


    private fun setupRecycler(data: Map<String, List<TimeSlot>>) {
        if (!isAdded || view == null) return

        binding.rvSchedule.layoutManager =
            LinearLayoutManager(binding.root.context)

        binding.rvSchedule.adapter = ScheduleDayAdapter(
            data,
            onApprove = { approveSlot(it) },
            onReject = { rejectSlot(it) },
            onEdit = { editSlot(it) },
            onDelete = { deleteSlot(it) }
        )
    }


    private fun approveSlot(slot: TimeSlot) {
        val doctorId = FirebaseAuth.getInstance().uid ?: return

        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(doctorId)
            .collection("schedules")
            .document(slot.id)
            .update(
                mapOf(
                    "status" to "booked"
                )
            )
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Jadwal berhasil di-approve",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Gagal approve jadwal",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
    private fun rejectSlot(slot: TimeSlot) {
        val doctorId = FirebaseAuth.getInstance().uid ?: return

        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(doctorId)
            .collection("schedules")
            .document(slot.id)
            .update(
                mapOf(
                    "status" to "available",
                    "patientId" to null
                )
            )
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Booking ditolak",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Gagal menolak booking",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}
