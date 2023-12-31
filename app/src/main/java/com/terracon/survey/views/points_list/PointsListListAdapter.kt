package com.terracon.survey.views.points_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terracon.survey.databinding.ListItemPointBinding
import com.terracon.survey.databinding.ListItemProjectBinding
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Project
import com.terracon.survey.views.home.HomeViewModel
import com.terracon.survey.views.points_list.PointsListViewModel


class PointsListAdapter internal constructor(private val onClickListener: OnClickListener,private val viewModel: PointsListViewModel) :
    ListAdapter<(BioPoint), PointsListAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(private val binding: ListItemPointBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: PointsListViewModel, item: BioPoint, onClickListener: OnClickListener) {
            binding.pointCodeTxt.text = item.code
            binding.dateTxt.text = item.date
            binding.timeTxt.text = item.time
            binding.habitatTxt.text = item.habitat
            if(item.isSynced){
                binding.syncBtn.visibility = View.GONE
                binding.editBtn.visibility = View.GONE
            }

            // AppUtils.imageWithGLide(binding.userProfileImage,item.projectUrl)
            binding.projectCard.setOnClickListener {
                onClickListener.onClick(item,"open")
            }

            binding.syncBtn.setOnClickListener {
                onClickListener.onClick(item,"syncData")
            }
            binding.editBtn.setOnClickListener {
                onClickListener.onClick(item,"edit")
            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), onClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemPointBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
    class OnClickListener(val clickListener: (projects: BioPoint,action:String) -> Unit) {
        fun onClick(projects: BioPoint,action:String) = clickListener(projects,action)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<BioPoint>() {
    override fun areItemsTheSame(oldItem: BioPoint, itemWithUserInfo: BioPoint): Boolean {
        return oldItem == itemWithUserInfo
    }

    override fun areContentsTheSame(oldItem: BioPoint, itemWithUserInfo: BioPoint): Boolean {
        return (oldItem.id == itemWithUserInfo.id )
    }
}
