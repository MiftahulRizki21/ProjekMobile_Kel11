package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.ItemMyScheduleBinding

class MyScheduleAdapter(
    private val list: MutableList<TimeSlot>,
    private val onCancel: (TimeSlot) -> Unit
) : RecyclerView.Adapter<MyScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMyScheduleBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyScheduleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = list[position]

        holder.binding.tvDate.text = "${slot.day}, ${slot.date}"
        holder.binding.tvTime.text = "${slot.startTime} - ${slot.endTime}"
        holder.binding.tvStatus.text = slot.status

        // tombol batal
        holder.binding.btnCancel.setOnClickListener {
            onCancel(slot)
        }
    }

    override fun getItemCount() = list.size
}
