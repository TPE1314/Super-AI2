package com.example.robotaggregator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.robotaggregator.data.AppDatabase
import com.example.robotaggregator.databinding.FragmentChatBinding
import com.example.robotaggregator.repository.RobotRepository
import com.example.robotaggregator.viewmodel.RobotViewModel
import com.example.robotaggregator.viewmodel.RobotViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ChatFragment : Fragment() {
    
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: RobotViewModel
    private var selectedRobot: com.example.robotaggregator.data.Robot? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = RobotRepository(database.robotDao())
        val factory = RobotViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RobotViewModel::class.java]
    }
    
    private fun setupObservers() {
        viewModel.robots.observe(viewLifecycleOwner) { robots ->
            val activeRobots = robots.filter { it.isActive }
            updateRobotSpinner(activeRobots)
        }
        
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }
    
    private fun updateRobotSpinner(robots: List<com.example.robotaggregator.data.Robot>) {
        val robotNames = robots.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, robotNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRobot.adapter = adapter
        
        if (robots.isNotEmpty()) {
            selectedRobot = robots.first()
        }
        
        binding.spinnerRobot.setOnItemSelectedListener { _, _, position, _ ->
            selectedRobot = robots.getOrNull(position)
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty() && selectedRobot != null) {
                viewModel.sendMessage(selectedRobot!!, message)
                binding.etMessage.text.clear()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}