package com.terracon.survey.views.project_details

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.terracon.survey.R
import com.terracon.survey.databinding.ProjectDetailsActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.views.bio_diversity_form_main.BioDiversityFormMainActivity
import com.terracon.survey.views.tree_assessment_form.TreeAssessmentFormActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class ProjectDetailsActivity : AppCompatActivity() {

    private val projectDetailsViewModel by viewModel<ProjectDetailsViewModel>()
    private lateinit var binding: ProjectDetailsActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectDetailsActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()

    }

    private fun setupUi() {

        val project: Project = intent.getSerializableExtra("projectData") as Project
        binding.projectDescText.text = project.projectDesc
        binding.startSurveyBtn.setOnClickListener {
            val intent = Intent(this, BioDiversityFormMainActivity::class.java)
            intent.putExtra("projectData", project as Serializable)
            this.startActivity(intent)
        }
        binding.startSurveyTreeBtn.setOnClickListener {
            val intent = Intent(this, TreeAssessmentFormActivity::class.java)
            intent.putExtra("projectData", project as Serializable)
            this.startActivity(intent)
        }

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = project.projectName
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

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }
}
