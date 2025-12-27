package com.example.projekmobile_kel11.fragments.admin

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.DokterAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaDokterBinding
import com.example.projekmobile_kel11.models.Dokter
import com.google.firebase.database.*

class KelolaDokterFragment : Fragment() {

    private var _binding: FragmentKelolaDokterBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var adapter: DokterAdapter
    private val dokterList = mutableListOf<Dokter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelolaDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("users")

        setupRecyclerView()
        loadDokters()

        binding.fabAddDoctor.setOnClickListener {
            val fragment = TambahDokterFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        adapter = DokterAdapter(
            mutableListOf(),
            onEditClick = { dokter ->
                val fragment = TambahDokterFragment()
                val bundle = Bundle()
                bundle.putString("doctorId", dokter.userId)
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { dokter ->
                database.child(dokter.userId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Dokter dihapus", Toast.LENGTH_SHORT).show()
                        dokterList.remove(dokter)
                        adapter.updateData(dokterList)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Gagal menghapus dokter", Toast.LENGTH_SHORT).show()
                    }
            }
        )

        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter
    }

    private fun loadDokters() {
        database.orderByChild("role").equalTo("doctor")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dokterList.clear()
                    for (data in snapshot.children) {
                        val dokter = data.getValue(Dokter::class.java) ?: continue
                        dokterList.add(dokter)
                    }
                    adapter.updateData(dokterList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
