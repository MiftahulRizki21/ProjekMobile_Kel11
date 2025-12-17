// di dalam file fragments/KelolaReminderFragment.kt
package com.example.projekmobile_kel11.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.adapters.ReminderAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaReminderBinding
import com.example.projekmobile_kel11.data.model.Reminder

class KelolaReminderFragment : Fragment() {
    private var _binding: FragmentKelolaReminderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKelolaReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvReminders.layoutManager = LinearLayoutManager(context)

        val dummyReminderList = listOf(
            Reminder("1", "Minum Obat Pagi", "08:00", "Citra Lestari"),
            Reminder("2", "Cek Gula Darah", "09:00", "Budi Santoso"),
            Reminder("3", "Minum Obat Malam", "20:00", "Citra Lestari"),
            Reminder("4", "Vitamin D", "10:00", "Dewi Anggraini")
        )

        val reminderAdapter = ReminderAdapter(dummyReminderList)
        binding.rvReminders.adapter = reminderAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
