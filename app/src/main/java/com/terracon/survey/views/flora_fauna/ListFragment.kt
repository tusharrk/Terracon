package com.terracon.survey.views.flora_fauna

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.terracon.survey.R
import com.terracon.survey.databinding.FloraFaunaListFragmentBinding
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioActivity
import com.terracon.survey.views.project_details.ProjectDetailsActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class ListFragment : Fragment() {


    private lateinit var binding: FloraFaunaListFragmentBinding
    private val floraFaunaViewModel by activityViewModel<FloraFaunaViewModel>()
    private lateinit var listAdapter: FloraFaunaListAdapter
private var pageType:String? = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FloraFaunaListFragmentBinding.inflate(inflater, container, false)
        pageType = arguments?.getString("fragmentName")
        setupUI()
        setupListAdapter()
        setupObservers()

        return binding.root
    }

    private fun setupUI(){
        binding.pullToRefresh.setOnRefreshListener {
            floraFaunaViewModel.fetchFloraFaunaCategories("as")
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun setupListAdapter() {
        listAdapter = FloraFaunaListAdapter(FloraFaunaListAdapter.OnClickListener { item ->
            val intent = Intent(context, AddPointFormBioActivity::class.java)
            intent.putExtra("projectData", floraFaunaViewModel.project as Serializable)
            intent.putExtra("pointData", floraFaunaViewModel.pointBio as Serializable)
            intent.putExtra("subType", item)
            intent.putExtra("type", pageType)


            this.startActivity(intent)
        }, floraFaunaViewModel)
        binding.floraOrFaunaRecyclerView.adapter = listAdapter
        binding.floraOrFaunaRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

    }

    private fun setupObservers() {
        floraFaunaViewModel.floraFaunaList.observe(viewLifecycleOwner) { data ->
            if(!pageType.isNullOrBlank()){
                if(pageType=="Flora"){
                    listAdapter.submitList(data?.floraCategoryList)
                }else{
                    listAdapter.submitList(data?.faunaCategoryList)
                }

            }
            //binding.parentLayout.visibility = View.VISIBLE

        }
    }

}