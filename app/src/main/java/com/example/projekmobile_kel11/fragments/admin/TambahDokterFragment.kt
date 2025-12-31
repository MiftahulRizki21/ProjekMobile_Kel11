package com.example.projekmobile_kel11.fragments.admin

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.widget.addTextChangedListener
import com.example.projekmobile_kel11.databinding.FragmentTambahDokterBinding
import com.example.projekmobile_kel11.data.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TambahDokterFragment : Fragment() {

    private var _binding: FragmentTambahDokterBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy {
        FirebaseDatabase.getInstance().getReference("users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPreview()

        binding.btnSimpan.setOnClickListener {
            simpanDokter()
        }
    }

    private fun setupPreview() {
        binding.cardPreview.visibility = View.VISIBLE

        binding.edtNama.addTextChangedListener {
            binding.tvPreviewNama.text = it.toString()
        }

        binding.edtEmail.addTextChangedListener {
            binding.tvPreviewEmail.text = it.toString()
        }

        binding.edtSpesialisasi.addTextChangedListener {
            binding.tvPreviewSpesialisasi.text = it.toString()
        }

        binding.edtSertifikasi.addTextChangedListener {
            binding.tvPreviewSertifikasi.text =
                "No. Sertifikasi: ${it.toString()}"
        }
    }

    private fun simpanDokter() {
        val nama = binding.edtNama.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val spesialisasi = binding.edtSpesialisasi.text.toString().trim()
        val fotoUrl = binding.edtFotoUrl.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (nama.isEmpty() || email.isEmpty() || spesialisasi.isEmpty() || password.isEmpty()) {
            toast("Semua data wajib diisi")
            return
        }

        // 1️⃣ Buat akun dokter di Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user!!.uid
                val now = System.currentTimeMillis()

                // 2️⃣ Simpan data dokter ke Realtime Database (TANPA PASSWORD)
                val dokter = Doctor(
                    userId = uid,
                    nama = nama,
                    email = email,
                    spesialisasi = spesialisasi,
                    fotoUrl = fotoUrl,
                    role = "doctor"
                )

                database.child(uid).setValue(dokter)
                    .addOnSuccessListener {
                        toast("Dokter berhasil ditambahkan")
                        parentFragmentManager.popBackStack()
                    }
            }
            .addOnFailureListener {
                toast(it.message ?: "Gagal membuat akun dokter")
            }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
