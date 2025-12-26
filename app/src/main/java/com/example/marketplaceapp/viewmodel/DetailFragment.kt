package com.example.marketplaceapp.viewmodel

import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.marketplaceapp.R
import com.example.marketplaceapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getItem(args.itemId).observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.tvDetailTitle.text = it.title
                binding.tvDetailPrice.text = getString(R.string.price_format, it.price.toString())
                binding.tvDetailDescription.text = it.description
                binding.tvDetailPhone.text = it.contactPhone

                if (it.imageUri != null) {
                    Glide.with(this)
                        .load(Uri.parse(it.imageUri))
                        .placeholder(R.drawable.market_icon)
                        .error(R.drawable.market_icon)
                        .into(binding.ivDetailImage)
                } else {
                    binding.ivDetailImage.setImageResource(R.drawable.market_icon)
                }

                val userLocation = viewModel.currentLocation.value
                if (userLocation != null && it.latitude != null && it.longitude != null) {
                    val itemLocation = Location("").apply {
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                    val distanceInMeters = userLocation.distanceTo(itemLocation)
                    val distanceInKm = distanceInMeters / 1000
                    binding.tvDetailDistance.text = String.format("%.1f km away", distanceInKm)
                    binding.tvDetailDistance.visibility = View.VISIBLE
                } else {
                    binding.tvDetailDistance.visibility = View.GONE
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToAddEditFragment(args.itemId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}