package com.example.awalfundamental.ui.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awalfundamental.data.FavsRepository
import com.example.awalfundamental.data.favs.Favorite
import com.example.awalfundamental.data.response.EventResponse
import com.example.awalfundamental.data.response.ListEventsItem
import com.example.awalfundamental.data.retrofit.ApiConfig
import com.example.awalfundamental.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(private val eventRepository: FavsRepository) : ViewModel() {


    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchUpcomingEvents()
    }

    fun fetchUpcomingEvents(limit: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<EventResponse> = ApiConfig.getApiService().getEvents(
                    active = 1,
                    limit = limit
                )
                if (response.isSuccessful && response.body() != null) {
                    _upcomingEvents.value = response.body()?.listEvents ?: listOf()
                } else {
                    Log.e(
                        "UpcomingViewModel",
                        "Failed to fetch upcoming events: ${response.message()}"
                    )
                    _upcomingEvents.value = listOf()
                }
            } catch (e: Exception) {
                Log.e("UpcomingViewModel", "Exception during fetch: ${e.message}")
                _upcomingEvents.value = listOf()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(event: ListEventsItem) {
        val eventEntity = eventEntityFromListEventsItem(event)
        viewModelScope.launch {
            eventRepository.setBookmarkedEvent(eventEntity, false)
        }
    }

    fun saveEvent(event: ListEventsItem) {
        val eventEntity = eventEntityFromListEventsItem(event)
        viewModelScope.launch {
            eventRepository.setBookmarkedEvent(eventEntity, true)
        }
    }

    private fun eventEntityFromListEventsItem(item: ListEventsItem): Favorite {
        return Favorite(
            id = item.id,
            name = item.name,
            description = item.description,
            ownerName = item.ownerName,
            cityName = item.cityName,
            quota = item.quota,
            registrants = item.registrants,
            imageLogo = item.imageLogo,
            beginTime = item.beginTime,
            endTime = item.endTime,
            link = item.link,
            mediaCover = item.mediaCover,
            summary = item.summary,
            category = item.category,
            imgUrl = item.imgUrl,
            active = true,
            isBookmarked = item.isBookmarked
        )
    }
}
