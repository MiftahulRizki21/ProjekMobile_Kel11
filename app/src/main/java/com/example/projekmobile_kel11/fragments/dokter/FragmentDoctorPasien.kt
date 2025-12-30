package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.adapters.UserAdapter
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.databinding.FragmentDoctorPasienBinding
import com.google.firebase.database.*
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.data.model.Consultation

class DoctorPasienFragment : Fragment() {

    private var _binding: FragmentDoctorPasienBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UserAdapter
    private val pasienList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorPasienBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        loadPasien()
    }

    private fun setupRecycler() {
        adapter = UserAdapter(
            users = pasienList,
            onItemClick = { user ->
                openPatientDetail(user.userId)
            },
            onDeleteClick = {
            }
        )

        binding.rvPatients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPatients.adapter = adapter
    }



    private fun openPatientDetail(userId: String) {
        val dialog = PatientDetailDialogFragment().apply {
            arguments = Bundle().apply {
                putString("userId", userId)
            }
        }

        dialog.show(parentFragmentManager, "PatientDetailDialog")
    }


    private fun loadPasien() {
        val doctorId = "doc_001"

        FirebaseDatabase.getInstance()
            .getReference("consultations")
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userIds = mutableSetOf<String>()

                    for (data in snapshot.children) {
                        val consultation = data.getValue(Consultation::class.java)
                        consultation?.userId?.let {
                            if (it.isNotEmpty()) userIds.add(it)
                        }
                    }

                    if (userIds.isEmpty()) {
                        pasienList.clear()
                        adapter.notifyDataSetChanged()
                        return
                    }

                    loadUsers(userIds)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun loadUsers(userIds: Set<String>) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        pasienList.clear()
        var loaded = 0

        userIds.forEach { uid ->
            usersRef.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(User::class.java)?.let {
                            pasienList.add(it)
                        }

                        loaded++
                        if (loaded == userIds.size) {
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        loaded++
                    }
                })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
