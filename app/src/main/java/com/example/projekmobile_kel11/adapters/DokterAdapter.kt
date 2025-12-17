// di dalam file adapters/DokterAdapter.kt
package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ItemDokterBinding
import com.example.projekmobile_kel11.models.Dokter

class DokterAdapter(private val dokterList: List<Dokter>) : RecyclerView.Adapter<DokterAdapter.DokterViewHolder>() {

    inner class DokterViewHolder(val binding: ItemDokterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DokterViewHolder {
        val binding = ItemDokterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DokterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DokterViewHolder, position: Int) {
        val dokter = dokterList[position]
        holder.binding.apply {
            tvDoctorName.text = dokter.nama
            tvDoctorSpecialization.text = dokter.spesialisasi

            Glide.with(holder.itemView.context)
                .load(dokter.fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivDoctorPhoto)
        }
    }

    override fun getItemCount() = dokterList.size
}
