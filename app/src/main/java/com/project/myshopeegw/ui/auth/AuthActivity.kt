package com.project.myshopeegw.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.project.myshopeegw.MainActivity
import com.project.myshopeegw.R
import com.project.myshopeegw.databinding.ActivityAuthBinding
import com.project.myshopeegw.utils.PrefManager

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = PrefManager(this)

        updateTabState()

        binding.btnTabLogin.setOnClickListener {
            if (!isLoginMode) {
                isLoginMode = true
                updateTabState()
            }
        }

        binding.btnTabRegister.setOnClickListener {
            if (isLoginMode) {
                isLoginMode = false
                updateTabState()
            }
        }

        binding.btnSubmitAuth.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString()?.trim() ?: ""

            if (email.isEmpty()) {
                binding.layoutEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }
            binding.layoutEmail.error = null

            if (password.length < 4) {
                binding.layoutPassword.error = "Password minimal 4 karakter"
                return@setOnClickListener
            }
            binding.layoutPassword.error = null

            pref.isLoggedIn = true
            pref.userEmail = email

            if (!isLoginMode) {
                val name = binding.etName.text?.toString()?.trim() ?: ""
                if (name.isNotEmpty()) pref.userName = name
                Toast.makeText(this, "Registrasi berhasil! Selamat datang, ${pref.userName}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Berhasil masuk!", Toast.LENGTH_SHORT).show()
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnGoogleLogin.setOnClickListener {
            pref.isLoggedIn = true
            pref.userName = "Mahasiswa IKMI (Google)"
            pref.userEmail = "mahasiswa.ikmi@gmail.com"
            Toast.makeText(this, "Masuk dengan Google berhasil!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnFacebookLogin.setOnClickListener {
            pref.isLoggedIn = true
            pref.userName = "Mahasiswa IKMI (FB)"
            Toast.makeText(this, "Masuk dengan Facebook berhasil!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvSkipLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateTabState() {
        if (isLoginMode) {
            binding.btnTabLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary))
            binding.btnTabLogin.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding.btnTabRegister.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.btnTabRegister.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

            binding.layoutName.visibility = View.GONE
            binding.tvAuthTitle.text = "Selamat Datang di ShopiKu"
            binding.tvAuthSubtitle.text = "Silakan masuk untuk melanjutkan transaksi belanjamu."
            binding.btnSubmitAuth.text = "Masuk ke Akun"
        } else {
            binding.btnTabRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary))
            binding.btnTabRegister.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding.btnTabLogin.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.btnTabLogin.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

            binding.layoutName.visibility = View.VISIBLE
            binding.tvAuthTitle.text = "Buat Akun Baru"
            binding.tvAuthSubtitle.text = "Daftarkan akunmu untuk mulai belanja di ShopiKu."
            binding.btnSubmitAuth.text = "Daftar Akun Sekarang"
        }
    }
}
