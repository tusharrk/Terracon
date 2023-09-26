package com.terracon.survey.utils

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.michaelflisar.lumberjack.L
import com.terracon.survey.R
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Result
import com.terracon.survey.model.Species
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

    suspend fun uploadImagesAndUpdateDB(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        speciesList: MutableList<Species>
    ) {
        try {
            var index = speciesList.indexOfFirst { species ->
                (species.images != "" && species.images?.contains("https") == false)
            }
            if (index != -1) {
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
                        pointDataRepository.fileUpload(requestBody, imagePart)
                            .collect {
                                when (it?.status) {
                                    Result.Status.SUCCESS -> {
                                        if (it.data?.status == "success") {
                                            Log.d("TAG_X", it.data.toString())
                                            activity.runOnUiThread {
                                                val msg = it.data.message
                                                showToast(activity, msg)
                                            }
                                             var speciesListNew: MutableList<Species> = speciesList
                                            speciesListNew.removeAt(index)
                                            uploadImagesAndUpdateDB(
                                                viewModelScope,
                                                pointDataRepository,
                                                activity,
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

                                        val errorMsg =
                                            if (it.error != null) it.error.message else activity.resources.getString(
                                                R.string.server_error_desc
                                            )
                                        showToast(activity, errorMsg)
                                    }

                                    else -> {
                                        // _errorState.value = ErrorState.NoData
                                    }
                                }
                            }
                    }
                }
            } else {
                showToast(activity,"Images complete")
            }
        } catch (e: Exception) {
            L.d { e.toString() }
            showToast(activity, e.toString())
        }
    }

    fun savePointDataToServerFromDB(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint
    ) {

        viewModelScope.launch {
            pointDataRepository.submitPoint(bioPoint, isUpdateLocal = true)
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            if (it.data != null) {
                                Log.d("TAG_X", it.data.toString())

                                //showToast(activity, "Data Saved Successfully")
                            } else {
                                showToast(activity, "error")
                            }
                        }

                        Result.Status.LOADING -> {}
                        Result.Status.ERROR -> {
                            val errorMsg =
                                if (it.error != null) it.error.message else activity.resources.getString(
                                    R.string.server_error_desc
                                )
                            showToast(activity, errorMsg)
                        }

                        else -> {}
                    }
                }
        }
    }

    fun getImageListToUpload(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint
    ) = runBlocking {
        viewModelScope.launch {
            pointDataRepository.getImageListToUpload(PointDTO(bio_diversity_survey_points_id = bioPoint.id.toString()))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            if (it.data != null) {
                                Log.d("TAG_X", it.data.toString())
                                if (it.data != null) {
                                    withContext(Dispatchers.Default) {
                                        uploadImagesAndUpdateDB(
                                            viewModelScope,
                                            pointDataRepository,
                                            activity,
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
                                showToast(activity, "error")
                            }
                        }

                        Result.Status.LOADING -> {}
                        Result.Status.ERROR -> {
                            val errorMsg =
                                if (it.error != null) it.error.message else activity.resources.getString(
                                    R.string.server_error_desc
                                )
                            showToast(activity, errorMsg)
                        }

                        else -> {}
                    }
                }
        }
    }

    fun getBioPointList(
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
                                if (it.error != null) it.error.message else activity.resources.getString(
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
            activity.runOnUiThread {
                Toast.makeText(
                    activity,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            L.d { "error---${e.toString()}" }
        }

    }


}