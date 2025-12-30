package com.example.projekmobile_kel11.fragments.dokter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.databinding.FragmentPatientDetail2Binding
import com.google.firebase.database.*

class PatientDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentPatientDetail2Binding? = null
    private val binding get() = _binding!!

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
        loadPatient(userId)
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

                    binding.tvNama.text = user.nama
                    binding.tvEmail.text = user.email
                    binding.tvGender.text = user.gender
                    binding.tvUsia.text = "${user.usia} tahun"
                    binding.tvPhone.text = user.phone
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
