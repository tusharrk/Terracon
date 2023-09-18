package com.terracon.survey.views.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terracon.survey.databinding.ListItemProjectBinding
import com.terracon.survey.model.Project


class ProjectsListAdapter internal constructor(private val onClickListener: OnClickListener,private val viewModel: HomeViewModel) :
    ListAdapter<(Project), ProjectsListAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(private val binding: ListItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: HomeViewModel, item: Project, onClickListener: OnClickListener) {
            binding.projectNameText.text = item.projectName
            binding.projectDescText.text = item.clientName
           // AppUtils.imageWithGLide(binding.userProfileImage,item.projectUrl)
            binding.projectCard.setOnClickListener {
                onClickListener.onClick(item)
            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), onClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemProjectBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
    class OnClickListener(val clickListener: (projects: Project) -> Unit) {
        fun onClick(projects: Project) = clickListener(projects)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<Project>() {
    override fun areItemsTheSame(oldItem: Project, itemWithUserInfo: Project): Boolean {
        return oldItem == itemWithUserInfo
    }

    override fun areContentsTheSame(oldItem: Project, itemWithUserInfo: Project): Boolean {
        return (oldItem.id == itemWithUserInfo.id )
    }
}
