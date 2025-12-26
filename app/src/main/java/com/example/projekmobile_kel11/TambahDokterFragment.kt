package com.example.projekmobile_kel11

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.FragmentTambahDokterBinding
import com.example.projekmobile_kel11.models.Dokter
import com.google.firebase.database.*

class TambahDokterFragment : Fragment() {

    private var _binding: FragmentTambahDokterBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var dokterId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("users")
        dokterId = arguments?.getString("doctorId")

        // Jika edit mode, ambil data dokter
        dokterId?.let { id ->
            database.child(id).get().addOnSuccessListener { snapshot ->
                val dokter = snapshot.getValue(Dokter::class.java)
                dokter?.let {
                    binding.edtNama.setText(it.nama)
                    binding.edtEmail.setText(it.email)
                    binding.edtSpesialisasi.setText(it.spesialisasi)
                    binding.edtFotoUrl.setText(it.fotoUrl)
                }
            }
        }

        binding.btnSimpan.setOnClickListener {
            simpanDokter()
        }
    }

    private fun simpanDokter() {
        val nama = binding.edtNama.text.toString()
        val email = binding.edtEmail.text.toString()
        val spesialisasi = binding.edtSpesialisasi.text.toString()
        val fotoUrl = binding.edtFotoUrl.text.toString()

        if (nama.isEmpty() || email.isEmpty() || spesialisasi.isEmpty()) {
            Toast.makeText(context, "Data wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val id = dokterId ?: database.push().key ?: return

        val dokter = Dokter(
            userId = id,
            nama = nama,
            email = email,
            spesialisasi = spesialisasi,
            fotoUrl = fotoUrl,
            role = "doctor"
        )

        database.child(id).setValue(dokter)
            .addOnSuccessListener {
                val message = if (dokterId == null) "Dokter berhasil ditambahkan" else "Dokter berhasil diupdate"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menyimpan dokter", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
