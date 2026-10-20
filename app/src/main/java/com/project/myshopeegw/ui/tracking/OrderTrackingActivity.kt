package com.project.myshopeegw.ui.tracking

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.myshopeegw.MainActivity
import com.project.myshopeegw.data.model.Order
import com.project.myshopeegw.databinding.ActivityOrderTrackingBinding
import com.project.myshopeegw.utils.PrefManager

class OrderTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderTrackingBinding

    companion object {
        const val EXTRA_ORDER = "extra_order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = PrefManager(this)
        val order = intent.getSerializableExtra(EXTRA_ORDER) as? Order

        val resi = order?.courierResi ?: "JNE-CRB-8839210"
        val courier = order?.shippingType ?: "JNE Express Reguler (Kota Cirebon)"
        val address = order?.shippingAddress ?: pref.userAddress

        binding.tvTrackingResi.text = resi
        binding.tvTrackingCourierName.text = courier
        binding.tvTrackingAddress.text = address

        binding.btnTrackingBack.setOnClickListener {
            finish()
        }

        binding.btnCopyResi.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Resi Pengiriman", resi)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Nomor Resi disalin ke clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
