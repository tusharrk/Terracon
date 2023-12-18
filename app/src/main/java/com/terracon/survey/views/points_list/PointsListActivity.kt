package com.terracon.survey.views.points_list


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.terracon.survey.R
import com.terracon.survey.databinding.HomeActivityBinding
import com.terracon.survey.databinding.PointsListActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.utils.GlobalData
import com.terracon.survey.views.bio_diversity_form_main.BioDiversityFormMainActivity
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity
import com.terracon.survey.views.home.HomeViewModel
import com.terracon.survey.views.home.ProjectsListAdapter
import com.terracon.survey.views.project_details.ProjectDetailsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class PointsListActivity : AppCompatActivity() {

    private val pointsListViewModel by viewModel<PointsListViewModel>()

    private lateinit var binding: PointsListActivityBinding
    private lateinit var listAdapter: PointsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PointsListActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupListAdapter()
        setupObservers()
        setupUi()
    }

    private fun setupListAdapter() {
        listAdapter = PointsListAdapter(PointsListAdapter.OnClickListener { bioPoint, action ->
//            pointsListViewModel.syncDataFromLocalToServer(this,bioPoint)
//            return@OnClickListener
            if (bioPoint.isSynced) {
                Toast.makeText(this, "Point is already Synced with Server", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (action == "open") {
                    val intent = Intent(this, FloraFaunaActivity::class.java)
                    intent.putExtra("pointData", bioPoint as Serializable)
                    intent.putExtra("projectData", pointsListViewModel.project as Serializable)
                    this.startActivity(intent)
                }else if(action == "edit"){
                    val intent = Intent(this, BioDiversityFormMainActivity::class.java)
                    intent.putExtra("projectData", pointsListViewModel.project as Serializable)
                    intent.putExtra("isEdit",true)
                    intent.putExtra("pointData",bioPoint as Serializable)
                    this.startActivity(intent)
                } else {
                    pointsListViewModel.syncDataFromLocalToServer(this, bioPoint)
                    return@OnClickListener
                }
            }

//            try {
//                val index = homeViewModel.projectsList.value?.indexOf(project)
//
//            } catch (e: Exception) {
//                Log.d("TAG_X", "error update read--$e")
//            }
        }, pointsListViewModel)
        binding.pointsRecyclerView.adapter = listAdapter
        binding.pointsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

    }

    private fun setupObservers() {
        pointsListViewModel.pointsList.observe(this) { data ->
            listAdapter.submitList(data)
        }

        pointsListViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.pointsRecyclerView.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.pointsRecyclerView.visibility = View.VISIBLE
            }
        }

        pointsListViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorView.root.visibility = View.VISIBLE
                binding.pointsRecyclerView.visibility = View.INVISIBLE
                ErrorUtils.setErrorView(
                    errorMessage = errorMessage,
                    errorBinding = binding.errorView,
                    context = this,
                    pageName = "projects",
                    onRetry = {
                        pointsListViewModel.fetchPoints()
                    })
                // showError(errorMessage.toString())
            } else {
                binding.errorView.root.visibility = View.GONE
                binding.pointsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUi() {
        pointsListViewModel.project = intent.getSerializableExtra("projectData") as Project


        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = pointsListViewModel.project.name
        }

        binding.pullToRefresh.setOnRefreshListener {
            pointsListViewModel.fetchPoints()
            binding.pullToRefresh.isRefreshing = false
        }

        binding.newChatFab.setOnClickListener {
            val intent = Intent(this, BioDiversityFormMainActivity::class.java)
            intent.putExtra("projectData", pointsListViewModel.project as Serializable)
            intent.putExtra("isEdit",false)
            this.startActivity(intent)

        }

        pointsListViewModel.fetchPoints()

    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
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

    override fun onResume() {
        pointsListViewModel.fetchPoints()
        super.onResume()
    }
    override fun onRestart() {
        // homeViewModel.fetchUserss(ChatApp.userProfileDTO.sapId)
        super.onRestart()
    }

    override fun onStop() {
        //stompClient.unsubscribeAll()
        super.onStop()
    }

    fun refreshList() {
        pointsListViewModel.fetchPoints()
    }

}
