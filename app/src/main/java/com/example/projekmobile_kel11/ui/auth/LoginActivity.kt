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
        val userRef = db.child("users").child(uid)

        userRef.get()
            .addOnSuccessListener { snapshot ->

                // 🔹 Kalau user BELUM ADA di Realtime DB
                if (!snapshot.exists()) {
                    val firebaseUser = auth.currentUser!!

                    val now = System.currentTimeMillis()
                    val userData = mapOf(
                        "name" to (firebaseUser.displayName ?: ""),
                        "email" to (firebaseUser.email ?: ""),
                        "role" to "user", // default
                        "gender" to "",
                        "age" to 0,
                        "phone" to "",
                        "photoUrl" to "",
                        "createdAt" to now,
                        "updatedAt" to now
                    )

                    userRef.setValue(userData)
                        .addOnSuccessListener {
                            openMainByRole("user")
                        }

                } else {
                    val role = snapshot.child("role").getValue(String::class.java)

                    when (role) {
                        "admin" -> openAdmin()
                        "doctor", "user" -> openMain()
                        else -> toast("Role tidak valid")
                    }
                }
            }
            .addOnFailureListener {
                toast("Gagal mengambil data user")
            }
    }

    private fun openAdmin() {
        startActivity(Intent(this, DashboardAdmin::class.java))
        finish()
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun openMainByRole(role: String) {
        when (role) {
            "admin" -> openAdmin()
            else -> openMain()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
