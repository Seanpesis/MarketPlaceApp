package com.example.marketplaceapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.databinding.FragmentListBinding
import com.example.marketplaceapp.ui.adapter.MarketAdapter
import com.example.marketplaceapp.viewmodel.MarketViewModel

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()
    private var cartBadge: TextView? = null

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

        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            updateCartBadge(cartItems.sumOf { it.quantity })
        }

        binding.fabAdd.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddEditFragment(null)
            findNavController().navigate(action)
        }

        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == View.NO_ID) {
                viewModel.setFilter("All")
            } else {

                when (checkedId) {
                    R.id.btnFilterAll -> viewModel.setFilter("All")
                    R.id.btnFilterBooks -> viewModel.setFilter("Books")
                    R.id.btnFilterClothing -> viewModel.setFilter("Clothing")
                    R.id.btnFilterArt -> viewModel.setFilter("Art")
                    R.id.btnFilterTechnology -> viewModel.setFilter("Technology")
                }
            }
        }
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

    private fun updateCartBadge(count: Int) {
        if (count > 0) {
            cartBadge?.visibility = View.VISIBLE
            cartBadge?.text = count.toString()
        } else {
            cartBadge?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}