package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.databinding.ItemUserBinding

class UserAdapter(
    private var users: MutableList<User>,
    private val onItemClick: (User) -> Unit,
    private val onDeleteClick: (userId: String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.apply {
            tvUserName.text = user.name
            tvUserEmail.text = user.email

            Glide.with(holder.itemView.context)
                .load(user.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivUserPhoto)

            // 🔥 KLIK ITEM
            root.setOnClickListener {
                onItemClick(user)
            }

            btnDeleteUser.setOnClickListener {
                onDeleteClick(user.userId)
            }
        }
    }


    override fun getItemCount() = users.size

    fun updateData(newList: List<User>) {
        users.clear()
        users.addAll(newList)
        notifyDataSetChanged()
    }


}
