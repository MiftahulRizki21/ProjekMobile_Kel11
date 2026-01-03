package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.DoctorChatListAdapter
import com.example.projekmobile_kel11.data.model.Consultation
import com.example.projekmobile_kel11.databinding.FragmentDoctorChatListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentDoctorChatList : Fragment() {

    private var _binding: FragmentDoctorChatListBinding? = null
    private val binding get() = _binding!!

    private val list = mutableListOf<Consultation>()
    private lateinit var adapter: DoctorChatListAdapter
    private lateinit var doctorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

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
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvChatList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatList.adapter = adapter

        loadChats()
    }

    private fun loadChats() {
        if (doctorId.isEmpty()) return

        FirebaseDatabase.getInstance()
            .getReference("consultations")
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach {
                        it.getValue(Consultation::class.java)?.let(list::add)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

