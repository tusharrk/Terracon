package com.terracon.survey.views.add_point_form_bio

import android.Manifest
import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.ImageProvider
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.bottomsheet.SSPickerOptionsBottomSheet
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textview.MaterialTextView
import com.michaelflisar.lumberjack.core.L
import com.terracon.survey.R
import com.terracon.survey.databinding.AddPointFormBioActivityBinding
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Project
import com.terracon.survey.model.Species
import com.terracon.survey.model.SpeciesNameDTO
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.DateUtils
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.utils.FileHelper
import com.terracon.survey.utils.FileUtils.getFileFromUri
import com.terracon.survey.utils.RealPathUtil
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class AddPointFormBioActivity : AppCompatActivity(),
    SSPickerOptionsBottomSheet.ImagePickerClickListener, ImagePickerResultListener {

    private val addPointFormBioViewModel by viewModel<AddPointFormBioViewModel>()
    private lateinit var binding: AddPointFormBioActivityBinding
    private lateinit var listAdapter: AddPointFormBioAdapter

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private var location: Location? = null

    private val imagePicker: ImagePicker by lazy {
        registerImagePicker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddPointFormBioActivityBinding.inflate(layoutInflater)
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
        listAdapter = AddPointFormBioAdapter(AddPointFormBioAdapter.OnClickListener { item,action ->
            val index = addPointFormBioViewModel.speciesBioList.value?.indexOf(item)
            if (index != null) {
                if(action == "edit"){
                    addPointFormBioViewModel.isEdit = true
                    addPointFormBioViewModel.isEditIndex = index
                    addPointFormBioViewModel.selectedItem = item

                  //  binding.scientificSpeciesNameEditText.editText?.setText(item.name)
                    binding.speciesNameEditText.editText?.setText(item.name)
                    binding.commonNameTxtValue.text = item.common_name
                    //binding.speciesNameEditText.editText?.setText(addPointFormBioViewModel.findSpeciesNameFromList(item.name,true))

                    binding.countEditText.editText?.setText(item.count)
                    binding.commentEditText.editText?.setText(item.comment)
                    binding.uploadImage.text =
                        if (item.images.isNullOrEmpty()) getString(R.string.capture_upload_image) else item.images!!

                    addPointFormBioViewModel.imageUrl =
                        if (item.images.isNullOrEmpty()) "" else item.images!!
                    addPointFormBioViewModel.imageLong = if(item.gps_longitude.isNullOrEmpty()) "" else item.gps_longitude!!
                    addPointFormBioViewModel.imageLat = if(item.gps_latitude.isNullOrEmpty()) "" else item.gps_latitude!!

                }else{
                    deleteItem(item,index)
                }


                //addPointFormBioViewModel.updateCountValue(item, index)
                // listAdapter.notifyItemChanged(index)
            }
//            val intent = Intent(this, ProjectDetailsActivity::class.java)
//            intent.putExtra("projectData", project as Serializable)
//            this.startActivity(intent)
//            try {
//                val index = homeViewModel.projectsList.value?.indexOf(project)
//
//            } catch (e: Exception) {
//                Log.d("TAG_X", "error update read--$e")
//            }
        }, addPointFormBioViewModel, this)
        binding.projectsRecyclerView.adapter = listAdapter
        binding.projectsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL
            )
        )

    }

    private fun setupObservers() {
        addPointFormBioViewModel.speciesBioList.observe(this) { data ->
            listAdapter.submitList(data)
        }
        addPointFormBioViewModel.speciesDTOList.observe(this) { data ->
            if (!data.isNullOrEmpty()) {
                binding.speciesNameAutoCompleteTextView.setAdapter(
//                    ArrayAdapter(
//                        this, R.layout.dropdown_item,R.id.item, data
//                    )
                    PoiAdapter(this, R.layout.dropdown_item, data)
                   // AutoSuggestAdapter(this, R.layout.dropdown_item, data)
                          //  CustomAutoCompleteAdapter(this, R.layout.dropdown_item, data)
                )
//                val autoCompleteTextView: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
//
//                val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")
//
//                val adapter =
//                autoCompleteTextView.setAdapter(adapter)
            }
        }
        binding.speciesNameAutoCompleteTextView.setOnItemClickListener() { parent, _, position, id ->
            val selectedPoi = parent.adapter.getItem(position) as SpeciesNameDTO?
            binding.speciesNameAutoCompleteTextView.setText(selectedPoi?.scientific_name)
            binding.commonNameTxtValue.text = selectedPoi?.common_name
        }
//        addPointFormBioViewModel.floraFaunaScientificSpeciesList.observe(this) { data ->
//            if (!data.isNullOrEmpty()) {
//                binding.scientificSpeciesNameAutoCompleteTextView.setAdapter(
//                    ArrayAdapter(
//                        this, R.layout.dropdown_item, data
//                    )
//                )
//            }
//        }
        addPointFormBioViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressView.root.visibility = View.VISIBLE
                binding.saveBtn.visibility = View.INVISIBLE
            } else {
                binding.progressView.root.visibility = View.GONE
                binding.saveBtn.visibility = View.VISIBLE
            }
        }

        addPointFormBioViewModel.isLoadingFullScreen.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressViewFull.root.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.INVISIBLE
            } else {
                binding.progressViewFull.root.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }
        addPointFormBioViewModel.errorState.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.errorView.root.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.INVISIBLE
                ErrorUtils.setErrorView(
                    errorMessage = errorMessage,
                    errorBinding = binding.errorView,
                    context = this,
                    pageName = "projects",
                    onRetry = {
                        addPointFormBioViewModel.getSpeciesNamesList()
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

        addPointFormBioViewModel.project = intent.getSerializableExtra("projectData") as Project
        addPointFormBioViewModel.pointBio = intent.getSerializableExtra("pointData") as BioPoint
        addPointFormBioViewModel.type = intent.getStringExtra("type").toString()
        addPointFormBioViewModel.subType = intent.getStringExtra("subType").toString()

        addPointFormBioViewModel.getSpeciesNamesList()

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = addPointFormBioViewModel.project.name
        }

//        binding.speciesNameAutoCompleteTextView.setAdapter(
//            ArrayAdapter(
//                this, R.layout.dropdown_item, resources.getStringArray(
//                    R.array.season_names
//                )
//            )
//        )

//        binding.scientificSpeciesNameAutoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
//            L.d { "item select--${(view as MaterialTextView).text}" }
//            try {
//              binding.speciesNameEditText.editText?.setText(addPointFormBioViewModel.findSpeciesNameFromList((view as MaterialTextView).text.toString(),true))
//            }catch (e:Exception){
//                L.d { "error---${e}" }
//            }
//        }
//        binding.speciesNameAutoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
//            L.d { "item select--${(view as MaterialTextView).text}" }
//            try {
//                binding.scientificSpeciesNameEditText.editText?.setText(addPointFormBioViewModel.findSpeciesNameFromList((view as MaterialTextView).text.toString(),false))
//            }catch (e:Exception){
//                L.d { "error---${e}" }
//            }
//        }

//        binding.scientificSpeciesNameAutoCompleteTextView.setOnFocusChangeListener { view, isFocus ->
//            try {
//            if(!isFocus){
//                L.d { "errorrororo---${addPointFormBioViewModel.floraFaunaScientificSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString())  }}"}
//            if(addPointFormBioViewModel.floraFaunaScientificSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString())  } == null){
//                binding.scientificSpeciesNameEditText.editText?.setText("Not Specified")
//                binding.speciesNameEditText.editText?.setText(addPointFormBioViewModel.findSpeciesNameFromList((view as MaterialAutoCompleteTextView).text.toString(),true))
//
//            }
//            }
//            }catch (e:Exception){
//                L.d { "error--${e}" }
//            }
//        }
//
//        binding.speciesNameAutoCompleteTextView.setOnFocusChangeListener { view, isFocus ->
//            try {
//                if(!isFocus){
//                L.d { "errorrororo---${addPointFormBioViewModel.floraFaunaSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString()) }}"}
//                if(addPointFormBioViewModel.floraFaunaSpeciesList.value?.firstOrNull { item -> item == ((view as MaterialAutoCompleteTextView).text.toString())  } == null){
//                    binding.speciesNameEditText.editText?.setText("Not Specified")
//                    binding.scientificSpeciesNameEditText.editText?.setText(addPointFormBioViewModel.findSpeciesNameFromList((view as MaterialAutoCompleteTextView).text.toString(),false))
//                }
//            }
//        }catch (e:Exception){
//            L.d { "error--${e}" }
//        }
//        }


        binding.addItemBtn.setOnClickListener {
            if (binding.speciesNameEditText.editText?.text.isNullOrBlank() || binding.speciesNameEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please select Species", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
           // binding.scientificSpeciesNameAutoCompleteTextView.clearFocus()
            binding.speciesNameAutoCompleteTextView.clearFocus()
            var species = Species(
                name = binding.speciesNameEditText.editText?.text.toString(),
                common_name = binding.commonNameTxtValue.text.toString(),
                count = binding.countEditText.editText?.text.toString(),
                images = addPointFormBioViewModel.imageUrl,
                comment = binding.commentEditText.editText?.text.toString(),
                gps_latitude = addPointFormBioViewModel.imageLat,
                gps_longitude = addPointFormBioViewModel.imageLong,
            )
            if (addPointFormBioViewModel.isEdit) {
                addPointFormBioViewModel.isEditIndex?.let { it1 ->
                    species.dbId = addPointFormBioViewModel.selectedItem.dbId
                    addPointFormBioViewModel.updateSpecies(this,species)
//                    addPointFormBioViewModel.updateCountValue(
//                        species,
//                        it1
//                    )
//                    setupPointDataPayload(species)
                }
            } else {
               // addPointFormBioViewModel.addItemToList(species)
                setupPointDataPayload(species)
            }
            listAdapter.notifyDataSetChanged()

           // binding.scientificSpeciesNameEditText.editText?.setText("")
            binding.speciesNameEditText.editText?.setText("")
            binding.commonNameTxtValue.text = ""
            binding.countEditText.editText?.setText("0")
            binding.commentEditText.editText?.setText("")
            binding.uploadImage.text = resources.getString(R.string.capture_upload_image)
            addPointFormBioViewModel.imageUrl = ""
            addPointFormBioViewModel.imageLong = ""
            addPointFormBioViewModel.imageLat = ""
            addPointFormBioViewModel.isEditIndex = null
            addPointFormBioViewModel.isEdit = false
        }
        binding.incrementCount.setOnClickListener {
            try {
               // binding.scientificSpeciesNameAutoCompleteTextView.clearFocus()
                binding.speciesNameAutoCompleteTextView.clearFocus()
                var count = Integer.parseInt(binding.countEditText.editText?.text.toString())
                count += 1
                binding.countEditText.editText?.setText(count.toString())
            } catch (e: Exception) {
                Toast.makeText(this, "Error:- $e", Toast.LENGTH_LONG).show()
            }
        }
        binding.decrementCount.setOnClickListener {
            try {
              //  binding.scientificSpeciesNameAutoCompleteTextView.clearFocus()
                binding.speciesNameAutoCompleteTextView.clearFocus()
                var count = Integer.parseInt(binding.countEditText.editText?.text.toString())
                if (count > 0) {
                    count -= 1
                    binding.countEditText.editText?.setText(count.toString())
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error:- $e", Toast.LENGTH_LONG).show()
            }
        }

        binding.uploadImage.setOnClickListener {
          //  binding.scientificSpeciesNameAutoCompleteTextView.clearFocus()
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



        binding.saveBtn.setOnClickListener {

            saveData()
        }
//        try {
//            onBackPressedDispatcher.addCallback(this /* lifecycle owner */) {
//                // Back is pressed... Finishing the activity
//                showDiscardAlert()
//            }
//        }catch (e:Exception){
//
//        }
    }

    private fun saveData(){
       // L.d { "list--${addPointFormBioViewModel.getSpeciesList()}" }
        if (addPointFormBioViewModel.speciesBioList.value.isNullOrEmpty()) {
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

    private fun setupPointDataPayload(species:Species) {
        species.user_created_date = DateUtils.getTodayDateOrTime("yyyy-MM-dd hh:mm:ss")

        addPointFormBioViewModel.pointBioDetails.type = addPointFormBioViewModel.type
        addPointFormBioViewModel.pointBioDetails.sub_type = addPointFormBioViewModel.subType
        addPointFormBioViewModel.pointBioDetails.tempId =
            addPointFormBioViewModel.pointBio.dbId
       // addPointFormBioViewModel.pointBioDetails.species = addPointFormBioViewModel.getSpeciesList()
        addPointFormBioViewModel.pointBioDetails.species = arrayListOf(species)
        L.d { "data--${addPointFormBioViewModel.pointBio}" }
        addPointFormBioViewModel.savePointData(this)
    }

    private fun deleteItem(item:Species,index:Int){
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete")
            .setMessage("Are you sure you want to Delete this Item?")
            .setNeutralButton("Cancel") { dialog, which ->
                // Respond to neutral button press
            }
            .setPositiveButton("Delete") { dialog, which ->
                addPointFormBioViewModel.deleteSpecieFromLocalDB(item)
               // listAdapter.notifyItemRemoved(index)

            }
            .show()
    }

    private fun openFilePicker() {
        imagePicker.open(PickerType.CAMERA)
//        imagePicker.allowCropping(true).compressImage(true, 50).open(PickerType.CAMERA)

        // val pickerOptionBottomSheet = SSPickerOptionsBottomSheet.newInstance()
        // pickerOptionBottomSheet.show(supportFragmentManager,"tag")

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       // menuInflater.inflate(R.menu.menu_main_save, menu)
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

    override fun onImagePick(uri: Uri?) {
        if (uri != null) {
            Log.d("TAG_X_FILE", "File path: ${uri}")
            binding.uploadImage.text = uri.toString()
            addPointFormBioViewModel.imageUrl = uri.toString()
            ///Glide.with(this).load(uri).into(binding.imageView);

//            val fileHelper = FileHelper()
            // binding.uploadImage.text = file?.absolutePath
        }
        //RealPathUtil.getRealPath(this,uri)?.let { Log.d("TAG_FILEE__", it) }
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
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
                        addPointFormBioViewModel.imageLong = location?.longitude.toString()
                        addPointFormBioViewModel.imageLat = location?.latitude.toString()
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

//content://com.terracon.survey.com.app.imagepickerlibrary.provider/external_files/Android/data/com.terracon.survey/files/Pictures/JPEG_20230926_170012_5514495750614150298.jpg
//file:///storage/emulated/0/Android/data/com.terracon.survey/files/Pictures/JPEG_20230926_170735_1845284174450346692.jpg