package com.project.myshopeegw.ui.checkout

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.project.myshopeegw.data.model.Order
import com.project.myshopeegw.databinding.ActivityPaymentSandboxBinding
import com.project.myshopeegw.ui.tracking.OrderTrackingActivity
import com.project.myshopeegw.utils.CurrencyHelper
import com.project.myshopeegw.utils.Resource
import com.project.myshopeegw.viewmodel.CheckoutViewModel

class PaymentSandboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSandboxBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()

    private var currentOrder: Order? = null
    private var paymentCountdown: CountDownTimer? = null

    companion object {
        const val EXTRA_ORDER = "extra_order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSandboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentOrder = (intent.getSerializableExtra(EXTRA_ORDER) as? Order) ?: checkoutViewModel.getLastOrder()

        setupViews()
        setupListeners()
        observePaymentState()
        startTimer()
    }

    private fun setupViews() {
        val total = currentOrder?.totalAmount ?: 5427000L
        binding.tvPaymentTotalAmount.text = CurrencyHelper.formatRupiah(total)
        binding.tvVaNumber.text = currentOrder?.vaNumber ?: "123 456 7890"
    }

    private fun setupListeners() {
        binding.btnPaymentBack.setOnClickListener {
            finish()
        }

        binding.btnCopyVa.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Nomor VA BCA", binding.tvVaNumber.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Nomor Virtual Account disalin ke clipboard", Toast.LENGTH_SHORT).show()
        }

        // Operation 7: Pembayaran Sandbox (Cek Status Pembayaran)
        binding.btnSimulatePayment.setOnClickListener {
            val orderId = currentOrder?.id ?: "ord-default"
            checkoutViewModel.simulatePayment(orderId)
        }
    }

    private fun observePaymentState() {
        checkoutViewModel.paymentState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSimulatePayment.isEnabled = false
                    binding.btnSimulatePayment.text = "Memverifikasi Pelunasan..."
                }
                is Resource.Success -> {
                    showSuccessDialog()
                }
                is Resource.Error -> {
                    binding.btnSimulatePayment.isEnabled = true
                    binding.btnSimulatePayment.text = "Cek Status Pembayaran"
                    Toast.makeText(this, resource.message ?: "Verifikasi gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Pembayaran Berhasil! ✓")
            .setMessage("Virtual Account BCA (123 456 7890) berhasil dilunasi. Pesananmu segera dikemas dan dikirimkan oleh kurir Logistik Cirebon.")
            .setCancelable(false)
            .setPositiveButton("Lihat Tracking Pesanan") { _, _ ->
                val intent = Intent(this, OrderTrackingActivity::class.java).apply {
                    putExtra(OrderTrackingActivity.EXTRA_ORDER, currentOrder)
                }
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun startTimer() {
        paymentCountdown = object : CountDownTimer(24 * 3600 * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.tvPaymentTimer.text = String.format("%02d : %02d : %02d", hours, minutes, seconds)
            }
            override fun onFinish() {}
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        paymentCountdown?.cancel()
    }
}
