package com.zybooks.basket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zybooks.basket.data.BasketRepository

class CommunityViewModelFactory(
    private val basketRepository: BasketRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityViewModel(basketRepository = basketRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}