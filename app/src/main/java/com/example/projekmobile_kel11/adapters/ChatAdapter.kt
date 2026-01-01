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
    private val doctorId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_DOCTOR = 1
        private const val VIEW_USER = 2
    }

    // ================= VIEW HOLDERS =================
    inner class DoctorVH(val binding: ItemChatDoctorBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class UserVH(val binding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    // ================= VIEW TYPE =================
    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId == doctorId)
            VIEW_DOCTOR
        else
            VIEW_USER
    }

    // ================= CREATE =================
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_DOCTOR) {
            DoctorVH(ItemChatDoctorBinding.inflate(inflater, parent, false))
        } else {
            UserVH(ItemChatUserBinding.inflate(inflater, parent, false))
        }
    }

    // ================= BIND =================
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = list[position]
        val time = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(msg.timestamp))

        if (holder is DoctorVH) {
            holder.binding.tvMessage.text = msg.message
            holder.binding.tvTime.text = time

            holder.binding.tvStatus.text = when (msg.status) {
                "sent" -> "✓"
                "delivered" -> "✓✓"
                "read" -> "✓✓"
                else -> ""
            }

            val defaultColor = ContextCompat.getColor(
                holder.itemView.context,
                R.color.text_secondary
            )

            holder.binding.tvStatus.setTextColor(
                if (msg.status == "read")
                    ContextCompat.getColor(holder.itemView.context, R.color.blue_primary)
                else
                    defaultColor
            )


        } else if (holder is UserVH) {
            holder.binding.tvName.text = "Pasien"
            holder.binding.tvMessage.text = msg.message
            holder.binding.tvTime.text = time
        }
    }

    override fun getItemCount() = list.size
}

