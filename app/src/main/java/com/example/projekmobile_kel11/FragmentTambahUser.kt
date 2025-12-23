package com.example.projekmobile_kel11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.FragmentTambahUserBinding
import com.example.projekmobile_kel11.data.model.User
import com.google.firebase.database.FirebaseDatabase

class TambahUserFragment : Fragment() {

    private var _binding: FragmentTambahUserBinding? = null
    private val binding get() = _binding!!

    private val database by lazy {
        FirebaseDatabase.getInstance().getReference("users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSimpan.setOnClickListener {
            simpanUser()
        }
    }

    private fun simpanUser() {
        val nama = binding.edtNama.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val usiaText = binding.edtUsia.text.toString().trim()
        val gender = binding.edtGender.text.toString().trim()

        if (nama.isEmpty() || email.isEmpty() || usiaText.isEmpty() || gender.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val usia = usiaText.toIntOrNull()
        if (usia == null) {
            binding.edtUsia.error = "Usia harus angka"
            return
        }

        val userId = database.push().key ?: return
        val user = User(
            userId = userId,
            nama = nama,
            email = email,
            usia = usia,
            gender = gender
        )

        database.child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menambah user", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
