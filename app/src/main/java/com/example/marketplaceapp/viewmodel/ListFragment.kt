package com.example.marketplaceapp.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplaceapp.ListFragmentDirections
import com.example.marketplaceapp.MarketAdapter
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
                // The confirmation dialog is handled inside the MarketAdapter.
                viewModel.delete(item)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        // Observe the sortedItems LiveData instead of allItems
        viewModel.sortedItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                adapter.submitList(it)
            }
        }

        binding.fabAdd.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddEditFragment(-1L)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}