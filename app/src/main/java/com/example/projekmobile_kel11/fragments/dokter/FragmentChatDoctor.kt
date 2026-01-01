package com.example.projekmobile_kel11.fragments.dokter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.adapters.ChatAdapter
import com.example.projekmobile_kel11.data.model.ChatMessage
import com.example.projekmobile_kel11.databinding.FragmentChatDoctorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentChatDoctor : Fragment() {

    private var _binding: FragmentChatDoctorBinding? = null
    private val binding get() = _binding!!

    private lateinit var consultationId: String
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    val auth = FirebaseAuth.getInstance()
    val doctorId = auth.currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        consultationId = arguments?.getString(ARG_CONSULTATION_ID) ?: ""
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDoctorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (consultationId.isEmpty()) {
            Toast.makeText(requireContext(), "Chat tidak valid", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        setupRecycler()
        listenMessages()
        loadChatTitle()
        setupToolbar()

        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler() {
        val doctorId = auth.currentUser?.uid
            ?: throw IllegalStateException("Doctor belum login")

        adapter = ChatAdapter(messages, doctorId)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true

        binding.rvChat.layoutManager = layoutManager
        binding.rvChat.adapter = adapter
    }
    private fun loadChatTitle() {
        val consultationRef = FirebaseDatabase.getInstance()
            .getReference("consultations")
            .child(consultationId)

        consultationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userId = snapshot.child("userId").getValue(String::class.java)
                    ?: return

                loadUserName(userId)
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
                    val name =
                        snapshot.child("name").getValue(String::class.java)
                            ?: snapshot.child("nama").getValue(String::class.java)
                            ?: "Pasien"

                    binding.tvChatTitle.text = name
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun sendMessage() {
        val text = binding.edtMessage.text.toString().trim()
        if (text.isEmpty()) return

        val senderId = auth.currentUser?.uid
        if (senderId == null) {
            Toast.makeText(requireContext(), "Dokter belum login", Toast.LENGTH_SHORT).show()
            return
        }

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
            .orderByChild("timestamp") // 🔥 INI KUNCI UTAMANYA
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    snapshot.children.forEach {
                        it.getValue(ChatMessage::class.java)?.let(messages::add)
                    }
                    adapter.notifyDataSetChanged()

                    if (messages.isNotEmpty()) {
                        binding.rvChat.scrollToPosition(messages.size - 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }
    override fun onResume() {
        super.onResume()

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

}
