package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.DaySchedule
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.ItemScheduleDayBinding
import com.example.projekmobile_kel11.utils.ReminderScheduler.schedule
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ScheduleDayAdapter(
    private val data: Map<String, List<TimeSlot>>,
    private val onApprove: (TimeSlot) -> Unit,
    private val onEdit: (TimeSlot) -> Unit,
    private val onDelete: (TimeSlot) -> Unit
) : RecyclerView.Adapter<ScheduleDayAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemScheduleDayBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = data.keys.elementAt(position)
        val slots = data[date] ?: emptyList()

        holder.binding.tvDay.text = date
        holder.binding.rvSlots.layoutManager =
            LinearLayoutManager(holder.itemView.context)

        holder.binding.rvSlots.adapter =
            TimeSlotAdapter(slots, onApprove, onEdit, onDelete)
    }

    override fun getItemCount() = data.size
}

