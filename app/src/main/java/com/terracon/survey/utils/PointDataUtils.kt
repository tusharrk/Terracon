package com.terracon.survey.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaelflisar.lumberjack.core.L
import com.terracon.survey.R
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Result
import com.terracon.survey.model.Species
import com.terracon.survey.views.points_list.PointsListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object PointDataUtils {

    private var imageUploadedCount = 0
    private var pointUploadedCount = 0
    private var speciesUploadedCount = 0
    fun savePointDataToServerFromDB(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint
    ) {
        imageUploadedCount = 0
        pointUploadedCount = 0
        speciesUploadedCount = 0
        L.d { "save point started" }
        viewModelScope.launch {
            pointDataRepository.submitPoint(bioPoint, isUpdateLocal = true)
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            try {
                                if (it.data != null) {
                                    Log.d("TAG_X", it.data.toString())
                                    L.d { "save point success--${it.data.toString()}" }
                                    getImageListToUpload(
                                        viewModelScope,
                                        pointDataRepository,
                                        activity,
                                        it.data.data.bio_diversity_survey_point_details
                                    )
                                    //showToast(activity, "Data Saved Successfully")
                                } else {
                                    showToast(
                                        activity,
                                        "Some data failed to Sync, Please try again"
                                    )

                                }
                            } catch (e: Exception) {
                                showToast(activity, e.toString())
                            }
                        }

                        Result.Status.LOADING -> {}
                        Result.Status.ERROR -> {
//                            val errorMsg =
//                                if (it.error?.message != null) it.error.message else activity.resources.getString(
//                                    R.string.server_error_desc
//                                )
                            showToast(activity, it.error?.message)
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun getImageListToUpload(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint
    ) = runBlocking {
        L.d { "inside getImageListToUpload" }

        viewModelScope.launch {
            pointDataRepository.getImageListToUpload(PointDTO(bio_diversity_survey_points_id = bioPoint.dbId.toString()))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            if (it.data != null) {
                                Log.d("TAG_X", it.data.toString())
                                L.d { " getImageListToUpload success---${it.data.toString()}" }

                                if (it.data != null) {
                                    withContext(Dispatchers.Default) {
                                        uploadImagesAndUpdateDB(
                                            viewModelScope,
                                            pointDataRepository,
                                            activity,
                                            bioPoint,
                                            it.data as MutableList<Species>
                                        )
                                    }
                                }
//                                it.data.forEach { species ->
//                                    if(species.images!="" && species.images?.contains("https") == false){
//                                        L.d {"url-- ${species.images.toString()}" }
//                                        withContext(Dispatchers.Default) {
//                                            uploadImagesAndUpdateDB(
//                                                viewModelScope,
//                                                pointDataRepository,
//                                                activity,
//                                                species
//                                            )
//                                        }
//
//                                    }
//                                }
                                //showToast(activity, "Data Saved Successfully")
                            } else {
                                showToast(activity, "Some data failed to Sync, Please try again")

                            }
                        }

                        Result.Status.LOADING -> {}
                        Result.Status.ERROR -> {
                            val errorMsg =
                                if (it.error?.message != null) it.error.message else activity.resources.getString(
                                    R.string.server_error_desc
                                )
                            showToast(activity, errorMsg)
                        }

                        else -> {}
                    }
                }
        }
    }

    private suspend fun uploadImagesAndUpdateDB(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint,
        speciesList: MutableList<Species>
    ) {
        L.d { "inside uploadImagesAndUpdateDB" }

        try {
            var index = speciesList.indexOfFirst { species ->
                (species.images != "" && species.images?.contains("https") == false && !species.isSynced)
            }
            if (index != -1) {
                L.d { "inside uploadImagesAndUpdateDB index--${index}" }

                var species = speciesList[index]

                val requestBody = HashMap<String, RequestBody>()
                var fileName = "${System.currentTimeMillis()}.jpeg"
                requestBody["fileName"] = fileName.toRequestBody("text/plain".toMediaTypeOrNull())
                val file: File? = FileHelper.getFile(activity, Uri.parse(species.images))

                if (file != null) {
                    L.d { "upload starting--${species.images}" }
                    val fileBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val imagePart: MultipartBody.Part =
                        MultipartBody.Part.createFormData("fileToUpload", file.name, fileBody)

                    viewModelScope.launch {
                        pointDataRepository.fileUpload(requestBody, imagePart, species)
                            .collect {
                                when (it?.status) {
                                    Result.Status.SUCCESS -> {

                                        if (it.data?.status == "success") {
                                            Log.d("TAG_X", it.data.toString())
                                            L.d { " uploadImagesAndUpdateDB success--${it.data.toString()}" }
                                            imageUploadedCount++
//                                            activity.runOnUiThread {
//                                                val msg = it.data.message
//                                                showToast(activity, msg)
//                                            }
                                            var speciesListNew: MutableList<Species> = speciesList
                                            speciesListNew.removeAt(index)
                                            uploadImagesAndUpdateDB(
                                                viewModelScope,
                                                pointDataRepository,
                                                activity,
                                                bioPoint,
                                                speciesListNew
                                            )

                                        } else {
                                            showToast(activity, it.data?.message)

                                        }
                                    }

                                    Result.Status.LOADING -> {
                                        L.d { "upload loading--${species.images}" }

                                    }

                                    Result.Status.ERROR -> {
                                        L.d { "upload error--${it.toString()}" }

                                        showToast(activity, it.error?.message)
                                    }

                                    else -> {
                                        // _errorState.value = ErrorState.NoData
                                    }
                                }
                            }
                    }
                }
            } else {
                L.d { " uploadImagesAndUpdateDB all images complete--" }

                getPointDetailsFromLocal(
                    viewModelScope,
                    pointDataRepository,
                    activity,
                    bioPoint
                )
            }
        } catch (e: Exception) {
            L.d { e.toString() }
            showToast(activity, e.toString())
        }
    }


    private fun getPointDetailsFromLocal(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint
    ) {
        L.d { " inside  getPointDetailsFromLocal" }

        viewModelScope.launch {
            if (bioPoint.dbId != null) {
                pointDataRepository.getAllPointDetailsFromLocal(bioPoint.dbId!!)
                    .collect {
                        when (it.status) {
                            Result.Status.SUCCESS -> {
                                try {
                                    if (it.data != null) {
                                        Log.d("TAG_X", it.data.toString())
                                        L.d { "getPointDetailsFromLocal success--${it.data.toString()}" }

                                        savePointDetailsToServerFromDB(
                                            viewModelScope,
                                            pointDataRepository,
                                            activity,
                                            bioPoint,
                                            it.data
                                        )
                                        //showToast(activity, "Data Saved Successfully")
                                    } else {
                                        showToast(
                                            activity,
                                            "Some data failed to Sync, Please try again"
                                        )

                                    }
                                } catch (e: Exception) {
                                    showToast(activity, e.toString())
                                }

                            }

                            Result.Status.LOADING -> {}
                            Result.Status.ERROR -> {
                                showToast(activity, it.error?.message)
                            }

                            else -> {}
                        }
                    }
            } else {
                showToast(activity, "Error in bio id")
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun savePointDetailsToServerFromDB(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint,
        list: List<BioPointDetails>
    ) {
        L.d { "inside savePointDetailsToServerFromDB size--${list.size}" }

        try {
            var index = list.indexOfFirst { item ->
                (!item.isSynced)
            }
            if (index != -1) {

                L.d { "inside savePointDetailsToServerFromDB index--${index}" }

                var bioDetailsItem = list[index]
                bioDetailsItem.id = null
                viewModelScope.launch {
                    pointDataRepository.submitPointDetails(bioDetailsItem)
                        .collect {
                            when (it?.status) {
                                Result.Status.SUCCESS -> {
                                    try {
                                        if (it.data != null) {
                                            Log.d("TAG_X", it.data.toString())
                                            L.d { " savePointDetailsToServerFromDB success--${it.data.toString()}" }
                                            speciesUploadedCount += bioDetailsItem.species.size
                                            var listNew: MutableList<BioPointDetails> =
                                                list.toMutableList()
                                            listNew.removeAt(index)
                                            savePointDetailsToServerFromDB(
                                                viewModelScope,
                                                pointDataRepository,
                                                activity,
                                                bioPoint,
                                                listNew
                                            )
                                            // showToast(activity, "Data Saved Successfully")
                                        } else {
                                            showToast(
                                                activity,
                                                "Some data failed to Sync, Please try again"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        showToast(activity, e.toString())
                                    }
                                }

                                Result.Status.LOADING -> {}
                                Result.Status.ERROR -> {
                                    showToast(activity, it.error?.message)
                                }

                                else -> {}
                            }
                        }
                }

            } else {
                updateBioPointInLocal(viewModelScope, pointDataRepository, activity, bioPoint)

            }
        } catch (e: Exception) {
            L.d { "error--${e.toString()}" }
            showToast(activity, e.toString())
        }
    }


    private fun updateBioPointInLocal(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint
    ) {
        L.d { "save point started" }
        L.d { "bio--${bioPoint}" }
        bioPoint.isSynced = true
        viewModelScope.launch {
            pointDataRepository.updateBioPointSyncStatusInLocalDB(bioPoint)
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            try {
                                if (it.data != null) {
                                    Log.d("TAG_X", it.data.toString())
                                    L.d { "save point success--${it.data.toString()}" }
                                    pointUploadedCount++
                                    //showToast(activity, "Point Synced With server")
                                    showToast(activity, "Data Synced Successfully " +
                                            "\nImages Synced = $imageUploadedCount " +
                                            "\nSpecies Synced = $speciesUploadedCount " +
                                            "\nPoint Synced = $pointUploadedCount")
                                    (activity as PointsListActivity).refreshList()
                                    //showToast(activity, "Data Saved Successfully")
                                } else {
                                    showToast(
                                        activity,
                                        "Some data failed to Sync, Please try again"
                                    )
                                }
                            } catch (e: Exception) {
                                showToast(activity, e.toString())
                            }
                        }

                        Result.Status.LOADING -> {}
                        Result.Status.ERROR -> {
                            showToast(activity, it.error?.message)
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun getBioPointList(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity
    ) {
        viewModelScope.launch {
            pointDataRepository.getPointList("3")
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            if (it.data != null) {
                                Log.d("TAG_X", it.data.toString())
                                showToast(activity, "Data fetched Successfully")
                            } else {
                                showToast(activity, "error")
                            }
                        }

                        Result.Status.LOADING -> {}
                        Result.Status.ERROR -> {
                            val errorMsg =
                                if (it.error?.message != null) it.error.message else activity.resources.getString(
                                    R.string.server_error_desc
                                )
                            showToast(activity, errorMsg)
                        }

                        else -> {}
                    }
                }
        }

    }


    private fun showToast(activity: Activity, msg: String?) {
        try {
            var message = msg ?: "Some data failed to Sync, Please try again"

            activity.runOnUiThread {
                MaterialAlertDialogBuilder(activity)
                    .setTitle("Status")
                    .setMessage(message)
                    .setPositiveButton("Ok") { dialog, which ->
                    }
                    .show()
            }
        } catch (e: Exception) {
            L.d { "error---${e.toString()}" }
            activity.runOnUiThread {
                Toast.makeText(
                    activity,
                    e.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


}