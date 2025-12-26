package com.example.marketplaceapp.viewmodel

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btnFilterAll -> viewModel.setFilter(null)
                R.id.btnFilterBooks -> viewModel.setFilter("Books")
                R.id.btnFilterClothing -> viewModel.setFilter("Clothing")
                R.id.btnFilterArt -> viewModel.setFilter("Art")
                R.id.btnFilterTechnology -> viewModel.setFilter("Technology")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                findNavController().navigate(ListFragmentDirections.actionListFragmentToAboutFragment())
                true
            }
            R.id.action_cart -> {
                findNavController().navigate(ListFragmentDirections.actionListFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}