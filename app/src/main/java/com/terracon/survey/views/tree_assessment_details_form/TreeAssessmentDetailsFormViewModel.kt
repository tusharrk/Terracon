package com.terracon.survey.views.tree_assessment_details_form

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terracon.survey.R
import com.terracon.survey.data.ProjectRepository
import com.terracon.survey.data.TreeAssessmentRepository
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.Species
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.model.TreeAssessmentSpeciesData
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioActivity
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity
import com.terracon.survey.views.tree_assessment_details_form.TreeAssessmentDetailsFormActivity
import kotlinx.coroutines.launch
import java.io.Serializable


class TreeAssessmentDetailsFormViewModel(
    private val treeAssessmentRepository: TreeAssessmentRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {


    private val _speciesList = MutableLiveData<ArrayList<TreeAssessmentSpecies>?>()
    val speciesList: MutableLiveData<ArrayList<TreeAssessmentSpecies>?> = _speciesList

    private val _floraFaunaSpeciesList = MutableLiveData<List<String>?>()
    val floraFaunaSpeciesList: MutableLiveData<List<String>?> = _floraFaunaSpeciesList


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<ErrorState?>()
    val errorState: LiveData<ErrorState?> = _errorState

    private val _isLoadingFullScreen = MutableLiveData<Boolean>()
    val isLoadingFullScreen: LiveData<Boolean> = _isLoadingFullScreen

    lateinit var project: Project
    lateinit var treePoint: TreeAssessmentPoint
    var treeSpecies: TreeAssessmentSpecies = TreeAssessmentSpecies()


    var isEdit = false
    var isEditIndex: Int? = null
    var imageUrl: String = ""

    fun navigateToFloraFaunaActivity(
        activity: TreeAssessmentDetailsFormActivity,
        project: Project
    ) {
        val intent = Intent(activity, FloraFaunaActivity::class.java)
        intent.putExtra("projectData", project as Serializable)

        activity.startActivity(intent)
    }

    fun getSpeciesNamesList() {
        _isLoadingFullScreen.value = true
        _errorState.value = null
        viewModelScope.launch {
            projectRepository.getFloraFaunaSpeciesListBasedOnType("Flora", "Tree")
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoadingFullScreen.value = false
                            _errorState.value = null
                            if (it.data != null) {
                                _floraFaunaSpeciesList.value = it.data
                                getPointDetailsData()
                            } else {
                                _errorState.value = ErrorState.NoData
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoadingFullScreen.value = true
                            _errorState.value = null
                        }

                        Result.Status.ERROR -> {
                            _isLoadingFullScreen.value = false
                            _errorState.value = ErrorState.ServerError
                        }

                        else -> _errorState.value = ErrorState.NoData
                    }
                }
        }
    }

    private fun getPointDetailsData() {
        _isLoadingFullScreen.value = true
        viewModelScope.launch {
            treeAssessmentRepository.getPointDetailsFromLocal(treePoint)
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoadingFullScreen.value = false
                            _errorState.value = null
                            if (it.data?.status == "success") {
                                Log.d("TAG_X", it.data.toString())
                                if (it.data.data.tree_assessment_survey_species != null) {
                                    _speciesList.value = ArrayList(it.data.data.list)
                                }
                            } else {
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoadingFullScreen.value = true
                        }

                        Result.Status.ERROR -> {
                            _isLoadingFullScreen.value = false
                        }

                        else -> {
                            // _errorState.value = ErrorState.NoData
                        }
                    }
                }
        }
    }

    fun addItemToList(treeSpecies: TreeAssessmentSpecies){
        treePoint.dbId?.let {
            treeSpecies.tempId = it

            if (_speciesList.value.isNullOrEmpty()){
                val tempList = arrayListOf<TreeAssessmentSpecies>(treeSpecies)
                _speciesList.value = tempList
            }else{
                val tempList = _speciesList.value
                tempList?.add(treeSpecies)
                _speciesList.value = tempList
            }
        }

    }

    fun updateCountValue(species: TreeAssessmentSpecies, index: Int) {
        _speciesList.value?.get(index)?.girth = species.girth
        _speciesList.value?.get(index)?.name = species.name
        _speciesList.value?.get(index)?.comment = species.comment
        _speciesList.value?.get(index)?.images = species.images
    }

    fun deleteItemFromList(index: Int,item:TreeAssessmentSpecies) {
        val tempList = _speciesList.value
        tempList?.removeAt(index)
        _speciesList.value = tempList
    }

    fun getSpeciesList():ArrayList<TreeAssessmentSpecies>{
        val list: ArrayList<TreeAssessmentSpecies> = arrayListOf<TreeAssessmentSpecies>()
        if(_speciesList.value!=null){
            list.addAll(_speciesList.value!!)
        }
        return list
    }

    fun savePointData(activity: TreeAssessmentDetailsFormActivity){
        _isLoading.value = true
        viewModelScope.launch {
            speciesList.value?.let {
                treeAssessmentRepository.savePointSpeciesInLocalDB(treePoint, it)
                    .collect {
                        when (it?.status) {
                            Result.Status.SUCCESS -> {
                                _isLoading.value = false
                                if (it.data != null) {
                                    Log.d("TAG_X", it.data.toString())
                                    activity.runOnUiThread {
                                        //val msg = it.data.message
                                        Toast.makeText(
                                            activity,
                                            "success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        activity.finish()
                                    }
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


}