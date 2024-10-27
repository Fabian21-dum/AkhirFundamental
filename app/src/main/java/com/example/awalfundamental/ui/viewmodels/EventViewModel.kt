package com.example.awalfundamental.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awalfundamental.data.FavsRepository
import com.example.awalfundamental.data.favs.Favorite
import kotlinx.coroutines.launch
import java.io.IOException
import com.example.awalfundamental.data.favs.Result

class EventViewModel(private val favsRepository: FavsRepository) : ViewModel() {

    private val _events = MutableLiveData<Result<List<Favorite>>>()
    val events: LiveData<Result<List<Favorite>>> = _events

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getHeadlineEvent() {
        viewModelScope.launch {
            _events.value = Result.Loading
            try {
                favsRepository.getHeadlineEvent().observeForever { result ->
                    when (result) {
                        is Result.Loading -> {
                            // No action needed
                        }

                        is Result.Success -> {
                            _events.value = result
                        }

                        is Result.Error -> {
                            _events.value = Result.Error(result.error)
                        }
                    }
                }
            } catch (e: IOException) {
                _error.value = "No internet connection."
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }

    fun getBookmarkedEvents() = favsRepository.getBookmarkedEvents()

    fun saveEvent(event: Favorite) {
        viewModelScope.launch {
            favsRepository.setBookmarkedEvent(event, true)
        }
    }

    fun deleteEvent(event: Favorite) {
        viewModelScope.launch {
            favsRepository.setBookmarkedEvent(event, false)
        }
    }
}