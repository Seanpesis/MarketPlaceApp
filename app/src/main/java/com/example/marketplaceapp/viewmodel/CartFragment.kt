package com.example.marketplaceapp.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.data.CartManager
import com.example.marketplaceapp.databinding.FragmentCartBinding
import com.google.android.material.snackbar.Snackbar

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cartAdapter = CartAdapter {
            CartManager.removeFromCart(it)
            Snackbar.make(binding.root, "${it.item.title} removed from cart", Snackbar.LENGTH_SHORT).show()
        }

        binding.cartRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.cartRecyclerView.adapter = cartAdapter

        CartManager.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)
            updateTotalPrice()
        }

        binding.btnCheckout.setOnClickListener {
            val totalItems = CartManager.cartItems.value?.size ?: 0
            val totalPrice = CartManager.getTotalPrice()

            AlertDialog.Builder(requireContext())
                .setTitle(R.string.checkout_summary)
                .setMessage(getString(R.string.checkout_details, totalItems, totalPrice.toInt()))
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }

    private fun updateTotalPrice() {
        val totalPrice = CartManager.getTotalPrice()
        binding.tvTotalPrice.text = getString(R.string.total_price_format, totalPrice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}