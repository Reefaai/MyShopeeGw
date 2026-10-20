package com.project.myshopeegw.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.project.myshopeegw.R
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.databinding.ItemCheckoutItemBinding
import com.project.myshopeegw.utils.CurrencyHelper

class CheckoutItemAdapter : ListAdapter<CartItem, CheckoutItemAdapter.CheckoutViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ItemCheckoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CheckoutViewHolder(private val binding: ItemCheckoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.tvCheckoutName.text = item.productName
            binding.tvCheckoutVariant.text = "Variasi: ${item.variant}"
            binding.tvCheckoutPriceQty.text = "${CurrencyHelper.formatRupiah(item.price)} x ${item.quantity}"

            Glide.with(binding.root.context)
                .load(item.productImage)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.bg_search_bar)
                .into(binding.imgCheckoutThumb)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean = oldItem == newItem
    }
}
