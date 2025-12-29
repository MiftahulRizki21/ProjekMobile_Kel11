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
            pasienList,
            onDeleteClick = { /* dokter tidak delete pasien */ }
        )

        binding.rvPatients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPatients.adapter = adapter
    }

    private fun loadPasien() {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .orderByChild("role")
            .equalTo("user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pasienList.clear()
                    snapshot.children.forEach {
                        it.getValue(User::class.java)?.let { user ->
                            pasienList.add(user)
                        }
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
