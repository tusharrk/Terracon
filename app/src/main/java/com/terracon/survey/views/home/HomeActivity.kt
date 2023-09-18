package com.terracon.survey.views.home


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
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.views.login.LoginActivity
import com.terracon.survey.views.project_details.ProjectDetailsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class HomeActivity : AppCompatActivity() {

    private val homeViewModel by viewModel<HomeViewModel>()

    private lateinit var binding: HomeActivityBinding
    private lateinit var listAdapter: ProjectsListAdapter
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
        listAdapter = ProjectsListAdapter(ProjectsListAdapter.OnClickListener { project ->
            val intent = Intent(this, ProjectDetailsActivity::class.java)
            intent.putExtra("projectData", project as Serializable)
            this.startActivity(intent)
//            try {
//                val index = homeViewModel.projectsList.value?.indexOf(project)
//
//            } catch (e: Exception) {
//                Log.d("TAG_X", "error update read--$e")
//            }
        }, homeViewModel)
        binding.projectsRecyclerView.adapter = listAdapter
        binding.projectsRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

    }

    private fun setupObservers() {
        homeViewModel.projectsList.observe(this) { data ->
            listAdapter.submitList(data)
        }

        homeViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.projectsRecyclerView.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.projectsRecyclerView.visibility = View.VISIBLE
            }
        }

        homeViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorView.root.visibility = View.VISIBLE
                binding.projectsRecyclerView.visibility = View.INVISIBLE
                ErrorUtils.setErrorView(
                    errorMessage = errorMessage,
                    errorBinding = binding.errorView,
                    context = this,
                    pageName = "projects",
                    onRetry = {
                        homeViewModel.fetchProjects("asd")
                    })
                // showError(errorMessage.toString())
            }else{
                binding.errorView.root.visibility = View.GONE
                binding.projectsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUi() {

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if(supportActionBar != null){
           // supportActionBar?.setDisplayHomeAsUpEnabled(true)
            //supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = "Welcome, Tushar Kalsara"
        }

        binding.pullToRefresh.setOnRefreshListener {
            homeViewModel.fetchProjects("as")
            binding.pullToRefresh.isRefreshing = false
        }

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
