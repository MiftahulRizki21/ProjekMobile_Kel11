package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.ChatMessage
import com.example.projekmobile_kel11.databinding.ItemChatBinding

class ChatAdapter(
    private val list: List<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.VH>() {

    inner class VH(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val msg = list[position]
        holder.binding.tvMessage.text = msg.message
    }

    override fun getItemCount() = list.size
}
