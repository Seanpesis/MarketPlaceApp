package com.example.marketplaceapp.viewmodel

import android.app.AlertDialog
import android.location.Location
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketplaceapp.R
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.databinding.ItemMarketBinding

class MarketAdapter(
    private val onItemClick: (MarketItem) -> Unit,
    private val onDeleteClick: (MarketItem) -> Unit,
    private var userLocation: Location?
) : ListAdapter<MarketItem, MarketAdapter.MarketViewHolder>(DiffCallback()) {

    fun updateUserLocation(location: Location) {
        userLocation = location
        notifyDataSetChanged() // Re-bind all items to update distances
    }

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
                    val item = getItem(adapterPosition)
                    val context = itemView.context
                    AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.delete_dialog_title))
                        .setMessage(context.getString(R.string.delete_dialog_message, item.title))
                        .setPositiveButton(context.getString(R.string.delete_confirm)) { _, _ ->
                            onDeleteClick(item)
                        }
                        .setNegativeButton(context.getString(R.string.cancel), null)
                        .show()
                }
            }
        }

        fun bind(item: MarketItem) {
            val context = itemView.context
            binding.tvTitle.text = item.title
            binding.tvPrice.text = context.getString(R.string.price_format, item.price.toString())

            // Display distance
            if (userLocation != null && item.latitude != null && item.longitude != null) {
                val itemLocation = Location("").apply {
                    latitude = item.latitude
                    longitude = item.longitude
                }
                val distanceInMeters = userLocation!!.distanceTo(itemLocation)
                val distanceInKm = distanceInMeters / 1000
                binding.tvDistance.text = String.format("%.1f km away", distanceInKm)
                binding.tvDistance.visibility = View.VISIBLE
            } else {
                binding.tvDistance.visibility = View.GONE
            }

            // Load image
            val imageUri = item.imageUri
            if (imageUri != null) {
                Glide.with(context)
                    .load(Uri.parse(imageUri))
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