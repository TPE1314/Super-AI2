package com.example.robotaggregator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RobotDao {
    @Query("SELECT * FROM robots ORDER BY createdAt DESC")
    fun getAllRobots(): Flow<List<Robot>>
    
    @Query("SELECT * FROM robots WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveRobots(): Flow<List<Robot>>
    
    @Query("SELECT * FROM robots WHERE id = :id")
    suspend fun getRobotById(id: Long): Robot?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRobot(robot: Robot): Long
    
    @Update
    suspend fun updateRobot(robot: Robot)
    
    @Delete
    suspend fun deleteRobot(robot: Robot)
    
    @Query("DELETE FROM robots WHERE id = :id")
    suspend fun deleteRobotById(id: Long)
    
    @Query("UPDATE robots SET isActive = :isActive WHERE id = :id")
    suspend fun updateRobotStatus(id: Long, isActive: Boolean)
}