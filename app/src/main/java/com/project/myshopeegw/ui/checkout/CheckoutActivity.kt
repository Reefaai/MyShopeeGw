package com.project.myshopeegw.ui.checkout

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.databinding.ActivityCheckoutBinding
import com.project.myshopeegw.ui.adapter.CheckoutItemAdapter
import com.project.myshopeegw.utils.CurrencyHelper
import com.project.myshopeegw.utils.PrefManager
import com.project.myshopeegw.utils.Resource
import com.project.myshopeegw.viewmodel.CheckoutViewModel

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()

    private lateinit var itemAdapter: CheckoutItemAdapter
    private lateinit var prefManager: PrefManager

    companion object {
        const val EXTRA_DIRECT_ITEMS = "extra_direct_items"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        @Suppress("UNCHECKED_CAST")
        val directItems = intent.getSerializableExtra(EXTRA_DIRECT_ITEMS) as? ArrayList<CartItem> ?: arrayListOf()

        setupViews()
        setupListeners()
        observeViewModel()

        checkoutViewModel.prepareCheckout(directItems)
    }

    private fun setupViews() {
        binding.tvRecipientInfo.text = "${prefManager.userName} (${prefManager.userPhone})"
        binding.tvFullAddress.text = prefManager.userAddress

        itemAdapter = CheckoutItemAdapter()
        binding.rvCheckoutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutItems.adapter = itemAdapter
    }

    private fun setupListeners() {
        binding.btnCheckoutBack.setOnClickListener {
            finish()
        }

        binding.btnChangeAddress.setOnClickListener {
            showChangeAddressDialog()
        }

        // Operation 6: Checkout Pesanan (Memilih opsi kurir Reguler / Express)
        binding.rgShipping.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbReguler.id) {
                checkoutViewModel.setShippingOption("Reguler (JNE Cirebon)", 15000L)
            } else {
                checkoutViewModel.setShippingOption("Express (SiCepat Cirebon)", 25000L)
            }
        }

        binding.btnSubmitOrder.setOnClickListener {
            checkoutViewModel.submitOrder()
        }
    }

    private fun observeViewModel() {
        checkoutViewModel.checkoutItems.observe(this) { items ->
            itemAdapter.submitList(items)
        }

        checkoutViewModel.subtotal.observe(this) { sub ->
            binding.tvCheckoutSubtotal.text = CurrencyHelper.formatRupiah(sub)
        }

        checkoutViewModel.shippingCost.observe(this) { cost ->
            binding.tvCheckoutShippingCost.text = CurrencyHelper.formatRupiah(cost)
        }

        checkoutViewModel.totalAmount.observe(this) { total ->
            binding.tvCheckoutTotalAmount.text = CurrencyHelper.formatRupiah(total)
            binding.tvBottomTotal.text = CurrencyHelper.formatRupiah(total)
        }

        checkoutViewModel.orderState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSubmitOrder.isEnabled = false
                    binding.btnSubmitOrder.text = "Memproses Pesanan..."
                }
                is Resource.Success -> {
                    val order = resource.data
                    if (order != null) {
                        val intent = Intent(this, PaymentSandboxActivity::class.java).apply {
                            putExtra(PaymentSandboxActivity.EXTRA_ORDER, order)
                        }
                        startActivity(intent)
                        finish()
                    }
                }
                is Resource.Error -> {
                    binding.btnSubmitOrder.isEnabled = true
                    binding.btnSubmitOrder.text = "Bayar Sekarang"
                    Toast.makeText(this, resource.message ?: "Gagal membuat pesanan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showChangeAddressDialog() {
        val input = EditText(this).apply {
            setText(prefManager.userAddress)
            setSelection(text.length)
        }

        AlertDialog.Builder(this)
            .setTitle("Ubah Alamat Pengiriman (Kota Cirebon)")
            .setView(input)
            .setPositiveButton("Gunakan Alamat Ini") { _, _ ->
                val newAddr = input.text.toString().trim()
                if (newAddr.isNotEmpty()) {
                    prefManager.userAddress = newAddr
                    binding.tvFullAddress.text = newAddr
                    checkoutViewModel.shippingAddress.value = newAddr
                    Toast.makeText(this, "Alamat diperbarui", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
