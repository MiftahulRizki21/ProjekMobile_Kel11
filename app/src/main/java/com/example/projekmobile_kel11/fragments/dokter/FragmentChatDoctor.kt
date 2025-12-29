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

        binding.btnSend.setOnClickListener { sendMessage() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler() {
        adapter = ChatAdapter(messages)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.adapter = adapter
    }

    private fun sendMessage() {
        val text = binding.edtMessage.text.toString().trim()
        if (text.isEmpty()) return

        val msg = ChatMessage(
            senderId = "doctorId",
            message = text,
            timestamp = System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(consultationId)
            .push()
            .setValue(msg)

        binding.edtMessage.setText("")
    }

    private fun listenMessages() {
        FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(consultationId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    snapshot.children.forEach {
                        it.getValue(ChatMessage::class.java)?.let(messages::add)
                    }
                    adapter.notifyDataSetChanged()
                    if (messages.isNotEmpty())
                        binding.rvChat.scrollToPosition(messages.size - 1)
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
}
