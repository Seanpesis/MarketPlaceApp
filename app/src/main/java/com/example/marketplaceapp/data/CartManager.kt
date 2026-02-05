package com.example.marketplaceapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CartManager {

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<List<CartItem>> = _cartItems.map { it.toList() }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(FakeStoreApiService::class.java)

    fun fetchCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiCarts = apiService.getCarts()
                val newCartItems = apiCarts.flatMap { apiCart ->
                    apiCart.products.map { apiProduct ->
                        val marketItem = MarketItem(
                            id = apiProduct.id.toString(),
                            title = apiProduct.title,
                            price = apiProduct.price,
                            description = apiProduct.description,
                            category = apiProduct.category,
                            imageUri = apiProduct.image
                        )
                        CartItem(item = marketItem, quantity = 1)
                    }
                }
                _cartItems.postValue(newCartItems.toMutableList())
            } catch (e: Exception) {
                Log.e("CartManager", "Error fetching cart items", e)
            }
        }
    }


    fun addToCart(marketItem: MarketItem) {
        val currentItems = _cartItems.value ?: mutableListOf()
        val existingItem = currentItems.find { it.item.id == marketItem.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentItems.add(CartItem(marketItem, 1))
        }
        _cartItems.value = currentItems
    }

    val totalItemsCount: LiveData<Int> = _cartItems.map { list ->
        list.sumOf { it.quantity }
    }

    fun removeFromCart(cartItem: CartItem) {
        val currentItems = _cartItems.value ?: mutableListOf()
        currentItems.remove(cartItem)
        _cartItems.value = currentItems
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }
}