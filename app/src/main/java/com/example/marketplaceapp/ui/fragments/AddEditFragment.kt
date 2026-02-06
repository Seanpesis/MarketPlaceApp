package com.example.marketplaceapp.ui.fragments

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.marketplaceapp.R
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.databinding.FragmentAddEditBinding
import com.example.marketplaceapp.viewmodel.MarketViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()
    private val args: AddEditFragmentArgs by navArgs()

    private var selectedImageUri: Uri? = null
    private var itemLocation: Location? = null
    private var currentItem: MarketItem? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUri ->
            selectedImageUri = imageUri
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(imageUri, flags)
            } catch (e: SecurityException) {
                Log.e("AddEditFragment", "Failed to take persistable permission for URI", e)
            }
            Glide.with(this).load(imageUri).into(binding.ivPreview)
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

        val categories = arrayOf(
            getString(R.string.category_books),
            getString(R.string.category_clothing),
            getString(R.string.category_art),
            getString(R.string.category_technology)
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        val isEditMode = args.itemId != null

        if (isEditMode) {
            args.itemId?.let { itemId ->
                viewModel.getItem(itemId).observe(viewLifecycleOwner) { item ->
                    item?.let { currentItemData ->
                        currentItem = currentItemData
                        binding.etTitle.setText(currentItemData.title)
                        binding.etDescription.setText(currentItemData.description)
                        binding.etPrice.setText(currentItemData.price.toString())
                        binding.etPhone.setText(currentItemData.contactPhone)

                        if (currentItemData.imageUri != null) {
                            selectedImageUri = currentItemData.imageUri.toUri()
                            Glide.with(this).load(selectedImageUri).into(binding.ivPreview)
                        }

                        if (currentItemData.latitude != null && currentItemData.longitude != null) {
                            binding.tvLocationStatus.text = getString(R.string.location_added_already)
                            itemLocation = Location("").apply {
                                latitude = currentItemData.latitude
                                longitude = currentItemData.longitude
                            }
                        }

                        val categoryIndex = categories.indexOf(currentItemData.category)
                        if (categoryIndex >= 0) {
                            binding.spinnerCategory.setSelection(categoryIndex)
                        }
                    }
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAddLocation.setOnClickListener {
            viewModel.currentLocation.value?.let { location ->
                itemLocation = location
                binding.tvLocationStatus.text = getString(R.string.location_added)
                Toast.makeText(context, getString(R.string.current_location_attached), Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(context, getString(R.string.location_not_available), Toast.LENGTH_SHORT).show()
        }

        binding.btnSave.setOnClickListener {
            saveItem(isEditMode)
        }
    }

    private fun saveItem(isEditMode: Boolean) {
        binding.btnSave.isEnabled = false 
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val selectedCategory = binding.spinnerCategory.selectedItem.toString()

        if (title.isBlank() || desc.isBlank() || priceStr.isBlank() || phone.isBlank()) {
            Toast.makeText(context, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            binding.btnSave.isEnabled = true
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0

        if (selectedImageUri != null && selectedImageUri.toString().startsWith("http").not()) {
            val imagePath = getString(R.string.product_images, System.currentTimeMillis())
            val storageRef = FirebaseStorage.getInstance().reference.child(imagePath)

            selectedImageUri?.let { imageToUpload ->
                storageRef.putFile(imageToUpload).addOnSuccessListener { _ -> // uploadTaskSnapshot
                    storageRef.downloadUrl.addOnSuccessListener { downloadedUri: Uri ->
                        val publicImageUrl = downloadedUri.toString()
                        performSave(isEditMode, title, desc, price, phone, selectedCategory, publicImageUrl)
                    }
                }.addOnFailureListener { exception ->
                    Log.e("AddEditFragment", "Image upload failed", exception)
                    Toast.makeText(context, getString(R.string.image_upload_failed), Toast.LENGTH_SHORT).show()
                    binding.btnSave.isEnabled = true
                }
            }
        } else {
            val imageUriToSave = selectedImageUri?.toString()
            performSave(isEditMode, title, desc, price, phone, selectedCategory, imageUriToSave)
        }
    }


    private fun performSave(isEditMode: Boolean, title: String, desc: String, price: Double, phone: String, category: String, imageUri: String?) {
        val itemToSave = MarketItem(
            id = if (isEditMode) currentItem!!.id else "",
            title = title,
            description = desc,
            price = price,
            contactPhone = phone,
            imageUri = imageUri,
            category = category,
            latitude = itemLocation?.latitude,
            longitude = itemLocation?.longitude
        )

        lifecycleScope.launch {
            val success = if (isEditMode) {
                viewModel.update(itemToSave)
            } else {
                viewModel.insert(itemToSave)
            }

            if (success) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Save failed. Check logs for details.", Toast.LENGTH_LONG).show()
                binding.btnSave.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}