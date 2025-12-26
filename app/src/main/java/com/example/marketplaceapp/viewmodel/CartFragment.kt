package com.example.marketplaceapp.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplaceapp.databinding.FragmentCartBinding

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
            val totalPrice = items.sumOf { it.price }
            binding.tvTotalPrice.text = "$${String.format("%.2f", totalPrice)}"
        }

        binding.btnCheckout.setOnClickListener {
            showCheckoutDialog()
        }
    }

    private fun showCheckoutDialog() {
        val paymentMethods = arrayOf("Cash", "Credit Card")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Payment Method")
            .setItems(paymentMethods) { dialog, which ->
                val paymentMethod = paymentMethods[which]
                showConfirmationDialog(paymentMethod)
                dialog.dismiss()
            }
            .show()
    }

    private fun showConfirmationDialog(paymentMethod: String) {
        val orderId = (100000..999999).random()
        AlertDialog.Builder(requireContext())
            .setTitle("Payment Successful")
            .setMessage("Your order (ID: #$orderId) has been placed successfully using $paymentMethod.")
            .setPositiveButton("OK") { dialog, _ ->
                viewModel.clearCart()
                dialog.dismiss()
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}