package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Project(
    @PrimaryKey
    val id: Int = 0,
    val client_name: String = "",
    val created_at: String = "",
    val created_by: Int = 0,
    val description:String = "",
    val location_of_survey: String = "",
    val map_kml: String = "",
    val name: String = "",
    val total_study_area_in_sq_km: String = "",
    val type: String = "",
    val updated_at: String = "",
    val updated_by: Int = 0
): Serializable{

}

data class ProjectResponse(
    val data: ProjectData,
    val message: String,
    val status: String
)

data class ProjectData(
    val project: List<Project>
)