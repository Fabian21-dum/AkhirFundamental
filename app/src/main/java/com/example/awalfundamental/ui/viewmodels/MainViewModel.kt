package com.example.awalfundamental.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awalfundamental.data.response.EventResponse
import com.example.awalfundamental.data.response.ListEventsItem
import com.example.awalfundamental.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _listEvents = MutableLiveData<List<ListEventsItem>>()
    val listEvents: LiveData<List<ListEventsItem>> = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "MainViewModel"
        private const val LIMIT = 10
    }

    init {
        findEvents()
    }

    private fun findEvents(active: Int = 0) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getEvents(active, LIMIT)
                if (response.isSuccessful) {
                    val eventResponse: EventResponse? = response.body()
                    _listEvents.value = eventResponse?.listEvents ?: emptyList()
                } else {
                    handleError(response.message())
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleError(message: String) {
        _errorMessage.value = message
        Log.e(TAG, "Error: $message")
    }
}
