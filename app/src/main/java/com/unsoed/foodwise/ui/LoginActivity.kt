package com.unsoed.foodwise.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.unsoed.foodwise.MainActivity
import com.unsoed.foodwise.data.UserProfile // Pastikan import UserProfile benar
import com.unsoed.foodwise.databinding.ActivityLoginBinding
import com.unsoed.foodwise.ui.onboarding.OnboardingActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        binding.loginBtn.setOnClickListener {
            loginUser()
        }

        binding.signupLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            checkUserProfile()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    checkUserProfile()
                } else {
                    Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkUserProfile() {
        profileViewModel.userProfile.observe(this, object : Observer<UserProfile?> {

            // FUNGSI WAJIB INI HARUS ADA DI DALAM KURUNG KURAWAL
            override fun onChanged(userProfile: UserProfile?) {
                // Hentikan pengamatan setelah mendapatkan data pertama kali
                profileViewModel.userProfile.removeObserver(this)

                if (userProfile == null) {
                    // JIKA DATA PROFIL KOSONG: Pengguna baru, arahkan ke Onboarding
                    val intent = Intent(this@LoginActivity, OnboardingActivity::class.java)
                    startActivity(intent)
                } else {
                    // JIKA DATA PROFIL ADA: Pengguna lama, arahkan ke Dashboard
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                finish() // Tutup LoginActivity agar tidak bisa kembali
            }
        })
    }
}