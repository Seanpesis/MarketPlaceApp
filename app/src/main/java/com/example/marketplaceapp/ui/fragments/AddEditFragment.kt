package com.example.marketplaceapp.ui.fragments

import android.R
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase

class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MarketViewModel by activityViewModels()
    private val args: AddEditFragmentArgs by navArgs()

    private var selectedImageUri: Uri? = null
    private var itemLocation: Location? = null
    private var currentItem: MarketItem? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(it, flags)
            } catch (e: SecurityException) {
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

        val categories = arrayOf("Books", "Clothing", "Art", "Technology")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        val isEditMode = args.itemId != null

        if (isEditMode) {
            viewModel.getItem(args.itemId!!).observe(viewLifecycleOwner) { item ->
                item?.let {
                    currentItem = it
                    binding.etTitle.setText(it.title)
                    binding.etDescription.setText(it.description)
                    binding.etPrice.setText(it.price.toString())
                    binding.etPhone.setText(it.contactPhone)

                    if (it.imageUri != null) {
                        selectedImageUri = Uri.parse(it.imageUri)
                        Glide.with(this).load(selectedImageUri).into(binding.ivPreview)
                    }

                    if (it.latitude != null && it.longitude != null) {
                        binding.tvLocationStatus.text = "Location Added"
                        itemLocation = Location("").apply {
                            latitude = it.latitude!!
                            longitude = it.longitude!!
                        }
                    }

                    val categoryIndex = categories.indexOf(it.category)
                    if (categoryIndex >= 0) {
                        binding.spinnerCategory.setSelection(categoryIndex)
                    }
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAddLocation.setOnClickListener {
            viewModel.currentLocation.value?.let {
                itemLocation = it
                binding.tvLocationStatus.text = "Location Added!"
                Toast.makeText(context, "Current location attached", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
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
        val selectedCategory = binding.spinnerCategory.selectedItem.toString()

        // בדיקת תקינות שדות
        if (title.isBlank() || desc.isBlank() || priceStr.isBlank() || phone.isBlank()) {
            Toast.makeText(context, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0

        // אם בחרנו תמונה חדשה - צריך להעלות אותה
        if (selectedImageUri != null && !selectedImageUri.toString().startsWith("http")) {

            //Toast.makeText(context, "מעלה תמונה, רק רגע...", Toast.LENGTH_SHORT).show()

            val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
                .child("product_images/${System.currentTimeMillis()}.jpg")

            storageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri: Uri ->

                    // --- כאן אנחנו בתוך "חדר ההצלחה" ---
                    val publicImageUrl = uri.toString()

                    // רק עכשיו, כשיש לנו קישור, יוצרים ושומרים!
                    performSave(isEditMode, title, desc, price, phone, selectedCategory, publicImageUrl)
                }
            }.addOnFailureListener {
                Toast.makeText(context, "העלאת התמונה נכשלה", Toast.LENGTH_SHORT).show()
            }
        } else {
            // אם אין תמונה חדשה (או שאנחנו בעריכה ויש כבר קישור), שומרים עם מה שיש
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

        if (isEditMode) viewModel.update(itemToSave) else viewModel.insert(itemToSave)

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}