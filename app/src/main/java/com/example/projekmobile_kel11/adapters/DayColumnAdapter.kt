package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.ItemScheduleDayBinding

class DayColumnAdapter(
    private val days: List<String>,
    private val scheduleMap: Map<String, List<TimeSlot>>,
    private val onDelete: (TimeSlot) -> Unit,
    private val onApprove: (TimeSlot) -> Unit,
    private val onReject: (TimeSlot) -> Unit
) : RecyclerView.Adapter<DayColumnAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemScheduleDayBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemScheduleDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = days[position]
        holder.binding.tvDay.text = day

        holder.binding.rvSlots.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TimeSlotAdapter(
                scheduleMap[day] ?: emptyList(),
                onDelete,
                onApprove,
                onEdit = { }, // jika belum dipakai
                onReject

            )
        }
    }

    override fun getItemCount(): Int = days.size
}
