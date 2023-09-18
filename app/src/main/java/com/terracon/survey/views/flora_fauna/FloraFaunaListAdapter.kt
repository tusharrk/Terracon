package com.terracon.survey.views.flora_fauna

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terracon.survey.databinding.ListItemProjectBinding


class FloraFaunaListAdapter internal constructor(private val onClickListener: OnClickListener,private val viewModel: FloraFaunaViewModel) :
    ListAdapter<(String), FloraFaunaListAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(private val binding: ListItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: FloraFaunaViewModel, item: String, onClickListener: OnClickListener) {
            binding.projectNameText.text = item
            binding.projectDescText.visibility = View.GONE
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
    class OnClickListener(val clickListener: (projects: String) -> Unit) {
        fun onClick(projects: String) = clickListener(projects)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, itemWithUserInfo: String): Boolean {
        return oldItem == itemWithUserInfo
    }

    override fun areContentsTheSame(oldItem: String, itemWithUserInfo: String): Boolean {
        return (oldItem == itemWithUserInfo )
    }
}
