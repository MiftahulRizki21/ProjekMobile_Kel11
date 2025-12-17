// di dalam file fragments/KelolaUserFragment.kt
package com.example.projekmobile_kel11.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.adapters.UserAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaUserBinding
import com.example.projekmobile_kel11.models.User

class KelolaUserFragment : Fragment() {
    private var _binding: FragmentKelolaUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKelolaUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.rvUsers.layoutManager = LinearLayoutManager(context)

        // Buat data dummy
        val dummyUserList = listOf(
            User("1", "Miftahul Rizki", "mifrizki@email.com"),
            User("2", "Budi Santoso", "budi.s@email.com"),
            User("3", "Citra Lestari", "citra.l@email.com"),
            User("4", "Dewi Anggraini", "dewi.a@email.com")
        )

        // Inisialisasi adapter
        val userAdapter = UserAdapter(dummyUserList)
        binding.rvUsers.adapter = userAdapter

        // TODO: Atur listener untuk FAB dan SearchView
        // binding.fabAddUser.setOnClickListener { ... }
        // binding.searchViewUser.setOnQueryTextListener(...)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
