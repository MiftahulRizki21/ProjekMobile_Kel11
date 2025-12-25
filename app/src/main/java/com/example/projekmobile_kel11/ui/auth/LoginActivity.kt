package com.example.projekmobile_kel11.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobile_kel11.DashboardAdmin
import com.example.projekmobile_kel11.databinding.ActivityLoginBinding
import com.example.projekmobile_kel11.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast("Isi email dan password")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkRole(auth.currentUser!!.uid)
            }
            .addOnFailureListener {
                toast("Login gagal")
            }
    }

    private fun checkRole(uid: String) {
        db.child("users").child(uid).child("role").get()
            .addOnSuccessListener {
                when (it.value.toString()) {
                    "admin" -> {
                        startActivity(Intent(this, DashboardAdmin::class.java))
                    }
                    "doctor", "user" -> {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else -> toast("Role tidak valid")
                }
                finish()
            }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
