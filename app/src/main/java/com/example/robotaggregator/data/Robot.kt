package com.example.robotaggregator.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "robots")
data class Robot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiUrl: String,
    val apiKey: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable