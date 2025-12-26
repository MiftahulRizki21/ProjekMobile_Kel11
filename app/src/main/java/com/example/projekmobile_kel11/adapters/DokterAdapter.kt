package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ItemDokterBinding
import com.example.projekmobile_kel11.models.Dokter
import com.google.firebase.database.FirebaseDatabase

class DokterAdapter(
    private val dokterList: List<Dokter>,
    private val onEditClick: (dokterId: String) -> Unit,
    private val onDeleteClick: (dokterId: String) -> Unit
) : RecyclerView.Adapter<DokterAdapter.DokterViewHolder>() {

    inner class DokterViewHolder(val binding: ItemDokterBinding) :
        RecyclerView.ViewHolder(binding.root)

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
                .placeholder(R.drawable.ic_doctor_default)
                .error(R.drawable.ic_doctor_default)
                .into(ivDoctorPhoto)

            // tombol edit
            btnEdit.setOnClickListener { onEditClick(dokter.userId) }

            // tombol delete
            btnDelete.setOnClickListener { onDeleteClick(dokter.userId) }
        }
    }

    override fun getItemCount() = dokterList.size
}
