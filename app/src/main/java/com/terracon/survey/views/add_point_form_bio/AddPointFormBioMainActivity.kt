package com.terracon.survey.views.add_point_form_bio

import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.ImageProvider
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.bottomsheet.SSPickerOptionsBottomSheet
import com.bumptech.glide.Glide
import com.michaelflisar.lumberjack.L
import com.terracon.survey.R
import com.terracon.survey.databinding.AddPointFormBioActivityBinding
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Project
import com.terracon.survey.model.Species
import com.terracon.survey.utils.AppUtils
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
    }

    private fun setupListAdapter() {
        listAdapter = AddPointFormBioAdapter(AddPointFormBioAdapter.OnClickListener { item ->
            val index = addPointFormBioViewModel.speciesBioList.value?.indexOf(item)
            if (index != null) {
                addPointFormBioViewModel.isEdit = true
                addPointFormBioViewModel.isEditIndex = index

                binding.speciesNameEditText.editText?.setText(item.name)
                binding.countEditText.editText?.setText(item.count)
                binding.commentEditText.editText?.setText(item.comment)
                binding.uploadImage.text =  if (item.images.isNullOrEmpty()) getString(R.string.capture_upload_image) else item.images!!

                addPointFormBioViewModel.imageUrl =
                    if (item.images.isNullOrEmpty()) "" else item.images!!

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
        addPointFormBioViewModel.floraFaunaSpeciesList.observe(this) { data ->
            if (!data.isNullOrEmpty()) {
                binding.speciesNameAutoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        this, R.layout.dropdown_item, data
                    )
                )
            }

        }
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
                        addPointFormBioViewModel.getSpeciesList()
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

        binding.addItemBtn.setOnClickListener {
            if (binding.speciesNameEditText.editText?.text.isNullOrBlank()) {
                Toast.makeText(this, "Please select Species", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            var species = Species(
                name = binding.speciesNameEditText.editText?.text.toString(),
                count = binding.countEditText.editText?.text.toString(),
                images = addPointFormBioViewModel.imageUrl,
                comment = binding.commentEditText.editText?.text.toString()

            )
            if (addPointFormBioViewModel.isEdit) {
                addPointFormBioViewModel.isEditIndex?.let { it1 ->
                    addPointFormBioViewModel.updateCountValue(
                        species,
                        it1
                    )
                }
            } else {
                addPointFormBioViewModel.addItemToList(species)
            }
            listAdapter.notifyDataSetChanged()

            binding.speciesNameEditText.editText?.setText("")
            binding.countEditText.editText?.setText("0")
            binding.commentEditText.editText?.setText("")
            binding.uploadImage.text = resources.getString(R.string.capture_upload_image)
            addPointFormBioViewModel.imageUrl = ""
            addPointFormBioViewModel.isEditIndex = null
            addPointFormBioViewModel.isEdit = false
        }
        binding.incrementCount.setOnClickListener {
            try {
                var count = Integer.parseInt(binding.countEditText.editText?.text.toString())
                count += 1
                binding.countEditText.editText?.setText(count.toString())
            } catch (e: Exception) {
                Toast.makeText(this, "Error:- $e", Toast.LENGTH_LONG).show()
            }
        }
        binding.decrementCount.setOnClickListener {
            try {
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



        binding.saveBtn.setOnClickListener {
            L.d { "list--${addPointFormBioViewModel.getSpeciesList()}" }
            if (addPointFormBioViewModel.speciesBioList.value.isNullOrEmpty()) {
                Toast.makeText(this, "Please add Species", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            setupPointDataPayload()

        }
    }

    private fun setupPointDataPayload() {
        addPointFormBioViewModel.pointBioDetails.type = addPointFormBioViewModel.type
        addPointFormBioViewModel.pointBioDetails.sub_type = addPointFormBioViewModel.subType
        addPointFormBioViewModel.pointBioDetails.tempId =
            addPointFormBioViewModel.pointBio.dbId
        addPointFormBioViewModel.pointBioDetails.species = addPointFormBioViewModel.getSpeciesList()
        L.d { "data--${addPointFormBioViewModel.pointBio}" }
        addPointFormBioViewModel.savePointData(this)
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
}

//content://com.terracon.survey.com.app.imagepickerlibrary.provider/external_files/Android/data/com.terracon.survey/files/Pictures/JPEG_20230926_170012_5514495750614150298.jpg
//file:///storage/emulated/0/Android/data/com.terracon.survey/files/Pictures/JPEG_20230926_170735_1845284174450346692.jpg