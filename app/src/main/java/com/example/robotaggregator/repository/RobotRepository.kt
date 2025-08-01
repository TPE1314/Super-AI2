package com.example.robotaggregator.repository

import com.example.robotaggregator.api.ApiClient
import com.example.robotaggregator.api.MessageRequest
import com.example.robotaggregator.data.Robot
import com.example.robotaggregator.data.RobotDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URL

class RobotRepository(private val robotDao: RobotDao) {
    
    fun getAllRobots(): Flow<List<Robot>> = robotDao.getAllRobots()
    
    fun getActiveRobots(): Flow<List<Robot>> = robotDao.getActiveRobots()
    
    suspend fun getRobotById(id: Long): Robot? = robotDao.getRobotById(id)
    
    suspend fun insertRobot(robot: Robot): Long = robotDao.insertRobot(robot)
    
    suspend fun updateRobot(robot: Robot) = robotDao.updateRobot(robot)
    
    suspend fun deleteRobot(robot: Robot) = robotDao.deleteRobot(robot)
    
    suspend fun updateRobotStatus(id: Long, isActive: Boolean) = 
        robotDao.updateRobotStatus(id, isActive)
    
    suspend fun testConnection(robot: Robot): Result<Boolean> = flow {
        try {
            // 验证URL格式
            if (!isValidUrl(robot.apiUrl)) {
                emit(Result.failure(Exception("Invalid URL format")))
                return@flow
            }
            
            val apiService = ApiClient.createApiService(robot.apiUrl)
            val response = apiService.checkStatus("Bearer ${robot.apiKey}")
            
            if (response.isSuccessful) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception("Connection failed: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.first()
    
    suspend fun sendMessage(robot: Robot, message: String): Result<String> = flow {
        try {
            if (!isValidUrl(robot.apiUrl)) {
                emit(Result.failure(Exception("Invalid URL format")))
                return@flow
            }
            
            val apiService = ApiClient.createApiService(robot.apiUrl)
            val request = MessageRequest(message, robot.apiKey)
            val response = apiService.sendMessage(request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody?.success == true) {
                    emit(Result.success(responseBody.message ?: "Message sent successfully"))
                } else {
                    emit(Result.failure(Exception(responseBody?.error ?: "Unknown error")))
                }
            } else {
                emit(Result.failure(Exception("Request failed: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.first()
    
    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }
}