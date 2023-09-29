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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.michaelflisar.lumberjack.L
import com.terracon.survey.R
import com.terracon.survey.databinding.TreeAssessmentDetailsFormActivityBinding
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Project
import com.terracon.survey.model.Species
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.DateUtils
import com.terracon.survey.utils.ErrorUtils
import com.terracon.survey.utils.RealPathUtil
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioAdapter
import com.terracon.survey.views.add_point_form_bio.FragmentSample
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TreeAssessmentDetailsFormActivity : AppCompatActivity(), SSPickerOptionsBottomSheet.ImagePickerClickListener,
    ImagePickerResultListener {

    private val treeAssessmentFormMainViewModel by viewModel<TreeAssessmentDetailsFormViewModel>()
    private lateinit var binding: TreeAssessmentDetailsFormActivityBinding
    private lateinit var listAdapter: TreeAssessmentDetailsAdapter

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
    }

    private fun setupListAdapter() {
        listAdapter = TreeAssessmentDetailsAdapter(TreeAssessmentDetailsAdapter.OnClickListener { item ->
            val index = treeAssessmentFormMainViewModel.speciesList.value?.indexOf(item)
            if (index != null) {
                treeAssessmentFormMainViewModel.isEdit = true
                treeAssessmentFormMainViewModel.isEditIndex = index

                binding.serialNumberEditText.editText?.setText(item.serial_number)
                binding.speciesEditText.editText?.setText(item.name)
                binding.girthEditText.editText?.setText(item.girth.toString())
                binding.heightEditText.editText?.setText(item.height.toString())
                binding.diameterEditText.editText?.setText(item.canopy_diameter.toString())
                binding.commentsEditText.editText?.setText(item.comment)
                binding.uploadImage.text =  if (item.images.isNullOrEmpty()) getString(R.string.capture_upload_image) else item.images!!


                treeAssessmentFormMainViewModel.imageUrl =
                    if (item.images.isNullOrEmpty()) "" else item.images!!
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
        treeAssessmentFormMainViewModel.floraFaunaSpeciesList.observe(this) { data ->
            if (!data.isNullOrEmpty()) {
                binding.speciesNameAutoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        this, R.layout.dropdown_item, data
                    )
                )
            }

        }
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
                        treeAssessmentFormMainViewModel.getSpeciesList()
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

        treeAssessmentFormMainViewModel.project =  intent.getSerializableExtra("projectData") as Project
        treeAssessmentFormMainViewModel.treePoint = intent.getSerializableExtra("pointData") as TreeAssessmentPoint

        treeAssessmentFormMainViewModel.getSpeciesNamesList()


        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = treeAssessmentFormMainViewModel.project.name
        }

        binding.addItemBtn.setOnClickListener {
            if (binding.serialNumberEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please enter Serial Number", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.speciesEditText.editText?.text.isNullOrBlank()) {
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
            var species = TreeAssessmentSpecies(
                name = binding.speciesEditText.editText?.text.toString(),
                serial_number = binding.serialNumberEditText.editText?.text.toString(),
                girth = binding.girthEditText.editText?.text.toString(),
                height = binding.heightEditText.editText?.text.toString(),
                canopy_diameter = binding.diameterEditText.editText?.text.toString(),
                images = treeAssessmentFormMainViewModel.imageUrl,
                comment = binding.commentsEditText.editText?.text.toString()

            )
            if (treeAssessmentFormMainViewModel.isEdit) {
                treeAssessmentFormMainViewModel.isEditIndex?.let { it1 ->
                    treeAssessmentFormMainViewModel.updateCountValue(
                        species,
                        it1
                    )
                }
            } else {
                treeAssessmentFormMainViewModel.addItemToList(species)
            }
            listAdapter.notifyDataSetChanged()

            binding.serialNumberEditText.editText?.setText("")

            binding.speciesEditText.editText?.setText("")
            binding.serialNumberEditText.editText?.setText("")
            binding.serialNumberEditText.editText?.setText("")
            binding.girthEditText.editText?.setText("")
            binding.heightEditText.editText?.setText("")
            binding.diameterEditText.editText?.setText("")
            binding.commentsEditText.editText?.setText("")

            binding.uploadImage.text = resources.getString(R.string.capture_upload_image)
            treeAssessmentFormMainViewModel.imageUrl = ""
            treeAssessmentFormMainViewModel.isEditIndex = null
            treeAssessmentFormMainViewModel.isEdit = false
        }

        binding.saveBtn.setOnClickListener {
            L.d { "list--${treeAssessmentFormMainViewModel.getSpeciesList()}" }
            if (treeAssessmentFormMainViewModel.speciesList.value.isNullOrEmpty()) {
                Toast.makeText(this, "Please add Species", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            setupPointDataPayload()
        }

        //set pre filled values
        binding.uploadImage.setOnClickListener {
             openFilePicker()
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

//        binding.speciesNameAutoCompleteTextView.setAdapter(
//            ArrayAdapter(this, R.layout.dropdown_item, resources.getStringArray(
//                R.array.season_names))
//        )


    }

    private fun setupPointDataPayload() {
//        treeAssessmentFormMainViewModel.treeSpecies.bio_diversity_survey_points_id =
//            treeAssessmentFormMainViewModel.pointBio.dbId
//        treeAssessmentFormMainViewModel.pointBioDetails.species = addPointFormBioViewModel.getSpeciesList()
        L.d { "data--${treeAssessmentFormMainViewModel.treePoint}" }
        treeAssessmentFormMainViewModel.savePointData(this)
    }
    private fun openFilePicker(){
        imagePicker.open(PickerType.CAMERA)

        // val pickerOptionBottomSheet = SSPickerOptionsBottomSheet.newInstance()
        // pickerOptionBottomSheet.show(supportFragmentManager,"tag")

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
    override fun onImagePick(uri: Uri?) {
        if(uri != null){
            Log.d("TAG_X_FILE", "File path: ${ uri }")
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
}
