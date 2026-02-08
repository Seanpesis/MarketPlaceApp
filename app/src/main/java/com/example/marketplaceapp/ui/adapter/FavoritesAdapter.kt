package com.example.marketplaceapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketplaceapp.R
import com.example.marketplaceapp.data.FavoriteItem
import com.example.marketplaceapp.databinding.ItemMarketBinding

class FavoritesAdapter(
    private val onItemClick: (FavoriteItem) -> Unit,
    private val onDeleteClick: (FavoriteItem) -> Unit
) : ListAdapter<FavoriteItem, FavoritesAdapter.FavoritesViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class FavoritesViewHolder(private val binding: ItemMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(adapterPosition))
                }
            }
            binding.btnAddToCart.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: FavoriteItem) {
            val context = itemView.context
            binding.tvTitle.text = item.name
            binding.tvPrice.text = context.getString(R.string.price_format, item.price.toString())

            binding.btnFavorite.visibility = View.GONE
            binding.btnAddToCart.setIconResource(android.R.drawable.ic_menu_delete)

            val imageUri = item.imageUrl
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

    class DiffCallback : DiffUtil.ItemCallback<FavoriteItem>() {
        override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem) = oldItem == newItem
    }
}

