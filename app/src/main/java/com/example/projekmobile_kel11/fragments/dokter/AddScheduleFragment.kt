package com.example.projekmobile_kel11.fragments.dokter

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.FragmentAddScheduleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddScheduleFragment : Fragment() {

    private lateinit var binding: FragmentAddScheduleBinding
    private val doctorId by lazy {
        FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupDaySpinner()
        setupTimePicker()

        binding.btnSave.setOnClickListener {
            saveSchedule()
        }
    }

    // 🔹 ISI SPINNER
    private fun setupDaySpinner() {
        val days = listOf(
            "Pilih Hari",
            "Senin",
            "Selasa",
            "Rabu",
            "Kamis",
            "Jumat",
            "Sabtu",
            "Minggu"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            days
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDay.adapter = adapter
    }

    // 🔹 TIME PICKER
    private fun setupTimePicker() {
        binding.btnStart.setOnClickListener {
            showTimePicker { time ->
                binding.btnStart.text = time
            }
        }

        binding.btnEnd.setOnClickListener {
            showTimePicker { time ->
                binding.btnEnd.text = time
            }
        }
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                onTimeSelected(time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun saveSchedule() {

        val day = binding.spDay.selectedItem?.toString() ?: ""
        val start = binding.btnStart.text.toString()
        val end = binding.btnEnd.text.toString()

        if (day == "Pilih Hari") {
            Toast.makeText(requireContext(), "Silakan pilih hari", Toast.LENGTH_SHORT).show()
            return
        }

        if (start.contains("Pilih") || end.contains("Pilih")) {
            Toast.makeText(requireContext(), "Jam belum lengkap", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "doctorId" to doctorId,
            "day" to day,
            "startTime" to start,
            "endTime" to end,
            "status" to "available"
        )

        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(doctorId)
            .collection("schedules")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Jadwal ditambahkan", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
    }
}

