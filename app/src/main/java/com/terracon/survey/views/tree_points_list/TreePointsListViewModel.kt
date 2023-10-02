package com.terracon.survey.views.tree_points_list

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.terracon.survey.R
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.data.TreeAssessmentRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.PointDataUtils
import com.terracon.survey.utils.TreePointDataUtils
import com.terracon.survey.views.points_list.PointsListActivity
import kotlinx.coroutines.launch


class TreePointsListViewModel(
    private val treeAssessmentRepository: TreeAssessmentRepository,
) : ViewModel() {

    private val _pointsList = MutableLiveData<List<TreeAssessmentPoint>?>()
    val pointsList: MutableLiveData<List<TreeAssessmentPoint>?> = _pointsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<ErrorState?>()
    val errorState: LiveData<ErrorState?> = _errorState


    private var gson = GsonBuilder().setLenient().serializeNulls().create()


    lateinit var project:Project

    init {
       // fetchPoints("asd")

    }

    fun syncDataFromLocalToServer(activity: TreePointsListActivity, treePoint: TreeAssessmentPoint){
      ///  PointDataUtils.savePointDataToServerFromDB(viewModelScope,pointRepository,activity,bioPoint)
        if(AppUtils.isNetworkAvailable(activity)){
            MaterialAlertDialogBuilder(activity)
                .setTitle(activity.getString(R.string.confirmation))
                .setMessage(activity.getString(R.string.sync_data_msg))
                .setNeutralButton("Cancel") { dialog, which ->

                }
                .setPositiveButton("Sync") { dialog, which ->
                    Toast.makeText(activity,"Syncing Data with server, Please wait", Toast.LENGTH_SHORT).show()
                    TreePointDataUtils.savePointDataToServerFromDB(viewModelScope,treeAssessmentRepository,activity,treePoint)
                }
                .show()
        }else{
            Toast.makeText(activity,"Please make sure you are connected to Internet.",Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchPoints() {
        _isLoading.value = true
        _errorState.value = null
        viewModelScope.launch {
            treeAssessmentRepository.getPointList(project.id.toString())
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            _errorState.value = null
                            if (it.data?.status == "success" && it.data.data.list.isNotEmpty()) {
                                Log.d("TAG_X", it.data.toString())
                                _pointsList.value =
                                    it.data.data.list
                            } else {
                                _errorState.value = ErrorState.NoData
                                //_errorState.value = ErrorState.NoData
                            }
                        }
                        Result.Status.LOADING -> {
                            _isLoading.value = true
                            _errorState.value = null
                        }

                        Result.Status.ERROR -> {
                            _isLoading.value = false
                            _errorState.value = ErrorState.ServerError
                        }

                        else -> _errorState.value = ErrorState.NoData
                    }
                }


        }
    }


}
