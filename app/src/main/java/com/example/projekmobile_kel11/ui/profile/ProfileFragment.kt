package com.example.projekmobile_kel11.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.databinding.FragmentProfileBinding
import com.example.projekmobile_kel11.data.model.PredictionResult
import com.example.projekmobile_kel11.ui.auth.LoginActivity
import com.example.projekmobile_kel11.ui.prediction.PredictionHistoryFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val adapter = PredictionHistoryAdapter(
        onDetail = { prediction ->
            // TODO: pindah ke halaman detail prediksi
            Toast.makeText(requireContext(), "Klik detail prediksi: ${prediction.userId}", Toast.LENGTH_SHORT).show()
        },
        onConsult = { prediction ->
            // TODO: aksi konsultasi
            Toast.makeText(requireContext(), "Klik konsultasi: ${prediction.userId}", Toast.LENGTH_SHORT).show()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupClickListeners()
        loadUserProfile()
        loadRecentPredictions()
    }

    private fun setupRecycler() {
        binding.rvRecentPredictions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentPredictions.adapter = adapter
    }

    private fun setupClickListeners() {
        // Logout
        binding.layoutLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Riwayat Prediksi
        binding.layoutHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(requireId(), PredictionHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        // Lihat Semua Prediksi
        binding.tvViewAll.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(requireId(), PredictionHistoryFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .get()
            .addOnSuccessListener {
                binding.tvUserName.text = it.child("name").value.toString()
                binding.tvUserEmail.text = it.child("email").value.toString()
                binding.tvMemberSince.text = "Member sejak Januari 2024" // bisa diganti dinamis
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal load profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRecentPredictions() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("predictions")
            .whereEqualTo("userId", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener {
                val list = it.toObjects(PredictionResult::class.java)
                adapter.submitList(list)
                binding.layoutEmptyHistory.visibility =
                    if (list.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal load riwayat prediksi", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Helper untuk mendapatkan container fragment utama
     * Agar replace Fragment bekerja dengan benar
     */
    private fun requireId(): Int {
        return (view?.parent as? ViewGroup)?.id
            ?: throw IllegalStateException("Container fragment tidak ditemukan")
    }
}
