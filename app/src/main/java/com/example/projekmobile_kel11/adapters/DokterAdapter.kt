package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.models.Dokter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class DokterAdapter(
    private var list: MutableList<Dokter>,
    private val onEditClick: (Dokter) -> Unit,
    private val onDeleteClick: (Dokter) -> Unit
) : RecyclerView.Adapter<DokterAdapter.DokterViewHolder>() {

    inner class DokterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: CircleImageView = itemView.findViewById(R.id.iv_doctor_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_doctor_name)
        val tvSpesialisasi: TextView = itemView.findViewById(R.id.tv_doctor_specialization)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DokterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dokter, parent, false)
        return DokterViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: DokterViewHolder, position: Int) {
        val dokter = list[position]
        holder.tvName.text = dokter.nama
        holder.tvSpesialisasi.text = dokter.spesialisasi

        Glide.with(holder.itemView.context)
            .load(dokter.fotoUrl)
            .placeholder(R.drawable.ic_doctor_default)
            .into(holder.ivPhoto)

        holder.btnEdit.setOnClickListener { onEditClick(dokter) }
        holder.btnDelete.setOnClickListener { onDeleteClick(dokter) }
    }

    fun updateData(newList: List<Dokter>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
