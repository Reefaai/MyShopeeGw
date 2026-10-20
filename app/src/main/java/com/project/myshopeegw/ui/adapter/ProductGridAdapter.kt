package com.project.myshopeegw.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.project.myshopeegw.R
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.databinding.ItemProductCardBinding
import com.project.myshopeegw.utils.CurrencyHelper

class ProductGridAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductGridAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = CurrencyHelper.formatRupiah(product.price)
            binding.tvRating.text = product.rating.toString()
            binding.tvSoldCount.text = "${product.soldCount} terjual"

            if (product.discountPercent > 0) {
                binding.tvDiscountBadge.visibility = View.VISIBLE
                binding.tvDiscountBadge.text = "-${product.discountPercent}%"

                binding.tvOriginalPrice.visibility = View.VISIBLE
                binding.tvOriginalPrice.text = CurrencyHelper.formatRupiah(product.originalPrice)
                binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvDiscountBadge.visibility = View.GONE
                binding.tvOriginalPrice.visibility = View.GONE
            }

            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.bg_search_bar)
                .into(binding.imgProduct)

            binding.cardProduct.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
    }
}
