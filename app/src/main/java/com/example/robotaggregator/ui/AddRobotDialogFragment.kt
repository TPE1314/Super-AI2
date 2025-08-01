package com.example.robotaggregator.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.robotaggregator.R
import com.example.robotaggregator.data.Robot
import com.example.robotaggregator.databinding.DialogAddRobotBinding

class AddRobotDialogFragment(
    private val robot: Robot? = null,
    private val onSave: (name: String, apiUrl: String, apiKey: String) -> Unit
) : DialogFragment() {
    
    private var _binding: DialogAddRobotBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddRobotBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 如果是编辑模式，填充现有数据
        robot?.let { existingRobot ->
            binding.etRobotName.setText(existingRobot.name)
            binding.etApiUrl.setText(existingRobot.apiUrl)
            binding.etApiKey.setText(existingRobot.apiKey)
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        binding.btnSave.setOnClickListener {
            val name = binding.etRobotName.text.toString().trim()
            val apiUrl = binding.etApiUrl.text.toString().trim()
            val apiKey = binding.etApiKey.text.toString().trim()
            
            if (name.isEmpty() || apiUrl.isEmpty() || apiKey.isEmpty()) {
                // 显示错误提示
                return@setOnClickListener
            }
            
            onSave(name, apiUrl, apiKey)
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}