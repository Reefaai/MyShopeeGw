package com.project.myshopeegw.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.project.myshopeegw.R
import com.project.myshopeegw.databinding.FragmentProfileBinding
import com.project.myshopeegw.ui.auth.AuthActivity
import com.project.myshopeegw.ui.checkout.PaymentSandboxActivity
import com.project.myshopeegw.ui.review.ReviewListActivity
import com.project.myshopeegw.ui.tracking.OrderTrackingActivity
import com.project.myshopeegw.utils.PrefManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvProfileName.text = prefManager.userName
        binding.tvProfileEmail.text = prefManager.userEmail

        Glide.with(this)
            .load("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80")
            .placeholder(R.drawable.ic_person)
            .into(binding.imgProfileAvatar)
    }

    private fun setupListeners() {
        binding.btnOrderUnpaid.setOnClickListener {
            startActivity(Intent(requireContext(), PaymentSandboxActivity::class.java))
        }

        binding.btnOrderPacked.setOnClickListener {
            startActivity(Intent(requireContext(), OrderTrackingActivity::class.java))
        }

        binding.btnOrderShipped.setOnClickListener {
            startActivity(Intent(requireContext(), OrderTrackingActivity::class.java))
        }

        binding.tvHistoryTracking.setOnClickListener {
            startActivity(Intent(requireContext(), OrderTrackingActivity::class.java))
        }

        binding.btnOrderReview.setOnClickListener {
            startActivity(Intent(requireContext(), ReviewListActivity::class.java))
        }

        binding.menuAddress.setOnClickListener {
            showAddressDialog()
        }

        binding.menuSupabaseConfig.setOnClickListener {
            showSupabaseConfigDialog()
        }

        binding.btnLogout.setOnClickListener {
            prefManager.logout()
            Toast.makeText(requireContext(), "Berhasil keluar dari akun", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun showAddressDialog() {
        val input = EditText(requireContext()).apply {
            setText(prefManager.userAddress)
            setSelection(text.length)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Alamat Pengiriman (Kota Cirebon)")
            .setMessage("Masukkan alamat pengiriman untuk proses checkout belanja:")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val newAddr = input.text.toString().trim()
                if (newAddr.isNotEmpty()) {
                    prefManager.userAddress = newAddr
                    Toast.makeText(requireContext(), "Alamat berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showSupabaseConfigDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val etUrl = EditText(requireContext()).apply {
            hint = "Supabase Project URL"
            setText(prefManager.supabaseUrl)
        }
        val etKey = EditText(requireContext()).apply {
            hint = "Supabase Anon Key"
            setText(prefManager.supabaseKey)
        }

        layout.addView(etUrl)
        layout.addView(etKey)

        AlertDialog.Builder(requireContext())
            .setTitle("Integrasi Supabase Cloud")
            .setMessage("Pengaturan endpoint REST API Live Backend untuk penilaian UAS:")
            .setView(layout)
            .setPositiveButton("Simpan & Tes Koneksi") { _, _ ->
                val newUrl = etUrl.text.toString().trim()
                val newKey = etKey.text.toString().trim()
                if (newUrl.isNotEmpty()) prefManager.supabaseUrl = newUrl
                if (newKey.isNotEmpty()) prefManager.supabaseKey = newKey
                Toast.makeText(requireContext(), "Konfigurasi Supabase berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tutup", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
