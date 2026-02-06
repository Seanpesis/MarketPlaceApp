package com.example.marketplaceapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.marketplaceapp.R
import com.example.marketplaceapp.databinding.FragmentListBinding
import com.example.marketplaceapp.ui.adapter.MarketAdapter
import com.example.marketplaceapp.viewmodel.MarketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()
    private var addToCartDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        setupAddToCartDialog()
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
                showAndDismissAnimation()
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

    private fun setupAddToCartDialog() {
        val dialog = Dialog(requireContext(), R.style.Theme_MarketplaceApp_Dialog_Transparent)
        dialog.setContentView(R.layout.dialog_add_to_cart)
        dialog.setCancelable(false)
        addToCartDialog = dialog
    }

    private fun showAndDismissAnimation() {
        lifecycleScope.launch {
            addToCartDialog?.show()
            delay(1500)
            addToCartDialog?.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addToCartDialog?.dismiss()
        _binding = null
    }
}