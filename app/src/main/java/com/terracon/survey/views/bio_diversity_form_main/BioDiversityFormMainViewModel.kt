package com.terracon.survey.views.bio_diversity_form_main

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terracon.survey.R
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointData
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioActivity
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity
import com.terracon.survey.views.otp_verify.OtpVerifyActivity
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BioDiversityFormMainViewModel(
    private val pointDataRepository: PointDataRepository,
) : ViewModel() {

var bioPoint: BioPoint = BioPoint()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var isEdit:Boolean = false

    var editBioPointData:BioPoint = BioPoint()

private fun navigateToFloraFaunaActivity(activity: BioDiversityFormMainActivity, project: Project){
    val intent = Intent(activity, FloraFaunaActivity::class.java)
    intent.putExtra("projectData", project as Serializable)
    intent.putExtra("pointData", bioPoint as Serializable)
    activity.startActivity(intent)
    activity.finish()
}

    fun savePointData(activity: BioDiversityFormMainActivity,project: Project){
            _isLoading.value = true
            viewModelScope.launch {
                pointDataRepository.saveBioPointInLocalDB(bioPoint)
                    .collect {
                        when (it?.status) {
                            Result.Status.SUCCESS -> {
                                _isLoading.value = false
                                if (it.data != null) {
                                   // var data = it.data as BioPoint
                                    Log.d("TAG_X", it.data.toString())
                                    activity.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            "Data Saved Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    bioPoint.id = it.data.toInt()
                                    bioPoint.dbId = it.data.toInt()
                                    navigateToFloraFaunaActivity(activity,project)
                                } else {
                                    activity.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            "error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    //_errorState.value = ErrorState.NoData
                                }
                            }

                            Result.Status.LOADING -> {
                                _isLoading.value = true
                            }

                            Result.Status.ERROR -> {
                                activity.runOnUiThread {
                                    val errorMsg =
                                        if (it.error?.message != null) it.error.message else activity.resources.getString(
                                            R.string.server_error_desc
                                        )
                                    Toast.makeText(
                                        activity,
                                        errorMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                                _isLoading.value = false
                            }

                            else -> {
                                // _errorState.value = ErrorState.NoData
                            }
                        }
                    }
            }
    }



}