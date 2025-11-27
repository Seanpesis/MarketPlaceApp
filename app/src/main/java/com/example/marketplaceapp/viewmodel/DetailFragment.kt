package com.example.marketplaceapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.marketplaceapp.databinding.FragmentDetailBinding
import com.example.marketplaceapp.viewmodel.MarketViewModel

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by viewModels()
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
                binding.tvDetailPrice.text = "${it.price} $"
                binding.tvDetailDescription.text = it.description
                binding.tvDetailPhone.text = it.contactPhone

                if (it.imageUri != null) {
                    Glide.with(this)
                        .load(Uri.parse(it.imageUri))
                        .into(binding.ivDetailImage)
                } else {
                    binding.ivDetailImage.setImageResource(android.R.drawable.ic_menu_gallery)
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