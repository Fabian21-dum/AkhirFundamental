package com.example.awalfundamental.data.favs

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "events")
data class Favorite (
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String?,
    val ownerName: String?,
    val cityName: String?,
    val quota: Int?,
    val registrants: Int?,
    val imageLogo: String?,
    val imgUrl: String?,
    val beginTime: String?,
    val endTime: String?,
    val link: String?,
    val mediaCover: String?,
    val summary: String?,
    val category: String?,
    val active: Boolean,
    var isBookmarked: Boolean,
    var isFavorite: Boolean = false
) : Parcelable