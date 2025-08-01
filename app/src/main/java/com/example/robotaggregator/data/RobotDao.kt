package com.example.robotaggregator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RobotDao {
    @Query("SELECT * FROM robots ORDER BY name ASC")
    fun getAllRobots(): Flow<List<Robot>>
    
    @Query("SELECT * FROM robots WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveRobots(): Flow<List<Robot>>
    
    @Query("SELECT * FROM robots WHERE id = :robotId")
    suspend fun getRobotById(robotId: Long): Robot?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRobot(robot: Robot): Long
    
    @Update
    suspend fun updateRobot(robot: Robot)
    
    @Delete
    suspend fun deleteRobot(robot: Robot)
    
    @Query("UPDATE robots SET lastUsed = :timestamp WHERE id = :robotId")
    suspend fun updateLastUsed(robotId: Long, timestamp: Long)
    
    @Query("UPDATE robots SET isActive = :isActive WHERE id = :robotId")
    suspend fun updateRobotStatus(robotId: Long, isActive: Boolean)
}