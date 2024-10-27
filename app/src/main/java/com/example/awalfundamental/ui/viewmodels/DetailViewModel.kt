package com.example.awalfundamental.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awalfundamental.data.favs.Favorite
import com.example.awalfundamental.data.favs.FavsDao
import com.example.awalfundamental.data.response.DetailEventResponse
import com.example.awalfundamental.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class DetailViewModel(private val favsDao: FavsDao): ViewModel() {
    private val _eventDetail = MutableLiveData<DetailEventResponse>()
    val eventDetail: LiveData<DetailEventResponse> get() = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchEventDetail(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<DetailEventResponse> = ApiConfig.getApiService().getDetail(eventId)
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()
                } else {
                    Log.e("DetailViewModel", "Error fetching event detail: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addEventToFavorite(event: Favorite) {
        viewModelScope.launch {
            try {
                favsDao.insert(event)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error adding event to favorites: ${e.message}")
            }
        }
    }

    fun removeEventFromFavorite(eventId: Int) {
        viewModelScope.launch {
            try {
                favsDao.deleteById(eventId)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error removing event from favorites: ${e.message}")
            }
        }
    }

    fun getFavoriteEventById(eventId: Int): LiveData<Favorite?> {
        return favsDao.getFavoriteEventById(eventId)
    }
}