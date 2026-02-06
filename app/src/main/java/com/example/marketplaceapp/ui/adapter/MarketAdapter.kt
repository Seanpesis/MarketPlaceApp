package com.example.marketplaceapp.ui.adapter

import android.annotation.SuppressLint
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketplaceapp.R
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.databinding.ItemMarketBinding

class MarketAdapter(
    private val onItemClick: (MarketItem) -> Unit,
    private val onAddToCartClick: (MarketItem) -> Unit,
    private var userLocation: Location?
) : ListAdapter<MarketItem, MarketAdapter.MarketViewHolder>(DiffCallback()) {

    fun updateUserLocation(location: Location) {
        userLocation = location
        if (itemCount > 0) {
            notifyItemRangeChanged(0, itemCount)
        }
    }

    @SuppressLint("InflateParams")
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
            binding.btnAddToCart.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onAddToCartClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: MarketItem) {
            val context = itemView.context
            binding.tvTitle.text = item.title
            binding.tvPrice.text = context.getString(R.string.price_format, item.price.toString())

            val imageUri = item.imageUri
            if (imageUri != null) {
                Glide.with(context)
                    .load(imageUri.toUri())
                    .placeholder(R.drawable.market_icon)
                    .error(R.drawable.market_icon)
                    .into(binding.ivItemImage)
            } else {
                binding.ivItemImage.setImageResource(R.drawable.market_icon)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MarketItem>() {
        override fun areItemsTheSame(oldItem: MarketItem, newItem: MarketItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MarketItem, newItem: MarketItem) = oldItem == newItem
    }
}