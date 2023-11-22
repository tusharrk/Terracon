package com.terracon.survey.views.bio_diversity_form_main

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
import com.michaelflisar.lumberjack.core.L
import com.terracon.survey.R
import com.terracon.survey.databinding.BioDiversityFormMainActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BioDiversityFormMainActivity : AppCompatActivity() {

    private val bioDiversityFormMainViewModel by viewModel<BioDiversityFormMainViewModel>()
    private lateinit var binding: BioDiversityFormMainActivityBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private var location: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BioDiversityFormMainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

       // val isEdit: Boolean? = intent.getSerializableExtra("isEdit") as Boolean?
      //  if (isEdit != null) {
      //      bioDiversityFormMainViewModel.isEdit = isEdit
      //  }

        setupUi()
        setupObservers()
       // if(!bioDiversityFormMainViewModel.isEdit){
            setupLocationService()
       // }

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
                        L.d { "Location Complete ${location?.latitude} -- ${location?.longitude}" }
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
           // getLocation()
            setupLocationService()
        }

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


                L.d { binding.seasonNameAutoCompleteTextView.text.toString() }
                if (binding.seasonNameAutoCompleteTextView.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please select Season Name", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }
                if (binding.weatherConditionNameAutoCompleteTextView.text.isNullOrBlank()) {
                    Toast.makeText(
                        this,
                        "Please select Weather Condition",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    return@setOnClickListener
                }
                if (binding.habitatAutoCompleteTextView.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please select Habitat", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (binding.plotCodeEditText.editText?.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please enter Plot Code", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (binding.villageEditText.editText?.text.isNullOrBlank()) {
                    Toast.makeText(this, "Please enter Village", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
//
//                if (binding.plotTypeEditText.editText?.text.isNullOrBlank()) {
//                    Toast.makeText(this, "Please enter Plot Type", Toast.LENGTH_LONG).show()
//                    return@setOnClickListener
//                }
                if (binding.circularRadioBtn.isChecked) {
                    if (binding.radiusEditText.editText?.text.isNullOrBlank()) {
                        Toast.makeText(this, "Please enter Plot Radius", Toast.LENGTH_LONG)
                            .show()
                        return@setOnClickListener
                    }
                } else {
                    if (binding.lengthEditText.editText?.text.isNullOrBlank()) {
                        Toast.makeText(this, "Please enter Plot Length", Toast.LENGTH_LONG)
                            .show()
                        return@setOnClickListener
                    }
                    if (binding.widthEditText.editText?.text.isNullOrBlank()) {
                        Toast.makeText(this, "Please enter Plot Width", Toast.LENGTH_LONG)
                            .show()
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

                        bioDiversityFormMainViewModel.bioPoint.project_id = project.id
                        bioDiversityFormMainViewModel.bioPoint.date =
                            binding.dateEditText.editText?.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.time =
                            binding.timeEditText.editText?.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.gps_latitude =
                            location?.latitude.toString()
                        bioDiversityFormMainViewModel.bioPoint.gps_longitude =
                            location?.longitude.toString()

                        bioDiversityFormMainViewModel.bioPoint.season_name =
                            binding.seasonNameAutoCompleteTextView.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.weather_condition =
                            binding.weatherConditionNameAutoCompleteTextView.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.habitat =
                            binding.habitatAutoCompleteTextView.text.toString()

                        bioDiversityFormMainViewModel.bioPoint.village =
                            binding.villageEditText.editText?.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.plot_dimension_type =
                            if (binding.circularRadioBtn.isChecked) "circular" else "rectangular"

                        bioDiversityFormMainViewModel.bioPoint.plot_type = ""
                        bioDiversityFormMainViewModel.bioPoint.radius =
                            binding.radiusEditText.editText?.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.width =
                            binding.widthEditText.editText?.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.length =
                            binding.lengthEditText.editText?.text.toString()
                        bioDiversityFormMainViewModel.bioPoint.code =
                            binding.plotCodeEditText.editText?.text.toString()

                        bioDiversityFormMainViewModel.savePointData(this, project)

                    }
                    .show()
            } catch (e: Exception) {
                L.d { "submit btn error--${e.toString()}" }
                Toast.makeText(this, "Error--${e.toString()}", Toast.LENGTH_LONG).show()
            }

        }

        //set pre filled values

        binding.dateEditText.editText?.setText(DateUtils.getTodayDateOrTime("dd MMM yyyy"))
        binding.timeEditText.editText?.setText(DateUtils.getTodayDateOrTime("hh:mm a"))

//        if (!project.villages.isNullOrBlank()) {
//            binding.villageAutoCompleteTextView.setAdapter(
//                ArrayAdapter(
//                    this,
//                    R.layout.dropdown_item,
//                    project.villages.split(",")
//                )
//            )
//        }



        binding.seasonNameAutoCompleteTextView.setAdapter(
            ArrayAdapter(
                this,
                R.layout.dropdown_item,
                R.id.item,
                resources.getStringArray(R.array.season_names)
            )
        )

        binding.weatherConditionNameAutoCompleteTextView.setAdapter(
            ArrayAdapter(
                this,
                R.layout.dropdown_item,
                R.id.item,
                resources.getStringArray(R.array.weather_condition_names)
            )
        )

        binding.habitatAutoCompleteTextView.setAdapter(
            ArrayAdapter(
                this,
                R.layout.dropdown_item,
                R.id.item,
                resources.getStringArray(R.array.habitat_names)
            )
        )

        try {
            onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
                // Back is pressed... Finishing the activity
                showDiscardAlert()
            }
        } catch (e: Exception) {

        }

    }

    private fun setupObservers() {

        bioDiversityFormMainViewModel.isLoading.observe(this) { isLoading ->
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

}
