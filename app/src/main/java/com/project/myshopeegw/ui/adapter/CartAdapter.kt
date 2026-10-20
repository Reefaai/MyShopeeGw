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
import com.project.myshopeegw.databinding.ItemCartItemBinding
import com.project.myshopeegw.utils.CurrencyHelper

class CartAdapter(
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onDelete: (CartItem) -> Unit,
    private val onToggleSelect: (CartItem, Boolean) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.tvCartProductName.text = item.productName
            binding.tvCartVariant.text = "Variasi: ${item.variant}"
            binding.tvCartPrice.text = CurrencyHelper.formatRupiah(item.price)
            binding.tvQuantity.text = item.quantity.toString()

            binding.cbSelect.setOnCheckedChangeListener(null)
            binding.cbSelect.isChecked = item.isSelected
            binding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked
                onToggleSelect(item, isChecked)
            }

            Glide.with(binding.root.context)
                .load(item.productImage)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.bg_search_bar)
                .into(binding.imgCartProduct)

            binding.btnIncrease.setOnClickListener {
                onIncrease(item)
            }

            binding.btnDecrease.setOnClickListener {
                onDecrease(item)
            }

            binding.btnDeleteCart.setOnClickListener {
                onDelete(item)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean = oldItem == newItem
    }
}
