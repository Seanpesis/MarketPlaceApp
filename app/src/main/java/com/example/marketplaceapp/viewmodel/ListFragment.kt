package com.example.marketplaceapp.viewmodel

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.data.CartManager
import com.example.marketplaceapp.databinding.FragmentListBinding
import com.google.android.material.snackbar.Snackbar

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
        setHasOptionsMenu(true) // Enable options menu
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MarketAdapter(
            onItemClick = { item ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { item ->
                viewModel.delete(item)
            },
            onAddToCartClick = { item ->
                CartManager.addToCart(item)
                Snackbar.make(binding.root, "${item.title} added to cart", Snackbar.LENGTH_SHORT).show()
            },
            userLocation = viewModel.currentLocation.value
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.sortedItems.observe(viewLifecycleOwner) { items ->
            items?.let { adapter.submitList(it) }
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            location?.let { adapter.updateUserLocation(it) }
        }

        binding.fabAdd.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddEditFragment(-1L)
            findNavController().navigate(action)
        }

        CartManager.cartItems.observe(viewLifecycleOwner) { cartItems ->
            updateCartBadge(cartItems.size)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        val cartItem = menu.findItem(R.id.action_cart)
        val actionView = cartItem.actionView
        cartBadge = actionView?.findViewById(R.id.cart_badge)

        actionView?.setOnClickListener {
            onOptionsItemSelected(cartItem)
        }

        updateCartBadge(CartManager.cartItems.value?.size ?: 0)
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