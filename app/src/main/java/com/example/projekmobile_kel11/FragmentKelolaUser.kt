package com.example.projekmobile_kel11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.adapters.UserAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaUserBinding
import com.example.projekmobile_kel11.data.model.User
import com.google.firebase.database.*

class KelolaUserFragment : Fragment() {

    private var _binding: FragmentKelolaUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var userAdapter: UserAdapter

    private val userList = mutableListOf<User>()
    private val filteredList = mutableListOf<User>()

    // Pagination
    private val pageSize = 10
    private var lastKey: String? = null
    private var isLoading = false
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelolaUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("users")
        binding.fabAddUser.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TambahUserFragment())
                .addToBackStack(null)
                .commit()
        }

        setupRecyclerView()
        loadUsers()
        setupSearch()
        setupPagination()
    }

    // ---------------- RecyclerView ----------------
    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            mutableListOf(),
            onDeleteClick = { confirmDelete(it) }
        )

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    // ---------------- Load Users (Pagination) ----------------
    private fun loadUsers() {
        if (isLoading || isSearching) return
        isLoading = true

        var query = database.orderByKey().limitToFirst(pageSize)
        lastKey?.let {
            query = database.orderByKey()
                .startAfter(it)
                .limitToFirst(pageSize)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    isLoading = false
                    return
                }

                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java) ?: continue

                    // ❗ Hindari duplikasi
                    if (userList.none { it.userId == user.userId }) {
                        userList.add(user)
                    }
                    lastKey = data.key
                }

                userAdapter.updateData(userList)
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    // ---------------- Pagination Scroll ----------------
    private fun setupPagination() {
        binding.rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return

                val lm = rv.layoutManager as LinearLayoutManager
                if (!isLoading && !isSearching &&
                    lm.findLastVisibleItemPosition() >= userList.size - 2
                ) {
                    loadUsers()
                }
            }
        })
    }

    // ---------------- Search ----------------
    private fun setupSearch() {
        binding.searchViewUser.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val keyword = newText.orEmpty()
                isSearching = keyword.isNotEmpty()

                filteredList.clear()

                if (keyword.isEmpty()) {
                    userAdapter.updateData(userList)
                    return true
                }

                for (user in userList) {
                    if (
                        user.nama.contains(keyword, true) ||
                        user.email.contains(keyword, true)
                    ) {
                        filteredList.add(user)
                    }
                }

                userAdapter.updateData(filteredList)
                return true
            }
        })
    }

    // ---------------- Delete ----------------
    private fun confirmDelete(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus User")
            .setMessage("Yakin ingin menghapus ${user.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                database.child(user.userId).removeValue()
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
