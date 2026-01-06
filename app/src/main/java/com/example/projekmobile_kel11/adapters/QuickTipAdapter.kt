package com.example.projekmobile_kel11.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.databinding.ItemQuickTipBinding

class QuickTipAdapter : RecyclerView.Adapter<QuickTipAdapter.QuickTipViewHolder>() {

    private val tips = mutableListOf<String>()

    inner class QuickTipViewHolder(private val binding: ItemQuickTipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tip: String, position: Int) {
            binding.tvTipNumber.text = "${position + 1}"
            binding.tvTipText.text = tip

            // Set warna background berdasarkan posisi
            val bgColor = when (position % 4) {
                0 -> R.drawable.bg_tip_blue
                1 -> R.drawable.bg_tip_green
                2 -> R.drawable.bg_tip_orange
                else -> R.drawable.bg_tip_purple
            }
            binding.tvTipNumber.setBackgroundResource(bgColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickTipViewHolder {
        val binding = ItemQuickTipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuickTipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuickTipViewHolder, position: Int) {
        holder.bind(tips[position], position)
    }

    override fun getItemCount(): Int = tips.size

    fun submitList(newTips: List<String>) {
        tips.clear()
        tips.addAll(newTips)
        notifyDataSetChanged()
    }
}