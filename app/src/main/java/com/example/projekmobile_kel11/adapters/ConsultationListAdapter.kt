package com.example.projekmobile_kel11.ui.consultation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.Consultation
import com.example.projekmobile_kel11.databinding.ItemConsultationBinding

class ConsultationListAdapter(
    private val list: List<Consultation>,
    private val onClick: (Consultation) -> Unit
) : RecyclerView.Adapter<ConsultationListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemConsultationBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Consultation) {
            binding.tvLastMessage.text = item.lastMessage
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemConsultationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
