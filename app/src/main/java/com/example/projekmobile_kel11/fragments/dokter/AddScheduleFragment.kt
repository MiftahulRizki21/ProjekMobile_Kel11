package com.example.projekmobile_kel11.fragments.dokter

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.FragmentAddScheduleBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddScheduleFragment : Fragment() {
    private var selectedDate: String? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var selectedDay: String? = null

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

        setupTimePicker()

        binding.btnSave.setOnClickListener {
            saveSchedule()
        }
        binding.btnDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build()

            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { millis ->
                val date = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date(millis))

                val dayName = getDayName(date)

                binding.tvDate.text = "$dayName, $date"

                selectedDate = date
                selectedDay = dayName
            }
        }


        binding.btnSave.setOnClickListener {

            if (selectedDate == null || startTime == null || endTime == null) {
                Toast.makeText(requireContext(), "Lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            saveSchedule()
        }

    }

    // 🔹 TIME PICKER
    private fun setupTimePicker() {
        binding.btnStart.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(8)
                .setMinute(0)
                .setTitleText("Pilih Jam Mulai")
                .build()
                .apply {
                    addOnPositiveButtonClickListener {
                        startTime = "%02d:%02d".format(hour, minute)
                        binding.btnStart.text = startTime
                    }
                }
                .show(parentFragmentManager, "startTime")
        }

        binding.btnEnd.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(9)
                .setMinute(0)
                .setTitleText("Pilih Jam Selesai")
                .build()
                .apply {
                    addOnPositiveButtonClickListener {
                        endTime = "%02d:%02d".format(hour, minute)
                        binding.btnEnd.text = endTime
                    }
                }
                .show(parentFragmentManager, "endTime")
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
        val doctorId = FirebaseAuth.getInstance().uid ?: return

        val data = hashMapOf(
            "date" to selectedDate,
            "day" to selectedDay,
            "startTime" to startTime,
            "endTime" to endTime,
            "status" to "available",
            "patientId" to null,
            "doctorId" to doctorId
        )


        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(doctorId)
            .collection("schedules")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Jadwal berhasil disimpan", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
    }


    private fun getDayName(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return ""

        return SimpleDateFormat("EEEE", Locale("id", "ID")).format(date)
    }

}

