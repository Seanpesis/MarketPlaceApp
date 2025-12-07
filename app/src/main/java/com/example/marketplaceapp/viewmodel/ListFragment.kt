package com.example.marketplaceapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.databinding.FragmentListBinding
import com.example.marketplaceapp.viewmodel.MarketViewModel

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by viewModels()
    private var fullItemList: List<MarketItem> = emptyList()

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
            onDeleteClick = { item ->
                showDeleteDialog(item)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            fullItemList = items
            adapter.submitList(items)
        }

        binding.fabAdd.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddEditFragment(-1L)
            findNavController().navigate(action)
        }

        binding.btnFilterAll.setOnClickListener { 
            (binding.recyclerView.adapter as MarketAdapter).submitList(fullItemList)
        }

        binding.btnFilterBooks.setOnClickListener { 
            filterList("Books")
        }

        binding.btnFilterClothing.setOnClickListener { 
            filterList("Clothing")
        }

        binding.btnFilterArt.setOnClickListener { 
            filterList("Art")
        }

        binding.btnFilterTechnology.setOnClickListener { 
            filterList("Technology")
        }
    }

    private fun filterList(category: String) {
        val filtered = fullItemList.filter { it.category == category }
        (binding.recyclerView.adapter as MarketAdapter).submitList(filtered)
    }

    private fun showDeleteDialog(item: com.example.marketplaceapp.data.MarketItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirm)
            .setMessage(R.string.delete_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.delete(item)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}