package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.Consultation
import com.example.projekmobile_kel11.databinding.ItemChatListBinding

class DoctorChatListAdapter(
    private val list: List<Consultation>,
    private val onClick: (Consultation) -> Unit
) : RecyclerView.Adapter<DoctorChatListAdapter.VH>() {

    inner class VH(val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.binding.tvName.text = "Pasien ${item.userId}"
        holder.binding.tvLastMessage.text = item.lastMessage
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size
}
