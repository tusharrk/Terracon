package com.terracon.survey.views.flora_fauna

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.tabs.TabLayoutMediator
import com.terracon.survey.R
import com.terracon.survey.databinding.FloraFaunaActivityBinding
import com.terracon.survey.databinding.LoginActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.AppUtils.spannableStringWithColor
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.views.home.ProjectsListAdapter
import com.terracon.survey.views.project_details.ProjectDetailsActivity
import com.terracon.survey.views.register.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class FloraFaunaActivity : AppCompatActivity() {

    private val floraFaunaViewModel by viewModel<FloraFaunaViewModel>()

    private lateinit var binding: FloraFaunaActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FloraFaunaActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupObservers()
        setupUi()
    }

    private fun setupObservers() {
        floraFaunaViewModel.floraFaunaList.observe(this) { data ->
            binding.parentLayout.visibility = View.VISIBLE

        }

        floraFaunaViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.parentLayout.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.parentLayout.visibility = View.VISIBLE
            }
        }

        floraFaunaViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorView.root.visibility = View.VISIBLE
                binding.parentLayout.visibility = View.INVISIBLE
                ErrorUtils.setErrorView(
                    errorMessage = errorMessage,
                    errorBinding = binding.errorView,
                    context = this,
                    pageName = "floraOrFauna",
                    onRetry = {
                        floraFaunaViewModel.fetchFloraFaunaCategories("asd")
                    })
                // showError(errorMessage.toString())
            }else{
                binding.errorView.root.visibility = View.GONE
                binding.parentLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUi() {
         floraFaunaViewModel.project = intent.getSerializableExtra("projectData") as Project

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
             supportActionBar?.title = floraFaunaViewModel.project.projectName
        }
        setupTabs()

    }


    private fun setupTabs(){
        val adapter = TabPagerAdapter(this, 2)
        binding.tabsViewpager.adapter = adapter
        binding.tabsViewpager.isUserInputEnabled = true
        TabLayoutMediator(binding.tabLayout, binding.tabsViewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Flora"
                }
                1 -> {
                    tab.text = "Fauna"

                }
            }
        }.attach()
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
}
