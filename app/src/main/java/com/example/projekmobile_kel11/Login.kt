package com.example.projekmobile_kel11

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnTamu: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi view
        etUsername = findViewById(R.id.edit_username)
        etPassword = findViewById(R.id.edit_password)
        btnLogin = findViewById(R.id.btn_login)
        btnTamu = findViewById(R.id.btn_tamu)
        tvRegister = findViewById(R.id.txt_link_regis)

        // Klik tombol Login
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                // NANTI ganti dengan Firebase Auth
                Toast.makeText(this, "Login sukses (dummy)", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                // Bisa kirim info user kalau mau
                intent.putExtra("USERNAME", username)
                startActivity(intent)
                finish()
            }
        }

        // Klik tombol Masuk sebagai Tamu
        btnTamu.setOnClickListener {
            Toast.makeText(this, "Masuk sebagai tamu", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("IS_GUEST", true)
            startActivity(intent)
            finish()
        }

        // Klik teks Registrasi
        tvRegister.setOnClickListener {
            // NANTI: ganti ke Intent ke RegisterActivity
            Toast.makeText(this, "Pindah ke halaman Registrasi (belum dibuat)", Toast.LENGTH_SHORT).show()
            // Contoh nanti:
            // val intent = Intent(this, Register::class.java)
            // startActivity(intent)
        }
    }
}
