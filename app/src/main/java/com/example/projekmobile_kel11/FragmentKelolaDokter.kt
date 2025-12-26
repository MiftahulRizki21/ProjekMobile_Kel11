package com.example.projekmobile_kel11.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.DokterAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaDokterBinding
import com.example.projekmobile_kel11.models.Dokter
import com.example.projekmobile_kel11.TambahDokterFragment
import com.google.firebase.database.*

class KelolaDokterFragment : Fragment() {

    private var _binding: FragmentKelolaDokterBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private val dokterList = mutableListOf<Dokter>()
    private lateinit var adapter: DokterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelolaDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("users")

        setupRecyclerView()
        loadDoctors()

        // FAB untuk tambah dokter baru
        binding.fabAddDoctor.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TambahDokterFragment())
                .addToBackStack(null)
                .commit()
        }

        // SearchView
        binding.searchViewDoctor.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDoctors(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDoctors(newText)
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = DokterAdapter(dokterList,
            onEditClick = { dokterId ->
                // Kirim ID dokter ke TambahDokterFragment untuk edit
                val fragment = TambahDokterFragment()
                val bundle = Bundle()
                bundle.putString("doctorId", dokterId)
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { dokterId ->
                // Hapus data dari Firebase
                database.child(dokterId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Dokter berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Gagal menghapus dokter", Toast.LENGTH_SHORT).show()
                    }
            }
        )

        binding.rvDoctors.layoutManager = LinearLayoutManager(context)
        binding.rvDoctors.adapter = adapter
    }

    private fun loadDoctors() {
        database.orderByChild("role").equalTo("doctor")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dokterList.clear()
                    for (data in snapshot.children) {
                        val dokter = data.getValue(Dokter::class.java)
                        if (dokter != null) dokterList.add(dokter)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Gagal memuat data dokter", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterDoctors(query: String?) {
        val filtered = if (!query.isNullOrEmpty()) {
            dokterList.filter { it.nama.contains(query, true) }
        } else {
            dokterList
        }

        adapter = DokterAdapter(filtered,
            onEditClick = { dokterId ->
                val fragment = TambahDokterFragment()
                val bundle = Bundle()
                bundle.putString("doctorId", dokterId)
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { dokterId ->
                database.child(dokterId).removeValue()
            })
        binding.rvDoctors.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
