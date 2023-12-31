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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.ImageProvider
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.bottomsheet.SSPickerOptionsBottomSheet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.michaelflisar.lumberjack.core.L
import com.terracon.survey.R
import com.terracon.survey.databinding.TreeAssessmentDetailsFormActivityBinding
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Project
import com.terracon.survey.model.Species
import com.terracon.survey.model.SpeciesNameDTO
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.DateUtils
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.utils.RealPathUtil
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioAdapter
import com.terracon.survey.views.add_point_form_bio.FragmentSample
import com.terracon.survey.views.add_point_form_bio.PoiAdapter
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TreeAssessmentDetailsFormActivity : AppCompatActivity(),
    SSPickerOptionsBottomSheet.ImagePickerClickListener,
    ImagePickerResultListener {

    private val treeAssessmentFormMainViewModel by viewModel<TreeAssessmentDetailsFormViewModel>()
    private lateinit var binding: TreeAssessmentDetailsFormActivityBinding
    private lateinit var listAdapter: TreeAssessmentDetailsAdapter


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private var location: Location? = null

    private val imagePicker: ImagePicker by lazy {
        registerImagePicker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TreeAssessmentDetailsFormActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupListAdapter()
        setupObservers()
        setupUi()
        setupLocationService()
    }

    private fun setupLocationService() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun setupListAdapter() {
        listAdapter =
            TreeAssessmentDetailsAdapter(TreeAssessmentDetailsAdapter.OnClickListener { item, action ->
                val index = treeAssessmentFormMainViewModel.speciesList.value?.indexOf(item)
                if (index != null) {
                    if(action == "edit"){
                        treeAssessmentFormMainViewModel.isEdit = true
                    treeAssessmentFormMainViewModel.isEditIndex = index
                        treeAssessmentFormMainViewModel.selectedItem = item
                    binding.serialNumberEditText.editText?.setText(item.serial_number)

                       // binding.scientificSpeciesNameEditText.editText?.setText(item.name)
                      //  binding.speciesNameEditText.editText?.setText(treeAssessmentFormMainViewModel.findSpeciesNameFromList(item.name,true))
                        binding.speciesNameEditText.editText?.setText(item.name)
                        binding.commonNameTxtValue.text = item.common_name


                        binding.girthEditText.editText?.setText(item.girth.toString())
                    binding.heightEditText.editText?.setText(item.height.toString())
                    binding.diameterEditText.editText?.setText(item.canopy_diameter.toString())
                    binding.commentsEditText.editText?.setText(item.comment)
                    binding.uploadImage.text =
                        if (item.images.isNullOrEmpty()) getString(R.string.capture_upload_image) else item.images!!

                        treeAssessmentFormMainViewModel.imageLong = if(item.gps_longitude.isNullOrEmpty()) "" else item.gps_longitude!!
                        treeAssessmentFormMainViewModel.imageLat = if(item.gps_latitude.isNullOrEmpty()) "" else item.gps_latitude!!



                        treeAssessmentFormMainViewModel.imageUrl =
                        if (item.images.isNullOrEmpty()) "" else item.images!!

                }else{
                    deleteItem(item,index)
                }
                }
            }, treeAssessmentFormMainViewModel, this)
        binding.projectsRecyclerView.adapter = listAdapter
        binding.projectsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL
            )
        )

    }


    private fun setupObservers() {
        treeAssessmentFormMainViewModel.speciesList.observe(this) { data ->
            listAdapter.submitList(data)
        }
        treeAssessmentFormMainViewModel.speciesDTOList.observe(this) { data ->
            if (!data.isNullOrEmpty()) {
                binding.speciesNameAutoCompleteTextView.setAdapter(
//                    ArrayAdapter(
//                        this, R.layout.dropdown_item,R.id.item, data
//                    )
                    PoiAdapter(this, R.layout.dropdown_item, data)
                )
            }

        }
        binding.speciesNameAutoCompleteTextView.setOnItemClickListener() { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as SpeciesNameDTO?
            binding.speciesNameAutoCompleteTextView.setText(selectedPoi?.scientific_name)
            binding.commonNameTxtValue.text = selectedPoi?.common_name
        }
//        treeAssessmentFormMainViewModel.floraFaunaScientificSpeciesList.observe(this) { data ->
//            if (!data.isNullOrEmpty()) {
//                binding.scientificSpeciesNameAutoCompleteTextView.setAdapter(
//                    ArrayAdapter(
//                        this, R.layout.dropdown_item,R.id.item, data
//                    )
//                )
//            }
//        }
        treeAssessmentFormMainViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.saveBtn.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.saveBtn.visibility = View.VISIBLE
            }
        }

        treeAssessmentFormMainViewModel.isLoadingFullScreen.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressViewFull.root.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.INVISIBLE
            } else {
                binding.progressViewFull.root.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }
        treeAssessmentFormMainViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorView.root.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.INVISIBLE
                ErrorUtils.setErrorView(
                    errorMessage = errorMessage,
                    errorBinding = binding.errorView,
                    context = this,
                    pageName = "projects",
                    onRetry = {
                        treeAssessmentFormMainViewModel.getSpeciesNamesList()
                    })
                // showError(errorMessage.toString())
            } else {
                binding.errorView.root.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi() {

        treeAssessmentFormMainViewModel.project =
            intent.getSerializableExtra("projectData") as Project
        treeAssessmentFormMainViewModel.treePoint =
            intent.getSerializableExtra("pointData") as TreeAssessmentPoint

        treeAssessmentFormMainViewModel.getSpeciesNamesList()


        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = treeAssessmentFormMainViewModel.project.name
        }

//        binding.scientificSpeciesNameAutoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
//            L.d { "item select--${(view as MaterialTextView).text}" }
//            try {
//                binding.speciesNameEditText.editText?.setText(treeAssessmentFormMainViewModel.findSpeciesNameFromList((view as MaterialTextView).text.toString(),true))
//            }catch (e:Exception){
//                L.d { "error---${e}" }
//            }
//        }
//        binding.speciesNameAutoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
//            L.d { "item select--${(view as MaterialTextView).text}" }
//            try {
//                binding.scientificSpeciesNameEditText.editText?.setText(treeAssessmentFormMainViewModel.findSpeciesNameFromList((view as MaterialTextView).text.toString(),false))
//            }catch (e:Exception){
//                L.d { "error---${e}" }
//            }
//        }
//
//        binding.scientificSpeciesNameAutoCompleteTextView.setOnFocusChangeListener { view, isFocus ->
//            try {
//                if(!isFocus){
//                    L.d { "errorrororo---${treeAssessmentFormMainViewModel.floraFaunaScientificSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString())  }}"}
//                    if(treeAssessmentFormMainViewModel.floraFaunaScientificSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString())  } == null){
//                        binding.scientificSpeciesNameEditText.editText?.setText("Not Specified")
//                        binding.speciesNameEditText.editText?.setText(treeAssessmentFormMainViewModel.findSpeciesNameFromList((view as MaterialAutoCompleteTextView).text.toString(),true))
//
//                    }
//                }
//            }catch (e:Exception){
//                L.d { "error--${e}" }
//            }
//        }
//
//        binding.speciesNameAutoCompleteTextView.setOnFocusChangeListener { view, isFocus ->
//            try {
//                if(!isFocus){
//                    L.d { "errorrororo---${treeAssessmentFormMainViewModel.floraFaunaSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString()) }}"}
//                    if(treeAssessmentFormMainViewModel.floraFaunaSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString())  } == null){
//                        binding.speciesNameEditText.editText?.setText("Not Specified")
//                        binding.scientificSpeciesNameEditText.editText?.setText(treeAssessmentFormMainViewModel.findSpeciesNameFromList((view as MaterialAutoCompleteTextView).text.toString(),false))
//                    }
//                }
//            }catch (e:Exception){
//                L.d { "error--${e}" }
//            }
//        }

        binding.addItemBtn.setOnClickListener {
            if (binding.serialNumberEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please enter Serial Number", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.speciesNameEditText.editText?.text.isNullOrBlank() || binding.speciesNameEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please select Species", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.girthEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please enter Girth", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.heightEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please enter Height", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.diameterEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please enter Canopy Diameter", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
          //  binding.scientificSpeciesNameAutoCompleteTextView.clearFocus()
            binding.speciesNameAutoCompleteTextView.clearFocus()
            var species = TreeAssessmentSpecies(
                name = binding.speciesNameEditText.editText?.text.toString(),
                common_name = binding.commonNameTxtValue.text.toString(),
                serial_number = binding.serialNumberEditText.editText?.text.toString(),
                girth = binding.girthEditText.editText?.text.toString(),
                height = binding.heightEditText.editText?.text.toString(),
                canopy_diameter = binding.diameterEditText.editText?.text.toString(),
                images = treeAssessmentFormMainViewModel.imageUrl,
                comment = binding.commentsEditText.editText?.text.toString(),
                gps_latitude = treeAssessmentFormMainViewModel.imageLat,
                gps_longitude = treeAssessmentFormMainViewModel.imageLong,
                tempId = treeAssessmentFormMainViewModel.treePoint.dbId

            )
            if (treeAssessmentFormMainViewModel.isEdit) {
                treeAssessmentFormMainViewModel.isEditIndex?.let { it1 ->
                    species.dbId = treeAssessmentFormMainViewModel.selectedItem.dbId
                    treeAssessmentFormMainViewModel.updateSpecies(this,species)
                }
            } else {
                setupPointDataPayload(species)
               // treeAssessmentFormMainViewModel.addItemToList(species)
            }
            listAdapter.notifyDataSetChanged()

            binding.serialNumberEditText.editText?.setText("")

          //  binding.scientificSpeciesNameEditText.editText?.setText("")
            binding.speciesNameEditText.editText?.setText("")
            binding.commonNameTxtValue.text = ""
            binding.serialNumberEditText.editText?.setText("")
            binding.serialNumberEditText.editText?.setText("")
            binding.girthEditText.editText?.setText("")
            binding.heightEditText.editText?.setText("")
            binding.diameterEditText.editText?.setText("")
            binding.commentsEditText.editText?.setText("")

            binding.uploadImage.text = resources.getString(R.string.capture_upload_image)
            treeAssessmentFormMainViewModel.imageUrl = ""
            treeAssessmentFormMainViewModel.imageLong = ""
            treeAssessmentFormMainViewModel.imageLat = ""
            treeAssessmentFormMainViewModel.isEditIndex = null
            treeAssessmentFormMainViewModel.isEdit = false
        }

        binding.saveBtn.setOnClickListener {
        saveData()
        }

        //set pre filled values
        binding.uploadImage.setOnClickListener {
        //    binding.scientificSpeciesNameAutoCompleteTextView.clearFocus()
            binding.speciesNameAutoCompleteTextView.clearFocus()
            getLocation()
//            startActivity(Intent(this, FragmentSample::class.java))
//            PixBus.results {
//                when (it.status) {
//                    PixEventCallback.Status.SUCCESS -> {
//                        it.data.forEach {
//                            Log.e("TAG_X_NEW", "showCameraFragment: ${it}")
//                            Log.d("TAG_X_FILE", "File path: ${RealPathUtil.getRealPath(this,it)}")
//
//                            binding.uploadImage.text = RealPathUtil.getRealPath(this,it)
//                        }
//                    }
//                    PixEventCallback.Status.BACK_PRESSED -> {
//                        supportFragmentManager.popBackStack()
//                    }
//                }
//            }

        }
//        try {
//            onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
//                // Back is pressed... Finishing the activity
//                showDiscardAlert()
//            }
//        }catch (e:Exception){
//
//        }

//        binding.speciesNameAutoCompleteTextView.setAdapter(
//            ArrayAdapter(this, R.layout.dropdown_item, resources.getStringArray(
//                R.array.season_names))
//        )


    }

    private fun saveData(){
        L.d { "list--${treeAssessmentFormMainViewModel.getSpeciesList()}" }
        if (treeAssessmentFormMainViewModel.speciesList.value.isNullOrEmpty()) {
            Toast.makeText(this, "Please add Species", Toast.LENGTH_LONG).show()
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.confirmation))
            .setMessage(getString(R.string.form_submit_msg))
            .setNeutralButton("Cancel") { dialog, which ->

            }
            .setPositiveButton("Submit") { dialog, which ->
               // setupPointDataPayload()
            }
            .show()
    }

    private fun deleteItem(item:TreeAssessmentSpecies,index:Int){
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete")
            .setMessage("Are you sure you want to Delete this Item?")
            .setNeutralButton("Cancel") { dialog, which ->
                // Respond to neutral button press
            }
            .setPositiveButton("Delete") { dialog, which ->
                treeAssessmentFormMainViewModel.deleteSpecieFromLocalDB(item)
               // listAdapter.notifyItemRemoved(index)
            }
            .show()
    }

    private fun setupPointDataPayload(species: TreeAssessmentSpecies) {
        species.user_created_date = DateUtils.getTodayDateOrTime("yyyy-MM-dd hh:mm:ss")

//        treeAssessmentFormMainViewModel.treeSpecies.bio_diversity_survey_points_id =
//            treeAssessmentFormMainViewModel.pointBio.dbId
//        treeAssessmentFormMainViewModel.pointBioDetails.species = addPointFormBioViewModel.getSpeciesList()
        L.d { "data--${treeAssessmentFormMainViewModel.treePoint}" }
        treeAssessmentFormMainViewModel.savePointData(this,species)
    }

    private fun openFilePicker() {
        imagePicker.open(PickerType.CAMERA)

        // val pickerOptionBottomSheet = SSPickerOptionsBottomSheet.newInstance()
        // pickerOptionBottomSheet.show(supportFragmentManager,"tag")

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      //  menuInflater.inflate(R.menu.menu_main_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.saveBtn) {
            saveData()
           // AppUtils.logoutUser(this)
            return true
        }
        if(id==android.R.id.home){
            //showDiscardAlert()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showDiscardAlert(){
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

    override fun onImagePick(uri: Uri?) {
        if (uri != null) {
            Log.d("TAG_X_FILE", "File path: ${uri}")
            binding.uploadImage.text = uri.toString()
            treeAssessmentFormMainViewModel.imageUrl = uri.toString()
            //  binding.uploadImage.text = RealPathUtil.getRealPath(this,uri)
        }
        //RealPathUtil.getRealPath(this,uri)?.let { Log.d("TAG_FILEE__", it) }
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
    }

    override fun onImageProvider(provider: ImageProvider) {
        when (provider) {
            ImageProvider.GALLERY -> {
                //Open gallery
                imagePicker.open(PickerType.GALLERY)

            }

            ImageProvider.CAMERA -> {
                //Open camera
                imagePicker.open(PickerType.CAMERA)

            }

            ImageProvider.NONE -> {}
        }
    }


    //location
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
            }else{
                Toast.makeText(this, "Location Permission not allowed", Toast.LENGTH_LONG).show()
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
                        treeAssessmentFormMainViewModel.imageLong = location?.longitude.toString()
                        treeAssessmentFormMainViewModel.imageLat = location?.latitude.toString()
//                        val geocoder = Geocoder(this, Locale.getDefault())
//                        val list: List<Address> =
//                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        // binding.gpsEditText.editText?.setText("${location?.latitude} , ${location?.longitude}")
                    }
                }
                openFilePicker()
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
}
