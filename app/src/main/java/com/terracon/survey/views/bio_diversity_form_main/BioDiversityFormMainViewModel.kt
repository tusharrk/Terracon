package com.terracon.survey.views.bio_diversity_form_main

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.Project
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity
import com.terracon.survey.views.otp_verify.OtpVerifyActivity
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BioDiversityFormMainViewModel(
    private val usersRepository: UserRepository,
) : ViewModel() {




fun navigateToFloraFaunaActivity(activity: BioDiversityFormMainActivity, project: Project){
    val intent = Intent(activity, FloraFaunaActivity::class.java)
    intent.putExtra("projectData", project as Serializable)
    activity.startActivity(intent)
}



}