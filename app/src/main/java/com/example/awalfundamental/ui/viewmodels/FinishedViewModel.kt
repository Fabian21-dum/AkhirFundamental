package com.example.awalfundamental.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awalfundamental.data.FavsRepository
import com.example.awalfundamental.data.favs.Favorite
import com.example.awalfundamental.data.response.EventResponse
import com.example.awalfundamental.data.response.ListEventsItem
import com.example.awalfundamental.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class FinishedViewModel(private val favsRepository: FavsRepository) : ViewModel() {
    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchFinishedEvents()
    }

    fun getFinishedEvents() {
        _isLoading.value = true
        favsRepository.getFinishedEvents().observeForever { events ->
            _finishedEvents.value = events.map { eventEntityToListEventsItem(it) }
            _isLoading.value = false
        }
    }

    fun deleteEvent(event: ListEventsItem) {
        val eventEntity = eventEntityFromListEventsItem(event)
        viewModelScope.launch {
            favsRepository.setBookmarkedEvent(eventEntity, false)
        }
    }

    fun saveEvent(event: ListEventsItem) {
        val eventEntity = eventEntityFromListEventsItem(event)
        viewModelScope.launch {
            favsRepository.setBookmarkedEvent(eventEntity, true)
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

    fun fetchFinishedEvents(limit: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<EventResponse> = ApiConfig.getApiService().getEvents(
                    active = 0,
                    limit = limit
                )
                if (response.isSuccessful && response.body() != null) {
                    _finishedEvents.value = response.body()?.listEvents ?: listOf()
                } else {
                    Log.e("FinishedViewModel", "Failed to fetch finished events: ${response.message()}")
                    _finishedEvents.value = listOf()
                }
            } catch (e: Exception) {
                Log.e("FinishedViewModel", "Exception during fetch: ${e.message}")
                _finishedEvents.value = listOf()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun eventEntityToListEventsItem(eventEntity: Favorite): ListEventsItem {
        return ListEventsItem(
            id = eventEntity.id,
            name = eventEntity.name,
            description = eventEntity.description,
            imageLogo = eventEntity.imageLogo,
            cityName = eventEntity.cityName,
            endTime = eventEntity.endTime,
            beginTime = eventEntity.beginTime,
            category = eventEntity.category,
            imgUrl = eventEntity.imgUrl,
            link = eventEntity.link,
            mediaCover = eventEntity.mediaCover,
            ownerName = eventEntity.ownerName,
            quota = eventEntity.quota,
            registrants = eventEntity.registrants,
            summary = eventEntity.summary,
            active = eventEntity.active,
            isBookmarked = eventEntity.isBookmarked
        )
    }
}