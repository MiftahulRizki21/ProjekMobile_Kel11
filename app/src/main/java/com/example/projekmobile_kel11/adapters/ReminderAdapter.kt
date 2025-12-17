// di dalam file adapters/ReminderAdapter.kt
package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.databinding.ItemReminderBinding
import com.example.projekmobile_kel11.data.model.Reminder

class ReminderAdapter(private val reminderList: List<Reminder>) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminderList[position]
        holder.binding.apply {
            tvReminderTitle.text = reminder.judul
            val detailText = "Pukul ${reminder.waktu} - oleh ${reminder.namaUser}"
            tvReminderTime.text = detailText
        }
    }

    override fun getItemCount() = reminderList.size
}
