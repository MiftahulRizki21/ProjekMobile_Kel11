package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.databinding.ItemReminderBinding
import com.example.projekmobile_kel11.data.model.Reminder

class ReminderAdapter(
    private var reminderList: List<Reminder>,
    private val onDelete: (Reminder) -> Unit,
    private val onEdit: (Reminder) -> Unit,
    private val onToggle: (Reminder, Boolean) -> Unit
)
 : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminderList[position]

        holder.binding.apply {
            tvReminderTitle.text = reminder.title

            val timeFormatted = String.format(
                "%02d:%02d",
                reminder.hour,
                reminder.minute
            )

            tvReminderTime.text = "Pukul $timeFormatted - oleh ${reminder.userName}"

            // 🔔 SWITCH ON OFF
            switchActive.setOnCheckedChangeListener(null)
            switchActive.isChecked = reminder.isActive
            switchActive.setOnCheckedChangeListener { _, isChecked ->
                onToggle(reminder, isChecked)
            }

            btnDelete.setOnClickListener {
                onDelete(reminder)
            }

            root.setOnClickListener {
                onEdit(reminder)
            }
        }
    }


    override fun getItemCount(): Int = reminderList.size

    fun updateData(newList: List<Reminder>) {
        reminderList = newList
        notifyDataSetChanged()
    }
}
