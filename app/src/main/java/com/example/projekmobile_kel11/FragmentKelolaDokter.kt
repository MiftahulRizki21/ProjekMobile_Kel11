// di dalam file fragments/KelolaDokterFragment.kt
package com.example.projekmobile_kel11.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.adapters.DokterAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaDokterBinding
import com.example.projekmobile_kel11.models.Dokter

class FragmentKelolaDokter : Fragment() {
    private var _binding: FragmentKelolaDokterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKelolaDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDoctors.layoutManager = LinearLayoutManager(context)

        val dummyDokterList = listOf(
            Dokter("1", "Dr. Amelia Wijaya", "Spesialis Jantung"),
            Dokter("2", "Dr. Bayu Perkasa", "Spesialis Anak"),
            Dokter("3", "Dr. Dian Paramita", "Dokter Umum"),
            Dokter("4", "Dr. Eka Putra", "Spesialis Kulit")
        )

        val dokterAdapter = DokterAdapter(dummyDokterList)
        binding.rvDoctors.adapter = dokterAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
