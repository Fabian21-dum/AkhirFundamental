package com.example.awalfundamental.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awalfundamental.data.FavsRepository
import com.example.awalfundamental.data.favs.Favorite
import com.example.awalfundamental.data.response.ListEventsItem
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavsRepository) : ViewModel() {
    private val _favoriteEvents = MutableLiveData<List<Favorite>>()
    val favoriteEvents: LiveData<List<Favorite>> get() = _favoriteEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchFavoriteEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val events = repository.getFavoriteEvents().value ?: emptyList()
                _favoriteEvents.value = events
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load favorite events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFavoriteEvent(eventItem: ListEventsItem) {
        viewModelScope.launch {
            try {
                repository.updateBookmarkStatus(eventItem.id, true)
                fetchFavoriteEvents()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add favorite event: ${e.message}"
            }
        }
    }

    fun deleteFavoriteEvent(eventItem: ListEventsItem) {
        viewModelScope.launch {
            try {
                repository.updateBookmarkStatus(eventItem.id, false)
                fetchFavoriteEvents()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove favorite event: ${e.message}"
            }
        }
    }
}