package com.terracon.survey.views.add_point_form_bio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terracon.survey.R
import com.terracon.survey.databinding.ListItemPointSpeciesBioBinding
import com.terracon.survey.model.Species
import kotlinx.coroutines.NonDisposableHandle.parent


class AddPointFormBioAdapter internal constructor(
    private val onClickListener: OnClickListener,
    private val viewModel: AddPointFormBioViewModel,
    private val context: Context
) :
    ListAdapter<(Species), AddPointFormBioAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(private val binding: ListItemPointSpeciesBioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: AddPointFormBioViewModel,
            item: Species,
            onClickListener: OnClickListener,
            context: Context,
            position: Int
        ) {
            //binding.projectNameText.text = item.projectName
            // binding.projectDescText.text = item.clientName
            // AppUtils.imageWithGLide(binding.userProfileImage,item.projectUrl)
            binding.speciesNameTxt.text = item.name
            binding.countTxt.text = item.count
            binding.indexTxt.text = (position+1).toString()
            binding.commentTxt.text = item.comment

            if(item.images.isNullOrBlank()){
                binding.imageView.visibility = View.GONE
            }else{
                binding.imageView.visibility = View.VISIBLE
            }

            binding.editBtn.setOnClickListener {
                onClickListener.onClick(item,"edit")
            }
            binding.deleteBtn.setOnClickListener {
                onClickListener.onClick(item,"delete")
            }



//            binding.seasonNameAutoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
//                val value: String = adapterView.getItemAtPosition(i).toString()
//                item.name = value
//                binding.seasonNameEditText.editText?.clearFocus()
//                binding.seasonNameAutoCompleteTextView.clearFocus()
//                onClickListener.onClick(item)
//
//            }


//                binding.seasonNameAutoCompleteTextView.setText(item.name)
//                binding.seasonNameAutoCompleteTextView.setAdapter(
//                    ArrayAdapter(
//                        context, R.layout.dropdown_item, context.resources.getStringArray(
//                            R.array.season_names
//                        )
//                    )
//                )


//            binding.incrementCount.setOnClickListener {
//                item.count = ((Integer.parseInt(item.count)+1).toString())
//                onClickListener.onClick(item)
//            }
//            binding.decrementCount.setOnClickListener {
//                if(Integer.parseInt(item.count)>0){
//                    item.count = (Integer.parseInt(item.count)-1).toString()
//                    onClickListener.onClick(item)
//                }
//
//            }

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

    class OnClickListener(val clickListener: (projects: Species, action : String) -> Unit) {
        fun onClick(projects: Species, action : String) = clickListener(projects,action)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<Species>() {
    override fun areItemsTheSame(oldItem: Species, itemWithUserInfo: Species): Boolean {
        return oldItem == itemWithUserInfo
    }

    override fun areContentsTheSame(oldItem: Species, itemWithUserInfo: Species): Boolean {
        return (oldItem.id == itemWithUserInfo.id)
    }
}
