// di dalam file adapters/UserAdapter.kt
package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ItemUserBinding
import com.example.projekmobile_kel11.models.User

class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.apply {
            tvUserName.text = user.nama
            tvUserEmail.text = user.email

            // Gunakan Glide untuk memuat foto profil
            Glide.with(holder.itemView.context)
                .load(user.fotoUrl)
                .placeholder(R.drawable.ic_launcher_background) // Gambar default jika URL kosong
                .into(ivUserPhoto)

            // TODO: Tambahkan fungsi untuk tombol hapus
            // btnDeleteUser.setOnClickListener { ... }
        }
    }

    override fun getItemCount() = userList.size
}
