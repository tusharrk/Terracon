package com.terracon.survey.views.tree_assessment_form

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terracon.survey.R
import com.terracon.survey.data.TreeAssessmentRepository
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.views.bio_diversity_form_main.BioDiversityFormMainActivity
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity
import com.terracon.survey.views.tree_assessment_details_form.TreeAssessmentDetailsFormActivity
import com.terracon.survey.views.tree_assessment_form.TreeAssessmentFormActivity
import kotlinx.coroutines.launch
import java.io.Serializable


class TreeAssessmentFormViewModel(
    private val treeAssessmentRepository: TreeAssessmentRepository,
) : ViewModel() {

    var treePoint: TreeAssessmentPoint = TreeAssessmentPoint()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading



fun navigateToDetails(activity: TreeAssessmentFormActivity, project: Project){
    val intent = Intent(activity, TreeAssessmentDetailsFormActivity::class.java)
    intent.putExtra("projectData", project as Serializable)
    intent.putExtra("pointData", treePoint as Serializable)
    activity.startActivity(intent)
    activity.finish()

}


    fun savePointData(activity: TreeAssessmentFormActivity, project: Project){
        _isLoading.value = true
        viewModelScope.launch {
            treeAssessmentRepository.savePointInLocalDB(treePoint)
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
                                treePoint.id = it.data.toInt()
                                treePoint.dbId = it.data.toInt()
                                navigateToDetails(activity,project)
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