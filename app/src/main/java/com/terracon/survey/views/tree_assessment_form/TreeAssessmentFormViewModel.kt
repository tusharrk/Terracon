package com.terracon.survey.views.tree_assessment_form

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.Project
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity
import com.terracon.survey.views.tree_assessment_details_form.TreeAssessmentDetailsFormActivity
import com.terracon.survey.views.tree_assessment_form.TreeAssessmentFormActivity
import java.io.Serializable


class TreeAssessmentFormViewModel(
    private val usersRepository: UserRepository,
) : ViewModel() {




fun navigateToDetails(activity: TreeAssessmentFormActivity, project: Project){
    val intent = Intent(activity, TreeAssessmentDetailsFormActivity::class.java)
    intent.putExtra("projectData", project as Serializable)
    activity.startActivity(intent)
}



}