package com.project.myshopeegw.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.databinding.FragmentHomeBinding
import com.project.myshopeegw.ui.adapter.CategoryAdapter
import com.project.myshopeegw.ui.adapter.ProductGridAdapter
import com.project.myshopeegw.ui.cart.CartActivity
import com.project.myshopeegw.ui.product.ProductDetailActivity
import com.project.myshopeegw.ui.product.ProductListActivity
import com.project.myshopeegw.utils.Resource
import com.project.myshopeegw.viewmodel.CartViewModel
import com.project.myshopeegw.viewmodel.ProductViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    private lateinit var productAdapter: ProductGridAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var flashSaleTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupListeners()
        observeViewModels()
        startFlashSaleCountdown()
    }

    private fun setupRecyclerViews() {
        productAdapter = ProductGridAdapter { product ->
            openProductDetail(product)
        }
        binding.rvHomeProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvHomeProducts.adapter = productAdapter

        productViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter = CategoryAdapter(categories, isGridMode = false) { category ->
                productViewModel.selectCategory(category.id)
            }
            binding.rvHomeCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvHomeCategories.adapter = categoryAdapter
        }
    }

    private fun setupListeners() {
        binding.layoutSearchBar.setOnClickListener {
            startActivity(Intent(requireContext(), ProductListActivity::class.java))
        }

        binding.btnHeaderCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        binding.tvSeeAllProducts.setOnClickListener {
            startActivity(Intent(requireContext(), ProductListActivity::class.java))
        }

        binding.swipeRefreshHome.setOnRefreshListener {
            productViewModel.loadProducts()
            cartViewModel.loadCart()
        }
    }

    private fun observeViewModels() {
        productViewModel.products.observe(viewLifecycleOwner) { resource ->
            binding.swipeRefreshHome.isRefreshing = false
            when (resource) {
                is Resource.Loading -> {
                    binding.progressHomeProducts.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressHomeProducts.visibility = View.GONE
                    productAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressHomeProducts.visibility = View.GONE
                }
            }
        }

        cartViewModel.cartState.observe(viewLifecycleOwner) { resource ->
            val count = resource.data?.size ?: 0
            if (count > 0) {
                binding.tvCartBadge.visibility = View.VISIBLE
                binding.tvCartBadge.text = count.toString()
            } else {
                binding.tvCartBadge.visibility = View.GONE
            }
        }
    }

    private fun startFlashSaleCountdown() {
        flashSaleTimer = object : CountDownTimer(3 * 3600 * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60
                _binding?.tvFlashSaleTimer?.text = String.format("%02d : %02d : %02d", hours, minutes, seconds)
            }
            override fun onFinish() {}
        }.start()
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra(ProductDetailActivity.EXTRA_PRODUCT, product)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        flashSaleTimer?.cancel()
        _binding = null
    }
}
