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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
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
    private lateinit var location: Location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BioDiversityFormMainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUi()
        setupLocationService()
    }

    private fun setupLocationService(){
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
                        binding.gpsEditText.editText?.setText("${location.latitude} , ${location.longitude}")
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
            supportActionBar?.title = project.projectName
        }

        binding.dateEditText.editText?.setOnClickListener {
            showDatePickerDialog()
        }
        binding.timeEditText.editText?.setOnClickListener {
            showTimePickerDialog()
        }
        binding.gpsEditText.editText?.setOnClickListener {
           // getLocation()
            if(location != null){
                val url = "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }

        }
        binding.gpsEditText.setEndIconOnClickListener {
            getLocation()
        }

        binding.saveBtn.setOnClickListener {
            bioDiversityFormMainViewModel.navigateToFloraFaunaActivity(this,project)
        }

        //set pre filled values

        binding.dateEditText.editText?.setText(DateUtils.getTodayDateOrTime("dd MMM yyyy"))
        binding.timeEditText.editText?.setText(DateUtils.getTodayDateOrTime("hh:mm a"))

        binding.seasonNameAutoCompleteTextView.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, resources.getStringArray(R.array.season_names)))

        binding.weatherConditionNameAutoCompleteTextView.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, resources.getStringArray(R.array.weather_condition_names)))

        binding.habitatAutoCompleteTextView.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, resources.getStringArray(R.array.habitat_names)))

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
        return super.onOptionsItemSelected(item)
    }
}
