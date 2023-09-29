package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TreeAssessmentPoint(
    @PrimaryKey(autoGenerate = true)
    var dbId:Int? = null,
    var code: String = "",
    var date: String = "",
    var gps_latitude: String = "",
    var gps_longitude: String = "",
    var habitat: String = "",
    var id: Int? = null,
    var length: String = "",
    var plot_area: String = "",
    var plot_dimension_type: String = "",
    var plot_type: String = "",
    var project_id: Int = 0,
    var radius: String = "",
    var season_name: String = "",
    var time: String = "",
    var village: String = "",
    var weather_condition: String = "",
    var width: String = "",
    var landmark: String? = null,
    var isSynced: Boolean = false
):Serializable{}

data class TreeAssessmentResponse(
    val data: TreeAssessmentPointData,
    val message: String,
    val status: String
) : Serializable {}

data class TreeAssessmentPointData(
    val list: List<TreeAssessmentPoint> = listOf(),
    val tree_assessment_survey_points: TreeAssessmentPoint
)