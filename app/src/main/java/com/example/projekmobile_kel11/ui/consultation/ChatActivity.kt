package com.example.projekmobile_kel11.ui.consultation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobile_kel11.R
import com.example.projekmobile_kel11.fragments.dokter.FragmentChatDoctor

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val consultationId =
            intent.getStringExtra("consultationId") ?: run {
                finish()
                return
            }

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.chat_container, // container di activity_chat.xml
                FragmentChatDoctor.newInstance(consultationId)
            )
            .commit()
    }
}
