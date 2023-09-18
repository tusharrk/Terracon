package com.terracon.survey.views.add_point_form_bio

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terracon.survey.R
import com.terracon.survey.databinding.ListItemPointSpeciesBioBinding
import com.terracon.survey.model.SpeciesBio
import kotlinx.coroutines.NonDisposableHandle.parent


class AddPointFormBioAdapter internal constructor(
    private val onClickListener: OnClickListener,
    private val viewModel: AddPointFormBioViewModel,
    private val context: Context
) :
    ListAdapter<(SpeciesBio), AddPointFormBioAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(private val binding: ListItemPointSpeciesBioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: AddPointFormBioViewModel,
            item: SpeciesBio,
            onClickListener: OnClickListener,
            context: Context,
            position: Int
        ) {
            //binding.projectNameText.text = item.projectName
            // binding.projectDescText.text = item.clientName
            // AppUtils.imageWithGLide(binding.userProfileImage,item.projectUrl)
            binding.countEditText.editText?.setText("${item.count}")

            binding.seasonNameAutoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
                val value: String = adapterView.getItemAtPosition(i).toString()
                item.speciesName = value
                binding.seasonNameEditText.editText?.clearFocus()
                binding.seasonNameAutoCompleteTextView.clearFocus()
                onClickListener.onClick(item)

            }


                binding.seasonNameAutoCompleteTextView.setText(item.speciesName)
                binding.seasonNameAutoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        context, R.layout.dropdown_item, context.resources.getStringArray(
                            R.array.season_names
                        )
                    )
                )


            binding.incrementCount.setOnClickListener {
                item.count = item.count+1
                onClickListener.onClick(item)
            }
            binding.decrementCount.setOnClickListener {
                if(item.count>0){
                    item.count = item.count-1
                    onClickListener.onClick(item)
                }

            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), onClickListener, context,position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemPointSpeciesBioBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    class OnClickListener(val clickListener: (projects: SpeciesBio) -> Unit) {
        fun onClick(projects: SpeciesBio) = clickListener(projects)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<SpeciesBio>() {
    override fun areItemsTheSame(oldItem: SpeciesBio, itemWithUserInfo: SpeciesBio): Boolean {
        return oldItem == itemWithUserInfo
    }

    override fun areContentsTheSame(oldItem: SpeciesBio, itemWithUserInfo: SpeciesBio): Boolean {
        return (oldItem.speciesId == itemWithUserInfo.speciesId)
    }
}
