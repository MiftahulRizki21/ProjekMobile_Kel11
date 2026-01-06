package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.adapters.ChatAdapter
import com.example.projekmobile_kel11.data.model.ChatMessage
import com.example.projekmobile_kel11.databinding.FragmentChatDoctorBinding
import com.example.projekmobile_kel11.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentChatDoctor : Fragment() {

    private var _binding: FragmentChatDoctorBinding? = null
    private val binding get() = _binding!!

    private lateinit var consultationId: String
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private var otherName: String = "..." // sementara sebelum load

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        consultationId = arguments?.getString(ARG_CONSULTATION_ID) ?: ""
        Log.d("CHAT_DEBUG", "consultationId = $consultationId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDoctorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (consultationId.isEmpty()) {
            Toast.makeText(requireContext(), "Chat tidak valid", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }

        setupToolbar()

        // ✅ Init adapter awal supaya listenMessages aman
        val myId = auth.currentUser?.uid ?: return
        adapter = ChatAdapter(messages, myId, otherName)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.rvChat.layoutManager = layoutManager
        binding.rvChat.adapter = adapter

        listenMessages()      // aman, adapter sudah ada
        loadChatTitle()       // update otherName nanti
        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    // 🔥 Load nama lawan bicara sesuai user/dokter
    private fun loadChatTitle() {
        val currentUserId = auth.currentUser?.uid ?: return
        val consultationRef = FirebaseDatabase.getInstance()
            .getReference("consultations")
            .child(consultationId)

        consultationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userId = snapshot.child("userId").getValue(String::class.java) ?: return
                val doctorId = snapshot.child("doctorId").getValue(String::class.java) ?: return

                val otherId = if (currentUserId == doctorId) userId else doctorId
                loadUserName(otherId)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadUserName(userId: String) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    otherName = snapshot.child("nama").getValue(String::class.java)
                        ?: snapshot.child("name").getValue(String::class.java) ?: "Pasien"

                    binding.tvChatTitle.text = otherName
                    adapter.updateOtherName(otherName) // update nama lawan di adapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendMessage() {
        val text = binding.edtMessage.text.toString().trim()
        if (text.isEmpty()) return

        val senderId = auth.currentUser?.uid ?: return
        val chatRef = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(consultationId)
            .push()

        val msg = ChatMessage(
            messageId = chatRef.key ?: "",
            senderId = senderId,
            message = text,
            timestamp = System.currentTimeMillis(),
            status = "sent"
        )

        chatRef.setValue(msg)
            .addOnSuccessListener {
                updateLastMessage(text)
                binding.edtMessage.setText("")
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal mengirim pesan", Toast.LENGTH_SHORT).show()
            }

    }

    private fun listenMessages() {
        FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(consultationId)
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    snapshot.children.forEach {
                        it.getValue(ChatMessage::class.java)?.let(messages::add)
                    }
                    adapter.notifyDataSetChanged()
                    if (messages.isNotEmpty()) binding.rvChat.scrollToPosition(messages.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onResume() {
        super.onResume()

        // reset unread untuk dokter
        FirebaseDatabase.getInstance()
            .getReference("consultations")
            .child(consultationId)
            .child("unreadCountDoctor")
            .setValue(0)

        // update status pesan
        FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(consultationId)
            .orderByChild("status")
            .equalTo("delivered")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        it.ref.child("status").setValue("read")
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun updateLastMessage(message: String) {
        val updateMap = mapOf(
            "lastMessage" to message,
            "lastTimestamp" to System.currentTimeMillis()
        )
        FirebaseDatabase.getInstance()
            .getReference("consultations")
            .child(consultationId)
            .updateChildren(updateMap)
    }

    companion object {
        private const val ARG_CONSULTATION_ID = "consultationId"

        fun newInstance(consultationId: String): FragmentChatDoctor {
            return FragmentChatDoctor().apply {
                arguments = Bundle().apply {
                    putString(ARG_CONSULTATION_ID, consultationId)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as? MainActivity)?.showBottomNav()

    }
}
