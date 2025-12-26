package com.example.marketplaceapp.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.databinding.ItemCartBinding

class CartAdapter(
    private val onDeleteClick: (MarketItem) -> Unit
) : ListAdapter<MarketItem, CartAdapter.CartViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, onDeleteClick)
    }

    class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MarketItem, onDeleteClick: (MarketItem) -> Unit) {
            binding.tvCartItemTitle.text = item.title
            binding.tvCartItemPrice.text = "${item.price} $"
            binding.btnDeleteFromCart.setOnClickListener { onDeleteClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MarketItem>() {
        override fun areItemsTheSame(oldItem: MarketItem, newItem: MarketItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MarketItem, newItem: MarketItem) = oldItem == newItem
    }
}