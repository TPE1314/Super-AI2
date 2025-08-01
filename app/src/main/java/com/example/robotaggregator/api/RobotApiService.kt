package com.example.robotaggregator.api

import retrofit2.Response
import retrofit2.http.*

data class MessageRequest(
    val message: String,
    val apiKey: String
)

data class MessageResponse(
    val success: Boolean,
    val message: String?,
    val error: String?
)

interface RobotApiService {
    @POST("chat")
    suspend fun sendMessage(
        @Body request: MessageRequest
    ): Response<MessageResponse>
    
    @GET("status")
    suspend fun checkStatus(
        @Header("Authorization") apiKey: String
    ): Response<MessageResponse>
}