package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ItemChatDoctorBinding
import com.example.projekmobile_kel11.databinding.ItemChatUserBinding

class ChatAdapter(
    private val list: List<ChatMessage>,
    private val myId: String,          // 🔥 ID user saat ini (doctor / user)
    private var otherName: String      // 🔥 NAMA LAWAN BICARA
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val VIEW_ME = 1
        private const val VIEW_OTHER = 2
    }
    fun updateOtherName(newName: String) {
        otherName = newName
        notifyDataSetChanged()
    }

    inner class MeVH(val binding: ItemChatDoctorBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class OtherVH(val binding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId == myId)
            VIEW_ME
        else
            VIEW_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_ME) {
            MeVH(ItemChatDoctorBinding.inflate(inflater, parent, false))
        } else {
            OtherVH(ItemChatUserBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = list[position]
        val time = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(msg.timestamp))

        if (holder is MeVH) {
            holder.binding.tvMessage.text = msg.message
            holder.binding.tvTime.text = time

            holder.binding.tvStatus.text = when (msg.status) {
                "sent" -> "✓"
                "delivered" -> "✓✓"
                "read" -> "✓✓"
                else -> ""
            }

            val color = if (msg.status == "read")
                ContextCompat.getColor(holder.itemView.context, R.color.blue_primary)
            else
                ContextCompat.getColor(holder.itemView.context, R.color.text_secondary)

            holder.binding.tvStatus.setTextColor(color)

        } else if (holder is OtherVH) {
            holder.binding.tvName.text = otherName   // 🔥 FIX UTAMA
            holder.binding.tvMessage.text = msg.message
            holder.binding.tvTime.text = time
        }
    }


    override fun getItemCount() = list.size
}


