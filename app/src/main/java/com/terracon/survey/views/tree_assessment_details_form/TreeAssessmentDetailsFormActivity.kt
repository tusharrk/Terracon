package com.terracon.survey.views.tree_assessment_details_form

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.terracon.survey.R
import com.terracon.survey.databinding.TreeAssessmentDetailsFormActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.DateUtils
import com.terracon.survey.utils.RealPathUtil
import com.terracon.survey.views.add_point_form_bio.FragmentSample
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TreeAssessmentDetailsFormActivity : AppCompatActivity() {

    private val treeAssessmentFormMainViewModel by viewModel<TreeAssessmentDetailsFormViewModel>()
    private lateinit var binding: TreeAssessmentDetailsFormActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TreeAssessmentDetailsFormActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()
    }






    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi() {

        val project: Project = intent.getSerializableExtra("projectData") as Project

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = project.projectName
        }


        binding.saveBtn.setOnClickListener {
            treeAssessmentFormMainViewModel.navigateToFloraFaunaActivity(this,project)
        }

        //set pre filled values
        binding.uploadImage.setOnClickListener {
            // openFilePicker()
            startActivity(Intent(this, FragmentSample::class.java))
            PixBus.results {
                when (it.status) {
                    PixEventCallback.Status.SUCCESS -> {
                        it.data.forEach {
                            Log.e("TAG_X_NEW", "showCameraFragment: ${it}")
                            Log.d("TAG_X_FILE", "File path: ${RealPathUtil.getRealPath(this,it)}")

                            binding.uploadImage.text = RealPathUtil.getRealPath(this,it)
                        }
                    }
                    PixEventCallback.Status.BACK_PRESSED -> {
                        supportFragmentManager.popBackStack()
                    }
                }
            }

        }

        binding.speciesNameAutoCompleteTextView.setAdapter(
            ArrayAdapter(this, R.layout.dropdown_item, resources.getStringArray(
                R.array.season_names))
        )


    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }
}
