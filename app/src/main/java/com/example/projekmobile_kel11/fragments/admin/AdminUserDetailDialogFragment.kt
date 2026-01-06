package com.example.projekmobile_kel11.fragments.admin

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.projekmobile_kel11.data.model.PredictionResult
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.databinding.FragmentAdminUserDetailDialogBinding
import com.example.projekmobile_kel11.ui.profile.PredictionHistoryAdapter
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query

class AdminUserDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminUserDetailDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: PredictionHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUserDetailDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val userId = arguments?.getString("userId") ?: return

        // 🔥 Adapter prediksi
        historyAdapter = PredictionHistoryAdapter(
            onDetail = { prediction ->
                // Admin hanya lihat detail
                Log.d("ADMIN", "Detail prediksi: ${prediction.riskLevel}")
            },
            onConsult = {
                // Admin tidak perlu konsultasi
            }
        )

        binding.rvPredictionHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        loadUser(userId)
        loadPredictionHistory(userId)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    // ================= USER DETAIL =================
    private fun loadUser(userId: String) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java) ?: return

                    binding.tvNama.text = user.name
                    binding.tvEmail.text = user.email
                    binding.tvGender.text = user.gender
                    binding.tvUsia.text = "${user.age} tahun"
                    binding.tvPhone.text = user.phone
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ================= RIWAYAT PREDIKSI =================
    private fun loadPredictionHistory(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("predictions")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(PredictionResult::class.java)
                historyAdapter.submitList(list)

                binding.tvEmptyPrediction.visibility =
                    if (list.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

