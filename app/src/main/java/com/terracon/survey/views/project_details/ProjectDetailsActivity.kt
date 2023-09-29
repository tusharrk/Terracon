package com.terracon.survey.views.project_details

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.terracon.survey.R
import com.terracon.survey.databinding.ProjectDetailsActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.views.bio_diversity_form_main.BioDiversityFormMainActivity
import com.terracon.survey.views.points_list.PointsListActivity
import com.terracon.survey.views.tree_assessment_form.TreeAssessmentFormActivity
import com.terracon.survey.views.tree_points_list.TreePointsListActivity
import com.terracon.survey.views.tree_points_list.TreePointsListAdapter
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
        binding.projectDescText.text = project.description
        binding.projectLocationTxt.text = getString(R.string.project_location, project.location_of_survey)
        binding.projectAreaTxt.text = getString(R.string.project_area, project.total_study_area_in_sq_km)
        if(project.type == "bio_diversity"){
            binding.startSurveyBtn.visibility = View.VISIBLE
            binding.startSurveyTreeBtn.visibility = View.GONE
        }else{
            binding.startSurveyBtn.visibility = View.GONE
            binding.startSurveyTreeBtn.visibility = View.VISIBLE

        }
        binding.startSurveyBtn.setOnClickListener {
          //  val intent = Intent(this, BioDiversityFormMainActivity::class.java)
            val intent = Intent(this, PointsListActivity::class.java)
            intent.putExtra("projectData", project as Serializable)
            this.startActivity(intent)
        }
        binding.startSurveyTreeBtn.setOnClickListener {
            val intent = Intent(this, TreePointsListActivity::class.java)
            intent.putExtra("projectData", project as Serializable)
            this.startActivity(intent)
        }

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = project.name
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
