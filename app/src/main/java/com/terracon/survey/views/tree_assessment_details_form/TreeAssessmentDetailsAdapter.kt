package com.terracon.survey.views.tree_assessment_details_form

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terracon.survey.databinding.ListItemPointSpeciesBioBinding
import com.terracon.survey.databinding.ListItemTreeSpeciesBinding
import com.terracon.survey.model.Species
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioAdapter
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioViewModel

class TreeAssessmentDetailsAdapter internal constructor(
    private val onClickListener: OnClickListener,
    private val viewModel: TreeAssessmentDetailsFormViewModel,
    private val context: Context
) :
    ListAdapter<(TreeAssessmentSpecies), TreeAssessmentDetailsAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(private val binding: ListItemTreeSpeciesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: TreeAssessmentDetailsFormViewModel,
            item: TreeAssessmentSpecies,
            onClickListener: OnClickListener,
            context: Context,
            position: Int
        ) {
            //binding.projectNameText.text = item.projectName
            // binding.projectDescText.text = item.clientName
            // AppUtils.imageWithGLide(binding.userProfileImage,item.projectUrl)
            binding.speciesNameTxt.text = item.name
            binding.girthTxt.text = item.girth.toString()
            binding.heightTxt.text = item.height.toString()
            binding.diameterTxt.text = item.canopy_diameter.toString()
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
        val binding = ListItemTreeSpeciesBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    class OnClickListener(val clickListener: (projects: TreeAssessmentSpecies,action:String) -> Unit) {
        fun onClick(projects: TreeAssessmentSpecies, action:String) = clickListener(projects,action)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<TreeAssessmentSpecies>() {
    override fun areItemsTheSame(oldItem: TreeAssessmentSpecies, itemWithUserInfo: TreeAssessmentSpecies): Boolean {
        return oldItem == itemWithUserInfo
    }

    override fun areContentsTheSame(oldItem: TreeAssessmentSpecies, itemWithUserInfo: TreeAssessmentSpecies): Boolean {
        return (oldItem.id == itemWithUserInfo.id)
    }
}
