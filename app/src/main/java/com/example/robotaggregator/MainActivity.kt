package com.example.robotaggregator

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<TextView>(R.id.textView).text = "机器人聚合器应用\n\n功能特性：\n• 机器人API管理\n• 消息发送\n• 状态监控\n\n版本：1.0"
    }
}