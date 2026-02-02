package com.example.marketplaceapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.data.CartManager
import com.example.marketplaceapp.databinding.FragmentCartBinding
import com.example.marketplaceapp.ui.adapter.CartAdapter
import com.example.marketplaceapp.viewmodel.MarketViewModel

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()

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
            viewModel.removeFromCart(it)
        }
        binding.rvCartItems.layoutManager = LinearLayoutManager(context)
        binding.rvCartItems.adapter = cartAdapter

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items.toList())
            val totalPrice = items.sumOf { it.item.price * it.quantity }
            binding.tvTotalPrice.text = getString(R.string.total_price_format, totalPrice)
        }

        binding.btnCheckout.setOnClickListener {
            showCheckoutDialog()
        }
    }

    private fun showCheckoutDialog() {

        val totalItems = CartManager.totalItemsCount.value ?: 0
        val totalPrice = CartManager.getTotalPrice()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.checkout_summary)

            .setMessage(getString(R.string.checkout_details, totalItems, totalPrice))
            .setPositiveButton(R.string.ok) { dialog, _ ->
                CartManager.clearCart()
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}