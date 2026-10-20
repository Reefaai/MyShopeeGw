package com.project.myshopeegw.ui.product

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myshopeegw.databinding.ActivityProductListBinding
import com.project.myshopeegw.ui.adapter.CategoryAdapter
import com.project.myshopeegw.ui.adapter.ProductGridAdapter
import com.project.myshopeegw.ui.cart.CartActivity
import com.project.myshopeegw.utils.Resource
import com.project.myshopeegw.viewmodel.CartViewModel
import com.project.myshopeegw.viewmodel.ProductViewModel

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    private lateinit var productAdapter: ProductGridAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    companion object {
        const val EXTRA_CATEGORY_ID = "extra_category_id"
        const val EXTRA_CATEGORY_NAME = "extra_category_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val initialCategoryId = intent.getStringExtra(EXTRA_CATEGORY_ID) ?: "all"

        setupRecyclerViews(initialCategoryId)
        setupSearch()
        setupListeners()
        observeViewModels()

        productViewModel.selectCategory(initialCategoryId)
    }

    private fun setupRecyclerViews(initialCategory: String) {
        productAdapter = ProductGridAdapter { product ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra(ProductDetailActivity.EXTRA_PRODUCT, product)
            }
            startActivity(intent)
        }
        binding.rvProductList.layoutManager = GridLayoutManager(this, 2)
        binding.rvProductList.adapter = productAdapter

        productViewModel.categories.observe(this) { categories ->
            categoryAdapter = CategoryAdapter(categories, isGridMode = false) { category ->
                categoryAdapter.setSelectedCategory(category.id)
                productViewModel.selectCategory(category.id)
            }
            categoryAdapter.setSelectedCategory(initialCategory)
            binding.rvListCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvListCategories.adapter = categoryAdapter
        }
    }

    private fun setupSearch() {
        // Instant Real-time Search as required in PDF Bagian III No. 2
        binding.etSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                productViewModel.searchInstantly(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnListCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun observeViewModels() {
        productViewModel.products.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvResultCount.text = "Memuat produk..."
                }
                is Resource.Success -> {
                    val list = resource.data ?: emptyList()
                    productAdapter.submitList(list)
                    if (list.isEmpty()) {
                        binding.layoutEmptyState.visibility = View.VISIBLE
                        binding.rvProductList.visibility = View.GONE
                        binding.tvResultCount.text = "0 produk ditemukan"
                    } else {
                        binding.layoutEmptyState.visibility = View.GONE
                        binding.rvProductList.visibility = View.VISIBLE
                        val catLabel = if (productViewModel.currentCategory != "all") {
                            "kategori ${productViewModel.currentCategory}"
                        } else "semua kategori"
                        binding.tvResultCount.text = "Menampilkan ${list.size} produk ($catLabel)"
                    }
                }
                is Resource.Error -> {
                    binding.tvResultCount.text = "Gagal memuat produk"
                }
            }
        }

        cartViewModel.cartState.observe(this) { resource ->
            val count = resource.data?.size ?: 0
            if (count > 0) {
                binding.tvListCartBadge.visibility = View.VISIBLE
                binding.tvListCartBadge.text = count.toString()
            } else {
                binding.tvListCartBadge.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.loadCart()
    }
}
