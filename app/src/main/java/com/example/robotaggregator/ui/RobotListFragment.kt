package com.example.robotaggregator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.robotaggregator.R
import com.example.robotaggregator.adapter.RobotAdapter
import com.example.robotaggregator.data.AppDatabase
import com.example.robotaggregator.databinding.FragmentRobotListBinding
import com.example.robotaggregator.repository.RobotRepository
import com.example.robotaggregator.viewmodel.RobotViewModel
import com.example.robotaggregator.viewmodel.RobotViewModelFactory
import com.google.android.material.snackbar.Snackbar

class RobotListFragment : Fragment() {
    
    private var _binding: FragmentRobotListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: RobotViewModel
    private lateinit var adapter: RobotAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRobotListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = RobotRepository(database.robotDao())
        val factory = RobotViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RobotViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = RobotAdapter(
            onEditClick = { robot -> showEditDialog(robot) },
            onDeleteClick = { robot -> showDeleteDialog(robot) },
            onTestClick = { robot -> viewModel.testConnection(robot) },
            onStatusChange = { robot, isActive -> 
                viewModel.updateRobotStatus(robot.id, isActive)
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@RobotListFragment.adapter
        }
    }
    
    private fun setupObservers() {
        viewModel.robots.observe(viewLifecycleOwner) { robots ->
            adapter.submitList(robots)
            binding.emptyView.visibility = if (robots.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 可以在这里显示加载指示器
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddRobot.setOnClickListener {
            showAddDialog()
        }
    }
    
    private fun showAddDialog() {
        val dialog = AddRobotDialogFragment { name, apiUrl, apiKey ->
            viewModel.addRobot(name, apiUrl, apiKey)
        }
        dialog.show(childFragmentManager, "AddRobotDialog")
    }
    
    private fun showEditDialog(robot: com.example.robotaggregator.data.Robot) {
        val dialog = AddRobotDialogFragment(robot) { name, apiUrl, apiKey ->
            val updatedRobot = robot.copy(name = name, apiUrl = apiUrl, apiKey = apiKey)
            viewModel.updateRobot(updatedRobot)
        }
        dialog.show(childFragmentManager, "EditRobotDialog")
    }
    
    private fun showDeleteDialog(robot: com.example.robotaggregator.data.Robot) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.delete_robot_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteRobot(robot)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}