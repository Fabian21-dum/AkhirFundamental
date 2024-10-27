package com.example.awalfundamental.data

import android.content.Context
import com.example.awalfundamental.data.favs.AppExecutors
import com.example.awalfundamental.data.favs.EventDatabase
import com.example.awalfundamental.data.favs.FavsDao
import com.example.awalfundamental.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): FavsRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.favsDao()
        val appExecutors = AppExecutors()
        return FavsRepository.getInstance(apiService, dao, appExecutors)
    }

    fun provideEventDao(context: Context): FavsDao {
        val database = EventDatabase.getInstance(context)
        return database.favsDao()
    }
}