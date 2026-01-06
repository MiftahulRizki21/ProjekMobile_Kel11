package com.example.projekmobile_kel11.fragments.dokter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.data.model.PredictionResult
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.databinding.FragmentPatientDetail2Binding
import com.example.projekmobile_kel11.ui.profile.PredictionHistoryAdapter
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PatientDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentPatientDetail2Binding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: PredictionHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientDetail2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val userId = arguments?.getString("userId") ?: return

        // 🔥 Adapter
        historyAdapter = PredictionHistoryAdapter(
            onDetail = { prediction ->
                // TODO: buka dialog detail prediksi
                Log.d("Prediction", "Detail: ${prediction.riskLevel}")
            },
            onConsult = { prediction ->
                // TODO: arahkan ke konsultasi dokter
                Log.d("Prediction", "Consult for ${prediction.riskLevel}")
            }
        )

        binding.rvPredictionHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        loadPatient(userId)
        loadPredictionHistory(userId)
    }



    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun loadPatient(userId: String) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java) ?: return

                    binding.tvNama.text = "Nama Pasien: ${user.name}"
                    binding.tvEmail.text = "Email Pasien: ${user.email}"
                    binding.tvGender.text = "Jenis Kelamin Pasien: ${user.gender}"
                    binding.tvUsia.text = "Usia Pasien: ${user.age} tahun"
                    binding.tvPhone.text = "Nomor HP Pasien: ${user.phone}"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadPredictionHistory(patientId: String) {
        FirebaseFirestore.getInstance()
            .collection("predictions")
            .whereEqualTo("userId", patientId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(PredictionResult::class.java)
                historyAdapter.submitList(list)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
