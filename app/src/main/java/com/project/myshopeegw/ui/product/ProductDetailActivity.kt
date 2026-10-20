package com.project.myshopeegw.ui.product

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.project.myshopeegw.R
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.databinding.ActivityProductDetailBinding
import com.project.myshopeegw.ui.cart.CartActivity
import com.project.myshopeegw.ui.checkout.CheckoutActivity
import com.project.myshopeegw.ui.review.ReviewListActivity
import com.project.myshopeegw.utils.CurrencyHelper
import com.project.myshopeegw.viewmodel.CartViewModel

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val cartViewModel: CartViewModel by viewModels()

    private var product: Product? = null
    private var selectedVariant: String = ""

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product = intent.getSerializableExtra(EXTRA_PRODUCT) as? Product

        if (product == null) {
            Toast.makeText(this, "Data produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupProductView(product!!)
        setupListeners(product!!)
        observeCart()
    }

    private fun setupProductView(p: Product) {
        binding.tvDetailName.text = p.name
        binding.tvDetailPrice.text = CurrencyHelper.formatRupiah(p.price)
        binding.tvDetailRating.text = p.rating.toString()
        binding.tvDetailSold.text = "${p.soldCount} terjual"
        binding.tvDetailDescription.text = p.description

        if (p.discountPercent > 0) {
            binding.tvDetailDiscount.visibility = View.VISIBLE
            binding.tvDetailDiscount.text = "-${p.discountPercent}%"

            binding.tvDetailOriginalPrice.visibility = View.VISIBLE
            binding.tvDetailOriginalPrice.text = CurrencyHelper.formatRupiah(p.originalPrice)
            binding.tvDetailOriginalPrice.paintFlags = binding.tvDetailOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.tvDetailDiscount.visibility = View.GONE
            binding.tvDetailOriginalPrice.visibility = View.GONE
        }

        Glide.with(this)
            .load(p.imageUrl)
            .placeholder(R.drawable.bg_search_bar)
            .into(binding.imgDetailMain)

        // Setup Variant Chips with vibrant visual selection states
        binding.chipGroupVariants.isSingleSelection = true
        binding.chipGroupVariants.isSelectionRequired = true
        binding.chipGroupVariants.removeAllViews()

        val variants = if (p.variants.isNotEmpty()) p.variants else listOf("Default")
        selectedVariant = variants.first()

        val bgStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.accent_matcha),
                ContextCompat.getColor(this, R.color.bg_card)
            )
        )

        val strokeStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.color_primary),
                ContextCompat.getColor(this, R.color.border_light)
            )
        )

        val textStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(this, R.color.color_primary),
                ContextCompat.getColor(this, R.color.text_secondary)
            )
        )

        variants.forEachIndexed { index, variantName ->
            val chip = Chip(this).apply {
                id = View.generateViewId()
                text = variantName
                isCheckable = true
                isChecked = index == 0
                chipBackgroundColor = bgStateList
                chipStrokeColor = strokeStateList
                chipStrokeWidth = 3f
                setTextColor(textStateList)

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedVariant = variantName
                        Toast.makeText(this@ProductDetailActivity, "Varian dipilih: $variantName", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            binding.chipGroupVariants.addView(chip)
        }
    }

    private fun setupListeners(p: Product) {
        binding.btnDetailBack.setOnClickListener {
            finish()
        }

        binding.btnDetailCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.cardReviewLink.setOnClickListener {
            startActivity(Intent(this, ReviewListActivity::class.java))
        }

        // Operation 4: Tambah ke Keranjang (POST API)
        binding.btnAddToCart.setOnClickListener {
            cartViewModel.addToCart(p, selectedVariant, 1)
            Toast.makeText(this, "✓ Ditambahkan ke keranjang ($selectedVariant)", Toast.LENGTH_SHORT).show()
        }

        // Beli Sekarang -> Add to cart & Direct to Checkout
        binding.btnBuyNow.setOnClickListener {
            cartViewModel.addToCart(p, selectedVariant, 1)
            val intent = Intent(this, CheckoutActivity::class.java).apply {
                val directItem = CartItem(
                    id = "direct-1",
                    productId = p.id,
                    productName = p.name,
                    productImage = p.imageUrl,
                    price = p.price,
                    quantity = 1,
                    variant = selectedVariant,
                    isSelected = true
                )
                putExtra(CheckoutActivity.EXTRA_DIRECT_ITEMS, arrayListOf(directItem))
            }
            startActivity(intent)
        }
    }

    private fun observeCart() {
        cartViewModel.cartState.observe(this) { resource ->
            val count = resource.data?.size ?: 0
            if (count > 0) {
                binding.tvDetailCartBadge.visibility = View.VISIBLE
                binding.tvDetailCartBadge.text = count.toString()
            } else {
                binding.tvDetailCartBadge.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.loadCart()
    }
}
