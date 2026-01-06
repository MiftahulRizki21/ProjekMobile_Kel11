package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

    private lateinit var adapter: DoctorChatListAdapter
    private lateinit var doctorId: String

    // 🔥 Data sumber
    private val chatList = mutableListOf<Consultation>()
    private val filteredChatList = mutableListOf<Consultation>()

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

        adapter = DoctorChatListAdapter(mutableListOf()) { consultation ->
            val bundle = bundleOf(
                "consultationId" to consultation.consultationId
            )
            findNavController().navigate(
                R.id.fragmentChatDoctor,
                bundle
            )
        }

        binding.rvChatList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatList.adapter = adapter

        setupSearch()
        loadChats()
    }

    // ===============================
    // LOAD CHAT DARI FIREBASE
    // ===============================
    private fun loadChats() {
        if (doctorId.isEmpty()) return

        val db = FirebaseDatabase.getInstance()
        val consultationRef = db.getReference("consultations")
        val userRef = db.getReference("users")

        consultationRef
            .orderByChild("doctorId")
            .equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()
                    val userIds = mutableSetOf<String>()

                    snapshot.children.forEach { snap ->
                        val chat = snap.getValue(Consultation::class.java) ?: return@forEach
                        chat.consultationId = snap.key ?: ""
                        chatList.add(chat)
                        userIds.add(chat.userId)
                    }

                    if (userIds.isEmpty()) {
                        adapter.updateData(emptyList())
                        return
                    }

                    loadUsersForChats(userIds, chatList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
    private fun loadUsersForChats(
        userIds: Set<String>,
        chats: MutableList<Consultation>
    ) {
        val userRef = FirebaseDatabase.getInstance().getReference("users")
        val userMap = mutableMapOf<String, String>()

        var loaded = 0

        userIds.forEach { uid ->
            userRef.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.child("name").getValue(String::class.java)
                            ?: "Pasien"

                        userMap[uid] = name

                        loaded++
                        if (loaded == userIds.size) {
                            // 🔥 JOIN DI SINI
                            chats.forEach { chat ->
                                chat.userName = userMap[chat.userId] ?: "Pasien"
                            }

                            adapter.updateData(chats.toList())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        loaded++
                    }
                })
        }
    }


    // ===============================
    // SEARCH CHAT
    // ===============================
    private fun setupSearch() {
        binding.searchChat.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val keyword = newText.orEmpty().trim()

                filteredChatList.clear()

                if (keyword.isEmpty()) {
                    adapter.updateData(chatList.toList())
                    return true
                }

                for (chat in chatList) {
                    if (
                        chat.lastMessage.contains(keyword, true) ||
                        chat.consultationId.contains(keyword, true)
                    ) {
                        filteredChatList.add(chat)
                    }
                }

                adapter.updateData(filteredChatList.toList())
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
