package com.example.robotaggregator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.robotaggregator.data.Robot
import com.example.robotaggregator.databinding.ItemRobotBinding

class RobotAdapter(
    private val onEditClick: (Robot) -> Unit,
    private val onDeleteClick: (Robot) -> Unit,
    private val onTestClick: (Robot) -> Unit,
    private val onStatusChange: (Robot, Boolean) -> Unit
) : ListAdapter<Robot, RobotAdapter.RobotViewHolder>(RobotDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RobotViewHolder {
        val binding = ItemRobotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RobotViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: RobotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class RobotViewHolder(
        private val binding: ItemRobotBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(robot: Robot) {
            binding.apply {
                tvRobotName.text = robot.name
                tvApiUrl.text = robot.apiUrl
                switchActive.isChecked = robot.isActive
                
                switchActive.setOnCheckedChangeListener { _, isChecked ->
                    onStatusChange(robot, isChecked)
                }
                
                btnTest.setOnClickListener {
                    onTestClick(robot)
                }
                
                btnEdit.setOnClickListener {
                    onEditClick(robot)
                }
                
                btnDelete.setOnClickListener {
                    onDeleteClick(robot)
                }
            }
        }
    }
    
    private class RobotDiffCallback : DiffUtil.ItemCallback<Robot>() {
        override fun areItemsTheSame(oldItem: Robot, newItem: Robot): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Robot, newItem: Robot): Boolean {
            return oldItem == newItem
        }
    }
}