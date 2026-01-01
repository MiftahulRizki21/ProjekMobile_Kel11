package com.example.projekmobile_kel11.fragments.admin

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.ReminderAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaReminderBinding
import com.example.projekmobile_kel11.data.model.Reminder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import com.example.projekmobile_kel11.utils.ReminderScheduler

class KelolaReminderFragment : Fragment() {

    private var _binding: FragmentKelolaReminderBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var reminderAdapter: ReminderAdapter

    private val allReminders = mutableListOf<Reminder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelolaReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        reminderAdapter = ReminderAdapter(
            allReminders,
            onDelete = { deleteReminder(it) },
            onEdit = { showEditDialog(it) },
            onToggle = { reminder, isActive ->
                toggleReminder(reminder, isActive)
            }
        )


        binding.rvReminders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reminderAdapter
        }

        binding.fabAddReminder.setOnClickListener {
            showAddReminderDialog()
        }

        loadReminders()
    }
    private fun toggleReminder(reminder: Reminder, isActive: Boolean) {
        firestore.collection("reminders")
            .document(reminder.id)
            .update("isActive", isActive)

        if (isActive) {
            ReminderScheduler.schedule(
                requireContext(),
                reminder.id,
                reminder.hour,
                reminder.minute,
                reminder.interval ?: "daily",
                reminder.title ?: "Reminder",
                "Pukul ${String.format("%02d:%02d", reminder.hour, reminder.minute)}"
            )
        } else {
            ReminderScheduler.cancel(requireContext(), reminder.id)
        }
    }



    private fun loadReminders() {
        firestore.collection("reminders")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener

                allReminders.clear()
                snapshot.documents.forEach { doc ->
                    doc.toObject(Reminder::class.java)?.let {
                        allReminders.add(it.copy(id = doc.id))
                    }
                }
                reminderAdapter.updateData(allReminders)
            }
    }

    private fun deleteReminder(reminder: Reminder) {
        ReminderScheduler.cancel(requireContext(), reminder.id)

        firestore.collection("reminders")
            .document(reminder.id)
            .delete()
    }


    // ================= ADD =================

    private fun showAddReminderDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_reminder, null)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etTime = view.findViewById<EditText>(R.id.etTime)

        val calendar = Calendar.getInstance()

        etTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, h, m ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, m)
                    etTime.setText(String.format("%02d:%02d", h, m))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Reminder")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                saveReminder(etTitle.text.toString(), calendar)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveReminder(title: String, calendar: Calendar) {
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val data = hashMapOf(
            "title" to title,
            "hour" to calendar.get(Calendar.HOUR_OF_DAY),
            "minute" to calendar.get(Calendar.MINUTE),
            "interval" to "daily",
            "userName" to "Admin",
            "isActive" to true,
            "nextTrigger" to Timestamp(calendar.time)
        )

        firestore.collection("reminders")
            .add(data)
            .addOnSuccessListener { doc ->
                Log.d("FIRESTORE", "Reminder berhasil disimpan")

                // 🔔 SCHEDULE ALARM
                ReminderScheduler.schedule(
                    requireContext(),
                    doc.id,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    "daily",
                    title,
                    "Pukul ${String.format(
                        "%02d:%02d",
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE)
                    )}"
                )
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Gagal menyimpan reminder", e)
            }
    }


    // ================= EDIT =================

    private fun showEditDialog(reminder: Reminder) {
        val view = layoutInflater.inflate(R.layout.dialog_edit_reminder, null)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etTime = view.findViewById<EditText>(R.id.etTime)

        etTitle.setText(reminder.title)

        val calendar = Calendar.getInstance()

        etTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, h, m ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, m)
                    etTime.setText(String.format("%02d:%02d", h, m))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Reminder")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                updateReminder(
                    reminder.id,
                    etTitle.text.toString(),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                )
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateReminder(id: String, title: String, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }

        firestore.collection("reminders")
            .document(id)
            .update(
                mapOf(
                    "title" to title,
                    "hour" to hour,
                    "minute" to minute,
                    "nextTrigger" to Timestamp(calendar.time)
                )
            )
            .addOnSuccessListener {
                // 🔔 RESCHEDULE
                ReminderScheduler.schedule(
                    requireContext(),
                    id,
                    hour,
                    minute,
                    "daily",
                    title,
                    "Pukul ${String.format("%02d:%02d", hour, minute)}"
                )

            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
