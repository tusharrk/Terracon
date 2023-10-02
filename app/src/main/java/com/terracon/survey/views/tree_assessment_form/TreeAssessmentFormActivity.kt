package com.terracon.survey.views.tree_assessment_form

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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.michaelflisar.lumberjack.L
import com.terracon.survey.R
import com.terracon.survey.databinding.TreeAssessmentFormActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TreeAssessmentFormActivity : AppCompatActivity() {

    private val treeAssessmentFormViewModel by viewModel<TreeAssessmentFormViewModel>()
    private lateinit var binding: TreeAssessmentFormActivityBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private var location: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TreeAssessmentFormActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()
        setupLocationService()
        setupObservers()
    }

    private fun setupLocationService() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    location = task.result
                    if (location != null) {
//                        val geocoder = Geocoder(this, Locale.getDefault())
//                        val list: List<Address> =
//                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        binding.gpsEditText.editText?.setText("${location?.latitude} , ${location?.longitude}")
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi() {

        val project: Project = intent.getSerializableExtra("projectData") as Project

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = project.name
        }

        binding.dateEditText.editText?.setOnClickListener {
            showDatePickerDialog()
        }
        binding.timeEditText.editText?.setOnClickListener {
            showTimePickerDialog()
        }
        binding.gpsEditText.editText?.setOnClickListener {
            // getLocation()
            if (location != null) {
                val url =
                    "https://www.google.com/maps/search/?api=1&query=${location?.latitude},${location?.longitude}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }

        }
        binding.gpsEditText.setEndIconOnClickListener {
            getLocation()
        }

        //set pre filled values

        binding.dateEditText.editText?.setText(DateUtils.getTodayDateOrTime("dd MMM yyyy"))
        binding.timeEditText.editText?.setText(DateUtils.getTodayDateOrTime("hh:mm a"))

        binding.habitatAutoCompleteTextView.setAdapter(
            ArrayAdapter(
                this,
                R.layout.dropdown_item,
                resources.getStringArray(R.array.habitat_names)
            )
        )


        binding.radioBtngrp.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.circularRadioBtn -> {
                    binding.radiusEditText.visibility = View.VISIBLE
                    binding.lengthEditText.visibility = View.GONE
                    binding.widthEditText.visibility = View.GONE
                }

                R.id.rectangularRadioBtn -> {
                    binding.radiusEditText.visibility = View.GONE
                    binding.lengthEditText.visibility = View.VISIBLE
                    binding.widthEditText.visibility = View.VISIBLE
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            try {

                if (binding.plotCodeEditText.editText?.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please enter Plot Code", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if (binding.landmarkEditText.editText?.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please enter Landmark", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }


                if (binding.plotTypeEditText.editText?.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please enter Plot Type", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if (binding.habitatAutoCompleteTextView.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please select Habitat", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if (binding.circularRadioBtn.isChecked) {
                    if (binding.radiusEditText.editText?.text.isNullOrBlank()) {
                        Toast.makeText(this, "Please enter Plot Radius", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                } else {
                    if (binding.lengthEditText.editText?.text.isNullOrBlank()) {
                        Toast.makeText(this, "Please enter Plot Length", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    if (binding.widthEditText.editText?.text.isNullOrBlank()) {
                        Toast.makeText(this, "Please enter Plot Width", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }

                if (location == null) {
                    Toast.makeText(this, "Please refresh location", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }

                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.confirmation))
                    .setMessage(getString(R.string.form_submit_msg))
                    .setNeutralButton("Cancel") { dialog, which ->

                    }
                    .setPositiveButton("Submit") { dialog, which ->

                        treeAssessmentFormViewModel.treePoint.project_id = project.id
                        treeAssessmentFormViewModel.treePoint.date =
                            binding.dateEditText.editText?.text.toString()
                        treeAssessmentFormViewModel.treePoint.time =
                            binding.timeEditText.editText?.text.toString()
                        treeAssessmentFormViewModel.treePoint.gps_latitude =
                            location?.latitude.toString()
                        treeAssessmentFormViewModel.treePoint.gps_longitude =
                            location?.longitude.toString()
                        treeAssessmentFormViewModel.treePoint.habitat =
                            binding.habitatAutoCompleteTextView.text.toString()
                        treeAssessmentFormViewModel.treePoint.plot_dimension_type =
                            if (binding.circularRadioBtn.isChecked) "circular" else "rectangular"

                        treeAssessmentFormViewModel.treePoint.landmark =
                            binding.landmarkEditText.editText?.text.toString()
                        treeAssessmentFormViewModel.treePoint.plot_type =
                            binding.plotTypeEditText.editText?.text.toString()
                        treeAssessmentFormViewModel.treePoint.radius =
                            binding.radiusEditText.editText?.text.toString()
                        treeAssessmentFormViewModel.treePoint.width =
                            binding.widthEditText.editText?.text.toString()
                        treeAssessmentFormViewModel.treePoint.length =
                            binding.lengthEditText.editText?.text.toString()
                        treeAssessmentFormViewModel.treePoint.code =
                            binding.plotCodeEditText.editText?.text.toString()


                        treeAssessmentFormViewModel.savePointData(this, project)

                        // treeAssessmentFormViewModel.navigateToDetails(this, project)

                    }
                    .show()
            } catch (e: Exception) {
                L.d { "submit btn error--${e.toString()}" }
                Toast.makeText(this, "Error--${e.toString()}", Toast.LENGTH_LONG).show()
            }
        }
        try {
            onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
                // Back is pressed... Finishing the activity
                showDiscardAlert()
            }
        } catch (e: Exception) {

        }
    }

    private fun setupObservers() {

        treeAssessmentFormViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.saveBtn.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.saveBtn.visibility = View.VISIBLE
            }
        }
    }


    private fun showDatePickerDialog() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTitleText("Select date")
                .build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener {
            binding.dateEditText.editText?.setText(datePicker.headerText)
        }
    }

    private fun showTimePickerDialog() {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setInputMode(INPUT_MODE_CLOCK)
                .setTitleText("Select Time")
                .build()
        timePicker.show(supportFragmentManager, "TIME_PICKER")
        timePicker.addOnPositiveButtonClickListener {
            val cal: Calendar = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            cal.isLenient = false
            val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            binding.timeEditText.editText?.setText(simpleDateFormat.format(cal.time))
        }
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
        if (id == android.R.id.home) {
            showDiscardAlert()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDiscardAlert() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.unsaved_changes))
            .setMessage(getString(R.string.discard_changes_msg))
            .setNeutralButton("Cancel") { dialog, which ->
                // Respond to neutral button press
            }
            .setPositiveButton("Discard") { dialog, which ->
                finish()

            }
            .show()
    }
}
