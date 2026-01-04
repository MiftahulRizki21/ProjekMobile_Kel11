package com.example.projekmobile_kel11.ui.prediction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.ui.profile.PredictionHistoryAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.data.repository.PredictionRepository
import com.google.firebase.auth.FirebaseAuth

class PredictionHistoryFragment : Fragment(R.layout.fragment_prediction_history) {

    private lateinit var adapter: PredictionHistoryAdapter
    private val repo = PredictionRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PredictionHistoryAdapter(
            onDetail = {},
            onConsult = {}
        )

        view.findViewById<RecyclerView>(R.id.rvHistory).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PredictionHistoryFragment.adapter
        }

        loadHistory()
    }

    private fun loadHistory() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        repo.getUserPredictions(
            userId = uid,
            onSuccess = {
                adapter.submitList(it)
            },
            onError = {
                Toast.makeText(requireContext(), "Gagal load riwayat", Toast.LENGTH_SHORT).show()
                it.printStackTrace()
            }
        )
    }
}
