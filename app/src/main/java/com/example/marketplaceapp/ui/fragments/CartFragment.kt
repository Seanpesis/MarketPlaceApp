package com.example.marketplaceapp.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.databinding.FragmentCartBinding
import com.example.marketplaceapp.ui.adapter.CartAdapter
import com.example.marketplaceapp.viewmodel.MarketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()
    private var checkoutDialog: Dialog? = null
    private var emptyCartDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        setupCheckoutDialog()
        setupEmptyCartDialog()
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
            if (isNetworkAvailable()) {
                if ((viewModel.cartItems.value?.size ?: 0) == 0) {
                    showEmptyCartAnimation()
                } else {
                    showConfirmationDialog()
                }
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCheckoutDialog() {
        val dialog = Dialog(requireContext(), R.style.Theme_MarketplaceApp_Dialog_Transparent)
        dialog.setContentView(R.layout.dialog_checkout)
        dialog.setCancelable(false)
        checkoutDialog = dialog
    }

    private fun setupEmptyCartDialog() {
        val dialog = Dialog(requireContext(), R.style.Theme_MarketplaceApp_Dialog_Transparent)
        dialog.setContentView(R.layout.dialog_empty_cart)
        dialog.setCancelable(false)
        emptyCartDialog = dialog
    }

    private fun showEmptyCartAnimation() {
        lifecycleScope.launch {
            emptyCartDialog?.show()
            delay(2000)
            emptyCartDialog?.dismiss()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    private fun showConfirmationDialog() {
        val totalItems = viewModel.cartItems.value?.sumOf { it.quantity } ?: 0
        val totalPrice = viewModel.cartItems.value?.sumOf { it.item.price * it.quantity } ?: 0.0

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.checkout_summary)
            .setMessage(getString(R.string.checkout_details, totalItems, totalPrice))
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                performCheckout()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun performCheckout() {
        lifecycleScope.launch {
            checkoutDialog?.show()
            viewModel.clearCart()
            delay(2500)
            checkoutDialog?.dismiss()
            Toast.makeText(context, "Checkout successful!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        checkoutDialog?.dismiss()
        emptyCartDialog?.dismiss()
        _binding = null
    }
}