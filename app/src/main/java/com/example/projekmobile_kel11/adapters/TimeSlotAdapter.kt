package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.ItemTimeSlotBinding

class TimeSlotAdapter(
    private val slots: List<TimeSlot>,
    private val onApprove: (TimeSlot) -> Unit,
    private val onEdit: (TimeSlot) -> Unit,
    private val onDelete: (TimeSlot) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTimeSlotBinding)
        : RecyclerView.ViewHolder(binding.root)

    // 🔹 WAJIB ADA
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = slots[position]

        holder.binding.tvTime.text =
            "${slot.startTime} - ${slot.endTime}"

        holder.binding.tvStatus.text = slot.status

        holder.binding.btnApprove.setOnClickListener {
            onApprove(slot)
        }

        holder.binding.btnEdit.setOnClickListener {
            onEdit(slot)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDelete(slot)
        }
    }

    override fun getItemCount(): Int = slots.size
}
