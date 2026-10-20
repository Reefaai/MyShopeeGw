package com.project.myshopeegw.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.myshopeegw.R
import com.project.myshopeegw.data.model.PaymentMethod
import com.project.myshopeegw.databinding.ItemPaymentMethodBinding

class PaymentMethodAdapter(
    private var methods: List<PaymentMethod>,
    private var selectedId: String,
    private val onSelect: (PaymentMethod) -> Unit
) : RecyclerView.Adapter<PaymentMethodAdapter.PaymentViewHolder>() {

    fun updateData(newMethods: List<PaymentMethod>, newSelectedId: String) {
        this.methods = newMethods
        this.selectedId = newSelectedId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(methods[position])
    }

    override fun getItemCount(): Int = methods.size

    inner class PaymentViewHolder(private val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(method: PaymentMethod) {
            val isSelected = method.id == selectedId

            binding.tvPaymentBadge.text = method.badgeText
            binding.tvMethodName.text = method.name
            binding.tvMethodCode.text = method.codeOrVa
            binding.tvMethodDesc.text = "${method.category} • ${method.description}"

            if (isSelected) {
                binding.imgSelectedCheck.visibility = View.VISIBLE
                binding.cardPaymentItem.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.accent_forest)
                binding.cardPaymentItem.strokeWidth = 3
                binding.tvPaymentBadge.setBackgroundResource(R.drawable.bg_button_primary)
                binding.tvPaymentBadge.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.white)
                )
            } else {
                binding.imgSelectedCheck.visibility = View.GONE
                binding.cardPaymentItem.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.border_light)
                binding.cardPaymentItem.strokeWidth = 1
                binding.tvPaymentBadge.setBackgroundResource(R.drawable.bg_chip_matcha)
                binding.tvPaymentBadge.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.accent_forest)
                )
            }

            binding.cardPaymentItem.setOnClickListener {
                selectedId = method.id
                notifyDataSetChanged()
                onSelect(method)
            }
        }
    }
}
