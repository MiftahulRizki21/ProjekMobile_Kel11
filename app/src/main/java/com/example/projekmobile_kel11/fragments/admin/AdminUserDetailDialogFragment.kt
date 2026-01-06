package com.example.projekmobile_kel11.fragments.admin

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.databinding.FragmentAdminUserDetailDialogBinding
import com.google.firebase.database.*

class AdminUserDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminUserDetailDialogBinding? = null
    private val binding get() = _binding!!

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
        Log.d("AdminUserDetail", "userId = $userId")

        loadUser(userId)

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun loadUser(userId: String) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java) ?: return

                    binding.tvName.text = "Nama Pasien: ${user.name}"
                    binding.tvEmail.text ="Email Pasien: ${user.email}"
                    binding.tvGender.text = "Jenis Kelamin Pasien: ${user.gender}"
                    binding.tvAge.text = "Usia Pasien: ${user.age} tahun"
                    binding.tvPhone.text = "Nomor HP Pasien: ${user.phone}"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
