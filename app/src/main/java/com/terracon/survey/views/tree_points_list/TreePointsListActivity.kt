package com.terracon.survey.views.tree_points_list


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
import com.terracon.survey.databinding.PointsListActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity
import com.terracon.survey.views.points_list.PointsListAdapter
import com.terracon.survey.views.points_list.PointsListViewModel
import com.terracon.survey.views.tree_assessment_details_form.TreeAssessmentDetailsFormActivity
import com.terracon.survey.views.tree_assessment_form.TreeAssessmentFormActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class TreePointsListActivity : AppCompatActivity() {

    private val treePointsListViewModel by viewModel<TreePointsListViewModel>()

    private lateinit var binding: PointsListActivityBinding
    private lateinit var listAdapter: TreePointsListAdapter
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
        listAdapter = TreePointsListAdapter(TreePointsListAdapter.OnClickListener { point, action ->
            if (point.isSynced) {
                Toast.makeText(this, "Point is already Synced with Server", Toast.LENGTH_SHORT)
                    .show()
            } else {
            if (action == "open") {
                val intent = Intent(this, TreeAssessmentDetailsFormActivity::class.java)
                intent.putExtra("pointData", point as Serializable)
                intent.putExtra("projectData", treePointsListViewModel.project as Serializable)
                this.startActivity(intent)
            } else {
                treePointsListViewModel.syncDataFromLocalToServer(this, point)
                return@OnClickListener
            }
        }

        }, treePointsListViewModel)
        binding.pointsRecyclerView.adapter = listAdapter
        binding.pointsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

    }

    private fun setupObservers() {
        treePointsListViewModel.pointsList.observe(this) { data ->
            listAdapter.submitList(data)
        }

        treePointsListViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.pointsRecyclerView.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.pointsRecyclerView.visibility = View.VISIBLE
            }
        }

        treePointsListViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorView.root.visibility = View.VISIBLE
                binding.pointsRecyclerView.visibility = View.INVISIBLE
                ErrorUtils.setErrorView(
                    errorMessage = errorMessage,
                    errorBinding = binding.errorView,
                    context = this,
                    pageName = "projects",
                    onRetry = {
                        treePointsListViewModel.fetchPoints()
                    })
                // showError(errorMessage.toString())
            }else{
                binding.errorView.root.visibility = View.GONE
                binding.pointsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUi() {
        treePointsListViewModel.project = intent.getSerializableExtra("projectData") as Project


        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = treePointsListViewModel.project.name
        }

        binding.pullToRefresh.setOnRefreshListener {
            treePointsListViewModel.fetchPoints()
            binding.pullToRefresh.isRefreshing = false
        }

        binding.newChatFab.setOnClickListener {
            val intent = Intent(this, TreeAssessmentFormActivity::class.java)
            intent.putExtra("projectData", treePointsListViewModel.project as Serializable)
            this.startActivity(intent)

        }

        treePointsListViewModel.fetchPoints()

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
        treePointsListViewModel.fetchPoints()
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
        treePointsListViewModel.fetchPoints()
    }

}
