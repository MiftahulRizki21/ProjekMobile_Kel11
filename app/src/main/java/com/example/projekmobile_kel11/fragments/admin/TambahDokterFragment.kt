package com.example.projekmobile_kel11.fragments.admin

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.FragmentTambahDokterBinding
import com.example.projekmobile_kel11.models.Dokter
import com.google.firebase.database.*
import androidx.core.widget.addTextChangedListener

class TambahDokterFragment : Fragment() {

    private var _binding: FragmentTambahDokterBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var dokterId: String? = null
    private var existingPassword: String? = null

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

        setupPreview()

        // Jika edit mode, ambil data dokter
        dokterId?.let { id ->
            database.child(id).get().addOnSuccessListener { snapshot ->
                val dokter = snapshot.getValue(Dokter::class.java)
                dokter?.let {
                    binding.edtNama.setText(it.nama)
                    binding.edtEmail.setText(it.email)
                    binding.edtSpesialisasi.setText(it.spesialisasi)
                    binding.edtFotoUrl.setText(it.fotoUrl)
                    existingPassword = it.password // simpan password lama
                }
            }
        }

        binding.btnSimpan.setOnClickListener {
            simpanDokter()
        }
    }
    private fun animatePreview(view: View) {
        view.animate()
            .scaleX(0.97f)
            .scaleY(0.97f)
            .setDuration(120)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }.start()
    }

    private fun setupPreview() {

        // Munculkan preview card pertama kali
        binding.cardPreview.visibility = View.VISIBLE
        binding.cardPreview.animate().alpha(1f).setDuration(300).start()

        binding.edtNama.addTextChangedListener {
            binding.tvPreviewNama.text = it.toString()
            animatePreview(binding.cardPreview)
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
        val nama = binding.edtNama.text.toString()
        val email = binding.edtEmail.text.toString()
        val spesialisasi = binding.edtSpesialisasi.text.toString()
        val fotoUrl = binding.edtFotoUrl.text.toString()
        val password = binding.edtPassword.text.toString()

        // Validasi wajib diisi
        if (nama.isEmpty() || email.isEmpty() || spesialisasi.isEmpty() || (dokterId == null && password.isEmpty())) {
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
            role = "doctor",
            password = if (password.isNotEmpty()) password else existingPassword ?: ""
        )

        database.child(id).setValue(dokter)
            .addOnSuccessListener {
                val message = if (dokterId == null) "Dokter berhasil ditambahkan" else "Dokter berhasil diupdate"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // kembali ke KelolaDokterFragment
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
