package com.example.marketplaceapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.databinding.FragmentAddEditBinding
import com.example.marketplaceapp.viewmodel.MarketViewModel

class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()
    private val args: AddEditFragmentArgs by navArgs()

    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            // This is critical for persistent access to the image URI
            try {
                val flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(it, flags)
            } catch (e: SecurityException) {
                // This can happen on some devices/OS versions. We'll proceed without it.
                e.printStackTrace()
            }
            Glide.with(this).load(it).into(binding.ivPreview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isEditMode = args.itemId != -1L

        if (isEditMode) {
            viewModel.getItem(args.itemId).observe(viewLifecycleOwner) { item ->
                item?.let {
                    binding.etTitle.setText(it.title)
                    binding.etDescription.setText(it.description)
                    binding.etPrice.setText(it.price.toString())
                    binding.etPhone.setText(it.contactPhone)

                    if (it.imageUri != null) {
                        selectedImageUri = Uri.parse(it.imageUri)
                        Glide.with(this).load(selectedImageUri).into(binding.ivPreview)
                    }
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveItem(isEditMode)
        }
    }

    private fun saveItem(isEditMode: Boolean) {
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (title.isBlank() || desc.isBlank() || priceStr.isBlank() || phone.isBlank()) {
            Toast.makeText(context, R.string.fill_fields, Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null) {
            // Optionally, show a more specific error for invalid price
            Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
            return
        }
        
        val imageString = selectedImageUri?.toString()

        val item = MarketItem(
            id = if (isEditMode) args.itemId else 0,
            title = title,
            description = desc,
            price = price,
            contactPhone = phone,
            imageUri = imageString
        )

        if (isEditMode) {
            viewModel.update(item)
        } else {
            viewModel.insert(item)
        }

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}