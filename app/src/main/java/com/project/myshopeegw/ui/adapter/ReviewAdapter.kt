package com.project.myshopeegw.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.project.myshopeegw.R
import com.project.myshopeegw.data.model.Review
import com.project.myshopeegw.databinding.ItemReviewItemBinding

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewViewHolder(private val binding: ItemReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.tvReviewUserName.text = review.userName
            binding.tvReviewDate.text = review.date
            binding.tvReviewVariant.text = "Variasi: ${review.variant}"
            binding.tvReviewComment.text = review.comment

            Glide.with(binding.root.context)
                .load(review.userAvatar)
                .placeholder(R.drawable.ic_person)
                .into(binding.imgUserAvatar)

            if (review.reviewImage.isNotBlank()) {
                binding.imgReviewPhoto.visibility = View.VISIBLE
                Glide.with(binding.root.context)
                    .load(review.reviewImage)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imgReviewPhoto)
            } else {
                binding.imgReviewPhoto.visibility = View.GONE
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem == newItem
    }
}
