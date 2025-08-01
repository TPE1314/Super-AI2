package com.example.robotaggregator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "robots")
data class Robot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiUrl: String,
    val apiKey: String,
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long = 0
)

data class RobotConfig(
    val name: String,
    val apiUrl: String,
    val apiKey: String,
    val description: String = ""
)

data class ChatMessage(
    val id: Long = 0,
    val robotId: Long,
    val message: String,
    val response: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true
)