package com.example.projekmobile_kel11.fragments.user

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.MyScheduleAdapter
import com.example.projekmobile_kel11.adapters.UserTimeSlotAdapter
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.FragmentUserScheduleBinding
import com.example.projekmobile_kel11.utils.StatusDecorator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class UserScheduleFragment : Fragment() {
    private lateinit var myScheduleAdapter: MyScheduleAdapter
    private val mySchedules = mutableListOf<TimeSlot>()
    private lateinit var calendarView: MaterialCalendarView
    private val doctorMap = mutableMapOf<String, Doctor>()


    private val allSchedules = mutableListOf<TimeSlot>()

    private lateinit var binding: FragmentUserScheduleBinding
    private lateinit var adapter: UserTimeSlotAdapter
    private val list = mutableListOf<TimeSlot>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        calendarView = binding.calendarView

        adapter = UserTimeSlotAdapter(
            list,
            doctorMap
        ) { slot ->
            requestBooking(slot)
        }


        myScheduleAdapter = MyScheduleAdapter(mySchedules) { slot ->
            cancelBooking(slot)
        }

        // 🔥 SET RV BOOKING SAYA
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = myScheduleAdapter

        // 🔥 LISTENER KALENDER
        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate =
                "%04d-%02d-%02d".format(date.year, date.month, date.day)

            val slots = allSchedules.filter { it.date == selectedDate }

            if (slots.isNotEmpty()) {
                showSchedulePopup(selectedDate)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Tidak ada jadwal pada tanggal ini",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        loadDoctors {
            loadSchedules()
        }
        loadMySchedules()
    }

    private fun loadDoctors(onComplete: () -> Unit) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    doctorMap.clear()

                    snapshot.children.forEach { snap ->
                        val doctor = snap.getValue(Doctor::class.java)
                        doctor?.let {
                            it.userId = snap.key ?: ""
                            doctorMap[it.userId] = it
                        }
                    }

                    onComplete()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }



    private fun loadMySchedules() {
        Log.d("MY_SCHEDULE", "loadMySchedules DIPANGGIL")

        val userId = FirebaseAuth.getInstance().uid
        Log.d("MY_SCHEDULE", "USER ID: $userId")

        if (userId == null) return

        FirebaseFirestore.getInstance()
            .collectionGroup("schedules")
            .whereEqualTo("patientId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("MY_SCHEDULE", "FOUND: ${snapshot.size()} schedules")

                mySchedules.clear() // ✅ BENAR

                for (doc in snapshot.documents) {
                    val slot = doc.toObject(TimeSlot::class.java)
                    if (slot != null) {
                        mySchedules.add(
                            slot.copy(
                                id = doc.id,
                                doctorId = doc.reference.parent.parent?.id ?: ""
                            )
                        )
                    }
                }

                myScheduleAdapter.notifyDataSetChanged() // ✅ BENAR
            }
            .addOnFailureListener {
                Log.e("MY_SCHEDULE", "ERROR", it)
            }
    }
    private fun showSchedulePopup(date: String) {
        val slots = allSchedules.filter { it.date == date }

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_schedule)

        val rv = dialog.findViewById<RecyclerView>(R.id.rvSlots)
        rv.layoutManager = LinearLayoutManager(requireContext())

        rv.adapter = UserTimeSlotAdapter(slots, doctorMap) { slot ->
            requestBooking(slot)
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }




    private fun loadSchedules() {
        FirebaseFirestore.getInstance()
            .collectionGroup("schedules") // ✅ FIX
            .get()
            .addOnSuccessListener { snapshot ->
                allSchedules.clear()

                snapshot.forEach {
                    allSchedules.add(
                        it.toObject(TimeSlot::class.java).copy(
                            id = it.id,
                            doctorId = it.reference.parent.parent?.id ?: ""
                        )
                    )
                }

                setupCalendarDecorators()
            }
    }

    private fun setupCalendarDecorators() {
        val blue = mutableSetOf<CalendarDay>()
        val yellow = mutableSetOf<CalendarDay>()
        val green = mutableSetOf<CalendarDay>()

        allSchedules.forEach {
            val parts = it.date.split("-") // yyyy-MM-dd
            if (parts.size == 3) {
                val day = CalendarDay.from(
                    parts[0].toInt(),
                    parts[1].toInt(),
                    parts[2].toInt()
                )

                when (it.status) {
                    "available" -> blue.add(day)
                    "requested" -> yellow.add(day)
                    "approved", "booked" -> green.add(day)
                }
            }
        }

        calendarView.removeDecorators()
        calendarView.addDecorator(StatusDecorator(blue, Color.BLUE))
        calendarView.addDecorator(StatusDecorator(yellow, Color.YELLOW))
        calendarView.addDecorator(StatusDecorator(green, Color.GREEN))
    }


    private fun filterScheduleByDate(date: String) {
        Log.d("FILTER", "Selected date: $date")

        allSchedules.forEach {
            Log.d("FILTER", "Slot date: ${it.date}")
        }

        val filtered = allSchedules.filter {
            it.date == date
        }

        Log.d("FILTER", "Filtered size: ${filtered.size}")

        list.clear()
        list.addAll(filtered)
        adapter.notifyDataSetChanged()
    }

    private fun requestBooking(slot: TimeSlot) {
        val patientId = FirebaseAuth.getInstance().uid ?: return

        FirebaseFirestore.getInstance()
            .collection("doctor_schedules")
            .document(slot.doctorId)
            .collection("schedules")
            .document(slot.id)
            .update(
                mapOf(
                    "status" to "requested",
                    "patientId" to patientId
                )
            )
            .addOnSuccessListener {

                // 🔥 UPDATE DATA LOKAL
                slot.status = "requested"
                slot.patientId = patientId

                Toast.makeText(
                    requireContext(),
                    "Booking berhasil",
                    Toast.LENGTH_SHORT
                ).show()

                // 🔥 REFRESH DATA
                loadSchedules()
                loadMySchedules()
            }
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
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Booking dibatalkan", Toast.LENGTH_SHORT).show()

                // refresh list
                loadMySchedules()
                loadSchedules()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal membatalkan", Toast.LENGTH_SHORT).show()
            }
    }
}
