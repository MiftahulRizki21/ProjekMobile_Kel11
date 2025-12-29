package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.DoctorChatListAdapter
import com.example.projekmobile_kel11.data.model.Consultation
import com.example.projekmobile_kel11.databinding.FragmentDoctorChatListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentDoctorChatList : Fragment() {

    private var _binding: FragmentDoctorChatListBinding? = null
    private val binding get() = _binding!!

    private val list = mutableListOf<Consultation>()
    private lateinit var adapter: DoctorChatListAdapter

    private val doctorId = "doc_001" // dari FirebaseAuth nanti

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DoctorChatListAdapter(list) { consultation ->
            val fragment = FragmentChatDoctor.newInstance(consultation.consultationId)

            parentFragmentManager.beginTransaction()
                .replace(R.id.doctor_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvChatList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatList.adapter = adapter

        loadChats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadChats() {
        FirebaseDatabase.getInstance()
            .getReference("consultations")
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach { snap ->
                        val c = snap.getValue(Consultation::class.java)
                        val key = snap.key

                        if (c != null && !key.isNullOrEmpty()) {
                            c.consultationId = key
                            list.add(c)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}