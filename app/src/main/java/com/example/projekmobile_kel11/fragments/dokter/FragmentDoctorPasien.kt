package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.adapters.UserAdapter
import com.example.projekmobile_kel11.data.model.User
import com.example.projekmobile_kel11.data.model.Consultation
import com.example.projekmobile_kel11.databinding.FragmentDoctorPasienBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DoctorPasienFragment : Fragment() {

    private var _binding: FragmentDoctorPasienBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UserAdapter
    private val pasienList = mutableListOf<User>()
    private val filteredList = mutableListOf<User>()

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
        setupSearch()

    }

    // ===============================
    // SETUP RECYCLER
    // ===============================
    private fun setupRecycler() {
        adapter = UserAdapter(
            users = pasienList,
            onItemClick = { user ->
                openPatientDetail(user.userId)
            },
            onDeleteClick = { }
        )

        binding.rvPatients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPatients.adapter = adapter
    }

    // ===============================
    // LOAD PASIEN DARI CONSULTATIONS
    // ===============================
    private fun loadPasien() {
        val doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Log.d("DoctorPasien", "Login doctorId = $doctorId")

        FirebaseDatabase.getInstance()
            .getReference("consultations")
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userIds = mutableSetOf<String>()

                    for (data in snapshot.children) {
                        val consultation = data.getValue(Consultation::class.java)
                        consultation?.userId
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { userIds.add(it) }
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

    // ===============================
    // LOAD DATA USER DARI users/{id}
    // ===============================
    private fun loadUsers(userIds: Set<String>) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        pasienList.clear()
        var loaded = 0

        userIds.forEach { uid ->
            usersRef.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(User::class.java)?.let {
                            it.userId = snapshot.key ?: ""   // 🔥 KUNCI UTAMA
                            pasienList.add(it)
                        }
                        Log.d("DoctorPasien", "Load userId = ${snapshot.key}")

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

    // ===============================
    // OPEN DETAIL PASIEN
    // ===============================
    private fun openPatientDetail(userId: String) {
        val dialog = PatientDetailDialogFragment().apply {
            arguments = Bundle().apply {
                putString("userId", userId)
            }
        }
        Log.d("PatientDetail", "userId = $userId")

        dialog.show(parentFragmentManager, "PatientDetailDialog")
    }
    private fun setupSearch() {
        binding.searchPasien.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val keyword = newText.orEmpty()

                filteredList.clear()

                if (keyword.isEmpty()) {
                    adapter.updateData(pasienList)
                    return true
                }

                for (user in pasienList) {
                    if (
                        user.name.contains(keyword, true) ||
                        user.email.contains(keyword, true)
                    ) {
                        filteredList.add(user)
                    }
                }

                adapter.updateData(filteredList)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
