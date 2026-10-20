package com.project.myshopeegw.ui.checkout

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.project.myshopeegw.R
import com.project.myshopeegw.data.api.PaymentMethodProvider
import com.project.myshopeegw.data.model.Order
import com.project.myshopeegw.data.model.PaymentMethod
import com.project.myshopeegw.databinding.ActivityPaymentSandboxBinding
import com.project.myshopeegw.databinding.DialogAddCustomPaymentBinding
import com.project.myshopeegw.databinding.LayoutBottomSheetPaymentBinding
import com.project.myshopeegw.ui.adapter.PaymentMethodAdapter
import com.project.myshopeegw.ui.tracking.OrderTrackingActivity
import com.project.myshopeegw.utils.CurrencyHelper
import com.project.myshopeegw.utils.Resource
import com.project.myshopeegw.viewmodel.CheckoutViewModel

class PaymentSandboxActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSandboxBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()

    private var currentOrder: Order? = null
    private var currentMethod: PaymentMethod = PaymentMethodProvider.getDefault()
    private var paymentCountdown: CountDownTimer? = null

    companion object {
        const val EXTRA_ORDER = "extra_order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSandboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentOrder = (intent.getSerializableExtra(EXTRA_ORDER) as? Order) ?: checkoutViewModel.getLastOrder()

        currentMethod = PaymentMethodProvider.getByNameOrVa(
            currentOrder?.paymentMethod,
            currentOrder?.vaNumber
        )

        setupViews()
        setupListeners()
        observePaymentState()
        startTimer()
    }

    private fun setupViews() {
        val total = currentOrder?.totalAmount ?: 5427000L
        binding.tvPaymentTotalAmount.text = CurrencyHelper.formatRupiah(total)
        renderPaymentMethod(currentMethod)
    }

    private fun renderPaymentMethod(method: PaymentMethod) {
        currentMethod = method
        currentOrder?.paymentMethod = method.name
        currentOrder?.vaNumber = method.codeOrVa

        binding.tvPaymentBadge.text = method.badgeText
        binding.tvPaymentMethodTitle.text = method.name
        binding.tvVaNumber.text = method.codeOrVa

        if (method.category == "Virtual Account") {
            binding.tvVaLabel.text = "Nomor Virtual Account:"
        } else if (method.category == "E-Wallet & QRIS") {
            binding.tvVaLabel.text = if (method.id == "qris") "NMID Merchant QRIS:" else "Nomor Akun E-Wallet:"
        } else {
            binding.tvVaLabel.text = "Kode Pembayaran / Tagihan:"
        }

        // Show QR code container only for QRIS
        if (method.id == "qris") {
            binding.layoutQrisContainer.visibility = View.VISIBLE
        } else {
            binding.layoutQrisContainer.visibility = View.GONE
        }

        binding.tvPaymentInstructionsTitle.text = "Petunjuk Pembayaran (${method.name}):"
        binding.tvPaymentInstructions.text = method.instructions.joinToString("\n")
    }

    private fun setupListeners() {
        binding.btnPaymentBack.setOnClickListener {
            finish()
        }

        binding.btnCopyVa.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Kode / Nomor Pembayaran", binding.tvVaNumber.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "${binding.tvVaNumber.text} disalin ke clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.btnSwitchPaymentMethod.setOnClickListener {
            showPaymentMethodBottomSheet()
        }

        // Operation 7: Pembayaran Sandbox (Cek Status Pembayaran)
        binding.btnSimulatePayment.setOnClickListener {
            val orderId = currentOrder?.id ?: "ord-default"
            val items = currentOrder?.items ?: emptyList()
            checkoutViewModel.simulatePayment(orderId, items)
        }
    }

    private fun showPaymentMethodBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        val sheetBinding = LayoutBottomSheetPaymentBinding.inflate(layoutInflater)
        bottomSheet.setContentView(sheetBinding.root)

        lateinit var adapter: PaymentMethodAdapter

        adapter = PaymentMethodAdapter(
            PaymentMethodProvider.methods,
            currentMethod.id
        ) { selectedMethod ->
            renderPaymentMethod(selectedMethod)
            Toast.makeText(this, "Metode pembayaran diubah ke: ${selectedMethod.name}", Toast.LENGTH_SHORT).show()
            bottomSheet.dismiss()
        }

        sheetBinding.rvPaymentMethods.layoutManager = LinearLayoutManager(this)
        sheetBinding.rvPaymentMethods.adapter = adapter

        sheetBinding.btnAddNewPaymentMethod.setOnClickListener {
            showAddCustomPaymentDialog { newMethod ->
                adapter.updateData(PaymentMethodProvider.methods, newMethod.id)
                renderPaymentMethod(newMethod)
                bottomSheet.dismiss()
            }
        }

        bottomSheet.show()
    }

    private fun showAddCustomPaymentDialog(onAdded: (PaymentMethod) -> Unit) {
        val dialogBinding = DialogAddCustomPaymentBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan & Gunakan") { _, _ ->
                val name = dialogBinding.etCustomMethodName.text.toString().trim()
                val code = dialogBinding.etCustomCodeOrVa.text.toString().trim()
                val badge = dialogBinding.etCustomBadge.text.toString().trim()

                if (name.isEmpty() || code.isEmpty()) {
                    Toast.makeText(this, "Nama dan nomor/kode wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val category = when (dialogBinding.rgCustomCategory.checkedRadioButtonId) {
                    R.id.rbCategoryWallet -> "E-Wallet & QRIS"
                    R.id.rbCategoryCash -> "Mitra & Tunai"
                    else -> "Virtual Account"
                }

                val newMethod = PaymentMethodProvider.addCustomMethod(name, code, category, badge)
                Toast.makeText(this, "Metode $name berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                onAdded(newMethod)
            }
            .setNegativeButton("Batal", null)
            .show()
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
        val totalStr = CurrencyHelper.formatRupiah(currentOrder?.totalAmount ?: 0L)
        AlertDialog.Builder(this)
            .setTitle("Pembayaran Berhasil! ✓")
            .setMessage("Pembayaran via ${currentMethod.name} (${currentMethod.codeOrVa}) sebesar $totalStr berhasil diverifikasi lunas.\n\nBarang yang dipesan telah dikeluarkan dari keranjang belanja. Pesananmu segera dikemas dan dikirimkan oleh kurir Logistik Cirebon.")
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
