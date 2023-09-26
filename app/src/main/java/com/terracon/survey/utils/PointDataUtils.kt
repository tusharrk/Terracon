package com.terracon.survey.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.michaelflisar.lumberjack.L
import com.terracon.survey.R
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object PointDataUtils {

    fun savePointDataToServerFromDB(
        viewModelScope: CoroutineScope,
        pointDataRepository: PointDataRepository,
        activity: Activity,
        bioPoint: BioPoint
    ) {

        viewModelScope.launch {
            pointDataRepository.submitPoint(bioPoint,isUpdateLocal = true)
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            if (it.data != null) {
                                Log.d("TAG_X", it.data.toString())
                                showToast(activity, "Data Saved Successfully")
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