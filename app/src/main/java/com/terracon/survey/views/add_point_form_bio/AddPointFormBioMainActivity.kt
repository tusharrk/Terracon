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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.ImageProvider
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.bottomsheet.SSPickerOptionsBottomSheet
import com.terracon.survey.R
import com.terracon.survey.databinding.AddPointFormBioActivityBinding
import com.terracon.survey.model.Project
import com.terracon.survey.utils.AppUtils
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
                addPointFormBioViewModel.updateCountValue(item, index)
                listAdapter.notifyItemChanged(index)
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
                this,
                DividerItemDecoration.VERTICAL
            )
        )

    }

    private fun setupObservers() {
        addPointFormBioViewModel.speciesBioList.observe(this) { data ->
            listAdapter.submitList(data)

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUi() {

        addPointFormBioViewModel.project = intent.getSerializableExtra("projectData") as Project

        setSupportActionBar(binding.mainToolbar.root)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
             supportActionBar?.title = addPointFormBioViewModel.project .projectName
        }


        binding.addBlankItemBtn.setOnClickListener {
            addPointFormBioViewModel.insertBlankItemInSpeciesList()
            listAdapter.notifyDataSetChanged()
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
    }

    fun openFilePicker(){
        imagePicker.open(PickerType.CAMERA)

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
        if(uri != null){
            Log.d("TAG_X_FILE", "File path: ${ uri }")
            binding.uploadImage.text = uri.toString()
          //  binding.uploadImage.text = RealPathUtil.getRealPath(this,uri)
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
