package com.example.projekmobile_kel11.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekmobile_kel11.adapters.UserAdapter
import com.example.projekmobile_kel11.databinding.FragmentKelolaUserBinding
import com.example.projekmobile_kel11.data.model.User
import com.google.firebase.database.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.projekmobile_kel11.R


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

        setupRecyclerView()
        loadUsers()
        setupSearch()
        setupPagination()
    }

    // ---------------- RecyclerView ----------------
    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            mutableListOf(),
            onDeleteClick = { userId ->
                val user = userList.find { it.userId == userId }
                user?.let {
                    showDeleteConfirmation(it)
                }
            }
        )

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = userAdapter

        // Animasi list
        binding.rvUsers.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(
                requireContext(),
                R.anim.layout_fade_slide
            )
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

                    // ✅ HANYA USER (PASlEN)
                    if (user.role != "user") continue

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

    private fun hapusUser(user: User) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(user.userId)
            .removeValue()
            .addOnSuccessListener {
                userList.removeAll { it.userId == user.userId }
                filteredList.removeAll { it.userId == user.userId }
                userAdapter.updateData(
                    if (isSearching) filteredList else userList
                )

                Toast.makeText(
                    requireContext(),
                    "User berhasil dihapus",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Gagal menghapus user",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showDeleteConfirmation(user: User) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus User")
            .setMessage("Yakin ingin menghapus ${user.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                hapusUser(user)
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
