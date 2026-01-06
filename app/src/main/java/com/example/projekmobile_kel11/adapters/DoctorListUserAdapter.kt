package com.example.projekmobile_kel11.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.Doctor
import com.example.projekmobile_kel11.databinding.ItemConsultationBinding
import com.example.projekmobile_kel11.databinding.ItemDokterBinding

class DoctorListUserAdapter(
    private val list: List<Doctor>,
    private val onClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorListUserAdapter.VH>() {

    inner class VH(val binding: ItemDokterBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(d: Doctor) {
            binding.tvDoctorName.text = d.nama
            binding.tvDoctorSpecialization.text = d.spesialisasi

            binding.btnEdit.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE

            binding.root.setOnClickListener {
                Log.d("CHAT_DEBUG", "Doctor clicked: ${d.userId}")
                onClick(d)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemDokterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}



