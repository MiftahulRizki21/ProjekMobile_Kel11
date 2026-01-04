package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.data.model.TimeSlot
import com.example.projekmobile_kel11.databinding.ItemUserTimeslotBinding

class UserTimeSlotAdapter(
    private val slots: List<TimeSlot>,
    private val doctorMap: Map<String, Doctor>, // ⬅️ TAMBAH
    private val onBook: (TimeSlot) -> Unit
) : RecyclerView.Adapter<UserTimeSlotAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUserTimeslotBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserTimeslotBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = slots[position]
        val doctor = doctorMap[slot.doctorId]

        holder.binding.tvTime.text =
            "${slot.startTime} - ${slot.endTime}"

        holder.binding.tvDoctorName.text =
            doctor?.nama ?: "Dokter tidak ditemukan"

        holder.binding.tvSpecialist.text =
            doctor?.spesialisasi ?: "-"

        if (slot.status == "available") {
            holder.binding.btnBook.isEnabled = true
            holder.binding.btnBook.text = "Booking"
            holder.binding.btnBook.setOnClickListener {
                onBook(slot)
            }
        } else {
            holder.binding.btnBook.isEnabled = false
            holder.binding.btnBook.text = slot.status
        }
    }


    override fun getItemCount() = slots.size
}
