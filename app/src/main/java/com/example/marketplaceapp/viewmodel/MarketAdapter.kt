package com.example.marketplaceapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.databinding.ItemMarketBinding

class MarketAdapter(
    private val onItemClick: (MarketItem) -> Unit,
    private val onDeleteClick: (MarketItem) -> Unit
) : ListAdapter<MarketItem, MarketAdapter.MarketViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val binding = ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class MarketViewHolder(private val binding: ItemMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(adapterPosition))
                }
            }
            binding.btnDelete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: MarketItem) {
            binding.tvTitle.text = item.title
            binding.tvPrice.text = "${item.price} $"

            if (item.imageUri != null) {
                Glide.with(itemView.context)
                    .load(Uri.parse(item.imageUri))
                    .into(binding.ivItemImage)
            } else {
                binding.ivItemImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MarketItem>() {
        override fun areItemsTheSame(oldItem: MarketItem, newItem: MarketItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MarketItem, newItem: MarketItem) = oldItem == newItem
    }
}