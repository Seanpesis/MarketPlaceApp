package com.example.marketplaceapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map

object CartManager {


    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

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

    fun getTotalPrice(): Double {
        return _cartItems.value?.sumOf { it.item.price * it.quantity } ?: 0.0
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }
}
