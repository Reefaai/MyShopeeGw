package com.project.myshopeegw.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.databinding.ActivityCartBinding
import com.project.myshopeegw.ui.adapter.CartAdapter
import com.project.myshopeegw.ui.checkout.CheckoutActivity
import com.project.myshopeegw.utils.CurrencyHelper
import com.project.myshopeegw.utils.Resource
import com.project.myshopeegw.viewmodel.CartViewModel

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter
    private var currentItems: List<CartItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.loadCart()
    }

    private fun setupRecyclerView() {
        // Operation 5: Ubah Jumlah Produk di Keranjang (Full CRUD)
        cartAdapter = CartAdapter(
            onIncrease = { item -> cartViewModel.increaseQuantity(item) },
            onDecrease = { item -> cartViewModel.decreaseQuantity(item) },
            onDelete = { item -> cartViewModel.deleteItem(item) },
            onToggleSelect = { item, isSelected -> cartViewModel.toggleSelect(item, isSelected) }
        )
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun setupListeners() {
        binding.btnCartBack.setOnClickListener {
            finish()
        }

        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            cartViewModel.toggleSelectAll(isChecked)
        }

        binding.btnProceedCheckout.setOnClickListener {
            val selectedItems = currentItems.filter { it.isSelected }
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Pilih minimal 1 barang untuk checkout", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putExtra(CheckoutActivity.EXTRA_DIRECT_ITEMS, ArrayList(selectedItems))
            }
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        cartViewModel.cartState.observe(this) { resource ->
            if (resource is Resource.Success) {
                currentItems = resource.data ?: emptyList()
                cartAdapter.submitList(currentItems.toList())

                binding.tvCartItemCountHeader.text = "${currentItems.size} Barang"

                if (currentItems.isEmpty()) {
                    binding.layoutCartEmpty.visibility = View.VISIBLE
                    binding.rvCartItems.visibility = View.GONE
                    binding.layoutSelectAll.visibility = View.GONE
                    binding.btnProceedCheckout.isEnabled = false
                } else {
                    binding.layoutCartEmpty.visibility = View.GONE
                    binding.rvCartItems.visibility = View.VISIBLE
                    binding.layoutSelectAll.visibility = View.VISIBLE
                    binding.btnProceedCheckout.isEnabled = true
                }
            }
        }

        cartViewModel.totalPrice.observe(this) { total ->
            binding.tvCartTotalPrice.text = CurrencyHelper.formatRupiah(total)
        }

        cartViewModel.selectedCount.observe(this) { count ->
            binding.btnProceedCheckout.text = "Checkout ($count)"
            binding.btnProceedCheckout.isEnabled = count > 0
        }

        cartViewModel.actionMessage.observe(this) { msg ->
            if (msg.isNotBlank()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
