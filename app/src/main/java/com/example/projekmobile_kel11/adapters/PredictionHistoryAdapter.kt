package com.example.projekmobile_kel11.ui.profile

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.PredictionResult
import com.example.projekmobile_kel11.databinding.ItemPredictionHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class PredictionHistoryAdapter(
    private val onDetail: (PredictionResult) -> Unit,
    private val onConsult: (PredictionResult) -> Unit
) : RecyclerView.Adapter<PredictionHistoryAdapter.ViewHolder>() {

    private val items = mutableListOf<PredictionResult>()

    fun submitList(data: List<PredictionResult>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemPredictionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPredictionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]

        holder.binding.tvPredictionDate.text = formatDate(data.timestamp)
        holder.binding.tvRiskLabel.text = data.riskLevel.uppercase()
        holder.binding.tvRiskDescription.text = getRiskDescription(data.riskLevel)

        holder.binding.tvRiskLabel.setBackgroundResource(
            getRiskBackground(data.riskLevel)
        )

        holder.binding.btnViewDetails.setOnClickListener {
            onDetail(data)
        }

        holder.binding.btnConsult.visibility =
            if (data.riskLevel.lowercase() == "rendah") View.GONE else View.VISIBLE

        holder.binding.btnConsult.setOnClickListener {
            onConsult(data)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }

    private fun getRiskDescription(level: String): String =
        when (level.lowercase()) {
            "rendah" -> "Risiko kanker mulut rendah."
            "sedang" -> "Risiko kanker mulut sedang."
            "tinggi" -> "Risiko kanker mulut tinggi."
            else -> "-"
        }

    private fun getRiskBackground(level: String): Int =
        when (level.lowercase()) {
            "rendah" -> R.drawable.bg_risk_low
            "sedang" -> R.drawable.bg_risk_medium
            "tinggi" -> R.drawable.bg_risk_high
            else -> R.drawable.bg_risk_low
        }
}
