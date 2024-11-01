package com.example.awalfundamental.data.favs

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavsDao {
    @Query("UPDATE events SET isFavorite = 1 WHERE id = :eventId")
    suspend fun addToFavorites(eventId: Int)

    @Query("UPDATE events SET isFavorite = 0 WHERE id = :eventId")
    suspend fun removeFromFavorites(eventId: Int)

    @Update
    fun update(event: Favorite)

    @Query("SELECT * FROM events WHERE id = :id")
    fun getFavoriteEventById(id: Int): LiveData<Favorite?>

    @Query("SELECT * FROM events")
    fun getAllFavorites(): LiveData<List<Favorite>>

    @Query("SELECT * FROM events ORDER BY beginTime DESC")
    fun getAllEvents(): LiveData<List<Favorite>>

    @Query("SELECT * FROM events WHERE isBookmarked = 1")
    fun getBookmarkedEvents(): LiveData<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvents(events: List<Favorite>)

    @Update
    suspend fun updateEvent(event: Favorite)

    @Query("DELETE FROM events WHERE isBookmarked = 0")
    suspend fun deleteAllNonBookmarked()

    @Query("SELECT EXISTS(SELECT * FROM events WHERE name = :name AND isBookmarked = 1)")
    fun isEventBookmarked(name: String): Boolean

    @Query("SELECT * FROM events WHERE active = :status")
    fun getEventsByStatus(status: Boolean): LiveData<List<Favorite>>
//
//    @Query("SELECT * FROM events WHERE id = :id")
//    fun getFavoriteEventById(id: Int): LiveData<Favorite?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Favorite)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteById(eventId: Int)

    @Query("SELECT * FROM events WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<Favorite>>

    @Query("UPDATE events SET isBookmarked = :bookmarked WHERE id = :eventId")
    suspend fun updateBookmarkStatus(eventId: Int, bookmarked: Boolean)


    companion object {
        fun getInstance(): FavsDao {
            throw NotImplementedError("Implement using Room database instance")
        }
    }
}