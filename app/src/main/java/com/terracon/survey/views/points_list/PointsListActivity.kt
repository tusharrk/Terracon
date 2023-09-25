package com.terracon.survey.views.points_list


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.terracon.survey.R
import com.terracon.survey.databinding.HomeActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.utils.GlobalData
import com.terracon.survey.views.home.HomeViewModel
import com.terracon.survey.views.home.ProjectsListAdapter
import com.terracon.survey.views.project_details.ProjectDetailsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class PointsListActivity : AppCompatActivity() {

    private val pointsListViewModel by viewModel<PointsListViewModel>()

    private lateinit var binding: HomeActivityBinding
    private lateinit var listAdapter: PointsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupListAdapter()
        setupObservers()
        setupUi()
    }

    private fun setupListAdapter() {
        listAdapter = PointsListAdapter(PointsListAdapter.OnClickListener { project ->
            val intent = Intent(this, ProjectDetailsActivity::class.java)
            intent.putExtra("projectData", project as Serializable)
            this.startActivity(intent)
//            try {
//                val index = homeViewModel.projectsList.value?.indexOf(project)
//
//            } catch (e: Exception) {
//                Log.d("TAG_X", "error update read--$e")
//            }
        }, pointsListViewModel)
        binding.projectsRecyclerView.adapter = listAdapter
        binding.projectsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

    }

    private fun setupObservers() {
        pointsListViewModel.pointsList.observe(this) { data ->
            listAdapter.submitList(data)
        }

        pointsListViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.projectsRecyclerView.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.projectsRecyclerView.visibility = View.VISIBLE
            }
        }

        pointsListViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorView.root.visibility = View.VISIBLE
                binding.projectsRecyclerView.visibility = View.INVISIBLE
                ErrorUtils.setErrorView(
                    errorMessage = errorMessage,
                    errorBinding = binding.errorView,
                    context = this,
                    pageName = "projects",
                    onRetry = {
                        pointsListViewModel.fetchPoints()
                    })
                // showError(errorMessage.toString())
            }else{
                binding.errorView.root.visibility = View.GONE
                binding.projectsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUi() {
        pointsListViewModel.project = intent.getSerializableExtra("projectData") as Project


        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if(supportActionBar != null){
           // supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = "Welcome, ${GlobalData.userData.name}"
        }

        binding.pullToRefresh.setOnRefreshListener {
            pointsListViewModel.fetchPoints()
            binding.pullToRefresh.isRefreshing = false
        }

        pointsListViewModel.fetchPoints()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.logoutBtn) {
            AppUtils.logoutUser(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun logToast(msg: String) {
        Log.d("TAG_WW", msg)
    }

    override fun onRestart() {
       // homeViewModel.fetchUserss(ChatApp.userProfileDTO.sapId)
        super.onRestart()
    }

    override fun onStop() {
        //stompClient.unsubscribeAll()
        super.onStop()
    }

}
