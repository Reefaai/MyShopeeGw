package com.project.myshopeegw.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.myshopeegw.R
import com.project.myshopeegw.data.model.Category
import com.project.myshopeegw.databinding.ItemCategoryChipBinding
import com.project.myshopeegw.databinding.ItemCategoryGridBinding

class CategoryAdapter(
    private val categories: List<Category>,
    private val isGridMode: Boolean = false,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedCategoryId: String = "all"

    fun setSelectedCategory(categoryId: String) {
        selectedCategoryId = categoryId
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridMode) VIEW_TYPE_GRID else VIEW_TYPE_CHIP
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_GRID) {
            val binding = ItemCategoryGridBinding.inflate(inflater, parent, false)
            CategoryGridViewHolder(binding)
        } else {
            val binding = ItemCategoryChipBinding.inflate(inflater, parent, false)
            CategoryChipViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = categories[position]
        if (holder is CategoryChipViewHolder) {
            holder.bind(category)
        } else if (holder is CategoryGridViewHolder) {
            holder.bind(category)
        }
    }

    inner class CategoryChipViewHolder(private val binding: ItemCategoryChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            val context = binding.root.context
            val isSelected = category.id == selectedCategoryId

            binding.tvCategoryName.text = category.name
            binding.imgCategoryIcon.setImageResource(category.iconRes)

            if (isSelected) {
                binding.layoutCategoryChip.setBackgroundResource(R.drawable.bg_chip_matcha)
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.accent_forest))
                binding.imgCategoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.accent_forest))
            } else {
                binding.layoutCategoryChip.setBackgroundResource(R.drawable.bg_chip_neutral)
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                binding.imgCategoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_primary))
            }

            binding.layoutCategoryChip.setOnClickListener {
                selectedCategoryId = category.id
                notifyDataSetChanged()
                onCategoryClick(category)
            }
        }
    }

    inner class CategoryGridViewHolder(private val binding: ItemCategoryGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.tvCategoryGridName.text = category.name
            binding.imgCategoryGridIcon.setImageResource(category.iconRes)
            binding.tvCategoryGridCount.text = "${category.itemCount * 25}+ Produk"

            binding.cardCategoryGrid.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    companion object {
        const val VIEW_TYPE_CHIP = 0
        const val VIEW_TYPE_GRID = 1
    }
}
