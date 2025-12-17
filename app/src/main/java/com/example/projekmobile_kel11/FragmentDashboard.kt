package com.example.projekmobile_kel11.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projekmobile_kel11.databinding.FragmentDashboardAdminBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set tanggal hari ini
        setCurrentDate()

        // Set data statistik (nantinya bisa dari database)
        binding.tvUserCount.text = "152"
        binding.tvDoctorCount.text = "26"

        // Set listener untuk tombol akses cepat
        binding.cardAddDoctor.setOnClickListener {
            // TODO: Arahkan ke halaman tambah dokter
            Toast.makeText(context, "Buka halaman tambah dokter...", Toast.LENGTH_SHORT).show()
        }

        binding.cardSendNotification.setOnClickListener {
            // TODO: Arahkan ke halaman kirim notifikasi
            Toast.makeText(context, "Buka halaman kirim notifikasi...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCurrentDate() {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(Date())
        binding.tvCurrentDate.text = currentDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
