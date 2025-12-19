package com.example.marketplaceapp.viewmodel

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        setHasOptionsMenu(true) // Enable options menu
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pass the initial user location to the adapter
        val adapter = MarketAdapter(
            onItemClick = { item ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { item ->
                // The dialog is handled in the adapter
                viewModel.delete(item)
            },
            userLocation = viewModel.currentLocation.value
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        // Observe the sorted items list
        viewModel.sortedItems.observe(viewLifecycleOwner) { items ->
            items?.let { adapter.submitList(it) }
        }

        // Observe the user's location and update the adapter when it changes
        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            location?.let { adapter.updateUserLocation(it) }
        }

        binding.fabAdd.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddEditFragment(-1L)
            findNavController().navigate(action)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}