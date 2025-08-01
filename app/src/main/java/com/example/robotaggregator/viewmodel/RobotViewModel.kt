package com.example.robotaggregator.viewmodel

import androidx.lifecycle.*
import com.example.robotaggregator.data.Robot
import com.example.robotaggregator.repository.RobotRepository
import kotlinx.coroutines.launch

class RobotViewModel(private val repository: RobotRepository) : ViewModel() {
    
    private val _robots = MutableLiveData<List<Robot>>()
    val robots: LiveData<List<Robot>> = _robots
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    
    private val _connectionResult = MutableLiveData<Boolean>()
    val connectionResult: LiveData<Boolean> = _connectionResult
    
    init {
        loadRobots()
    }
    
    private fun loadRobots() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllRobots().collect { robotList ->
                    _robots.value = robotList
                }
            } catch (e: Exception) {
                _message.value = "加载机器人列表失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addRobot(name: String, apiUrl: String, apiKey: String) {
        if (name.isBlank() || apiUrl.isBlank() || apiKey.isBlank()) {
            _message.value = "请填写所有字段"
            return
        }
        
        viewModelScope.launch {
            try {
                val robot = Robot(name = name, apiUrl = apiUrl, apiKey = apiKey)
                repository.insertRobot(robot)
                _message.value = "机器人添加成功"
            } catch (e: Exception) {
                _message.value = "添加机器人失败: ${e.message}"
            }
        }
    }
    
    fun updateRobot(robot: Robot) {
        viewModelScope.launch {
            try {
                repository.updateRobot(robot)
                _message.value = "机器人更新成功"
            } catch (e: Exception) {
                _message.value = "更新机器人失败: ${e.message}"
            }
        }
    }
    
    fun deleteRobot(robot: Robot) {
        viewModelScope.launch {
            try {
                repository.deleteRobot(robot)
                _message.value = "机器人删除成功"
            } catch (e: Exception) {
                _message.value = "删除机器人失败: ${e.message}"
            }
        }
    }
    
    fun testConnection(robot: Robot) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.testConnection(robot)
                result.fold(
                    onSuccess = {
                        _connectionResult.value = true
                        _message.value = "连接成功"
                    },
                    onFailure = { exception ->
                        _connectionResult.value = false
                        _message.value = "连接失败: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _connectionResult.value = false
                _message.value = "连接测试失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun sendMessage(robot: Robot, message: String) {
        if (message.isBlank()) {
            _message.value = "请输入消息"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.sendMessage(robot, message)
                result.fold(
                    onSuccess = { response ->
                        _message.value = "消息发送成功: $response"
                    },
                    onFailure = { exception ->
                        _message.value = "消息发送失败: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _message.value = "发送消息失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearMessage() {
        _message.value = ""
    }
    
    fun clearConnectionResult() {
        _connectionResult.value = null
    }
}

class RobotViewModelFactory(private val repository: RobotRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RobotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RobotViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}