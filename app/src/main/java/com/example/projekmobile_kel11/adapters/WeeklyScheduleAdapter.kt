package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.ItemTimeSlotBinding

class WeeklyScheduleAdapter(
    private val slots: List<TimeSlot>,
    private val onClick: (TimeSlot) -> Unit
) : RecyclerView.Adapter<WeeklyScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTimeSlotBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = slots[position]

        // JAM
        holder.binding.tvTime.text =
            "${slot.startTime} - ${slot.endTime}"

        // WARNA STATUS
        holder.binding.tvTime.setBackgroundResource(
            when (slot.status) {
                "booked" -> R.drawable.bg_input
                "pending" -> R.drawable.bg_circle_blue
                else -> R.drawable.bg_input_yellow
            }
        )

        holder.binding.root.setOnClickListener {
            onClick(slot)
        }
    }

    override fun getItemCount() = slots.size

}
