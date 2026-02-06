package com.example.marketplaceapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.databinding.FragmentListBinding
import com.example.marketplaceapp.ui.adapter.MarketAdapter
import com.example.marketplaceapp.viewmodel.MarketViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MarketAdapter(
            onItemClick = { item ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            },
            onAddToCartClick = { item ->
                viewModel.addToCart(item)
            },
            userLocation = viewModel.currentLocation.value
        )

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = adapter

        viewModel.finalItemList.observe(viewLifecycleOwner) { items ->
            items?.let { adapter.submitList(it) }
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            location?.let { adapter.updateUserLocation(it) }
        }

        binding.fabAdd.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddEditFragment(null)
            findNavController().navigate(action)
        }

        @Suppress("DEPRECATION")
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val category = when (checkedId) {
                R.id.btnFilterBooks -> "Books"
                R.id.btnFilterClothing -> "Clothing"
                R.id.btnFilterArt -> "Art"
                R.id.btnFilterTechnology -> "Technology"
                else -> "All"
            }
            viewModel.setFilter(category)
        }
        binding.chipGroup.check(R.id.btnFilterAll)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}