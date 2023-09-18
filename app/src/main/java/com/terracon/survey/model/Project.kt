package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Project(
    @PrimaryKey
    val id: Int,
    val clientName: String,
    val createdAt: String,
    val createdBy: String,
    val lastModifiedAt: String,
    val lastModifiedBy: String,
    val map: String,
    val projectName: String,
    val projectDesc: String,
    val surveyLocation: String,
    val totalStudyArea: String,
    val type: String
): Serializable{

}