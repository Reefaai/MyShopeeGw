package com.project.myshopeegw.ui.review

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myshopeegw.databinding.ActivityReviewListBinding
import com.project.myshopeegw.ui.adapter.ReviewAdapter
import com.project.myshopeegw.viewmodel.ProductViewModel

class ReviewListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewListBinding
    private val productViewModel: ProductViewModel by viewModels()
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeReviews()
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.rvReviewList.layoutManager = LinearLayoutManager(this)
        binding.rvReviewList.adapter = reviewAdapter
    }

    private fun setupListeners() {
        binding.btnReviewBack.setOnClickListener {
            finish()
        }
    }

    private fun observeReviews() {
        productViewModel.reviews.observe(this) { reviews ->
            reviewAdapter.submitList(reviews)
        }
    }
}
