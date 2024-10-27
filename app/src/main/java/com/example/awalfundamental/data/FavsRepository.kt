package com.example.awalfundamental.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.awalfundamental.data.favs.AppExecutors
import com.example.awalfundamental.data.favs.Favorite
import com.example.awalfundamental.data.favs.FavsDao
import com.example.awalfundamental.data.favs.Result
import com.example.awalfundamental.data.response.EventResponse
import com.example.awalfundamental.data.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.awalfundamental.BuildConfig



class FavsRepository private constructor(
    private val apiService: ApiService,
    private val favsDao: FavsDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<Favorite>>>()

    fun getHeadlineEvent(): LiveData<Result<List<Favorite>>> {
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getEvent(BuildConfig.API_KEY)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    val eventEntities = events.map { event ->
                        Favorite(
                            id = event.id,
                            name = event.name,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            link = event.link,
                            mediaCover = event.mediaCover,
                            summary = event.summary,
                            category = event.category,
                            imgUrl = event.imgUrl,
                            active = event.active,
                            isBookmarked = favsDao.isEventBookmarked(event.name)
                        )
                    }
                    favsDao.insertEvents(eventEntities)
                    favsDao.deleteAllNonBookmarked()
                } else {
                    withContext(Dispatchers.Main) {
                        result.value = Result.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    result.value = Result.Error(e.message ?: "An error occurred")
                }
            }
        }

        val localData = favsDao.getAllEvents()
        result.addSource(localData) { newData: List<Favorite> ->
            result.value = Result.Success(newData)
        }

        return result
    }

    fun getBookmarkedEvents(): LiveData<List<Favorite>> {
        return favsDao.getBookmarkedEvents()
    }

    fun setBookmarkedEvent(event: Favorite, bookmarkState: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            event.isBookmarked = bookmarkState
            favsDao.updateEvent(event)
        }
    }


    fun getFinishedEvents(): LiveData<List<Favorite>> {
        return favsDao.getEventsByStatus(false)
    }

    fun getFavoriteEvents(): LiveData<List<Favorite>> {
        return favsDao.getFavoriteEvents()
    }

    fun updateBookmarkStatus(eventId: Int, isBookmarked: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            favsDao.updateBookmarkStatus(eventId, isBookmarked)
        }
    }
    fun getFavorites(): LiveData<List<Favorite>> {
        return favsDao.getAllFavorites()
    }

    suspend fun addFavorite (event:Favorite){
        favsDao.addFavorite(event)
    }

    suspend fun removeFavorite (event:Favorite){
        favsDao.removeFavorite(event)
    }

    fun getDetailFavorite (id: Int) : LiveData<Favorite?>{
        return favsDao.getFavoriteEventById(id)
    }

    fun getEvents(): LiveData<Result<List<Favorite>>> {
        result.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getEvent(BuildConfig.API_KEY)
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    val eventEntities = events.map { event ->
                        Favorite(
                            id = event.id,
                            name = event.name,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            link = event.link,
                            mediaCover = event.mediaCover,
                            summary = event.summary,
                            category = event.category,
                            imgUrl = event.imgUrl,
                            active = event.active,
                            isBookmarked = favsDao.isEventBookmarked(event.name)
                        )
                    }
                    favsDao.insertEvents(eventEntities)
                    favsDao.deleteAllNonBookmarked()
                } else {
                    withContext(Dispatchers.Main) {
                        result.value = Result.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    result.value = Result.Error(e.message ?: "An error occurred")
                }
            }
        }

        val localData = favsDao.getAllEvents()
        result.addSource(localData) { newData: List<Favorite> ->
            result.value = Result.Success(newData)
        }

        return result
    }

    suspend fun getUpcomingEvent(limit: Int = 1): EventResponse? {
        return try {
            val response = apiService.getEvents(active = -1, limit = limit)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        @Volatile
        private var instance: FavsRepository? = null
        fun getInstance(
            apiService: ApiService,
            favsDao: FavsDao,
            appExecutors: AppExecutors
        ): FavsRepository =
            instance ?: synchronized(this) {
                instance ?: FavsRepository(apiService, favsDao, appExecutors)
            }.also { instance = it }
    }
}