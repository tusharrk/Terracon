package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity
data class BioPoint(
    @PrimaryKey
    var id: Int? = null,
    var code: String = "",
    var created_at: String = "",
    var date: String = "",
    var gps_latitude: String = "",
    var gps_longitude: String = "",
    var habitat: String = "",
    var length: String = "",
    var plot_area: String = "",
    var plot_dimension_type: String = "",
    var plot_type: String = "",
    var project_id: Int = 0,
    var radius: String = "",
    var season_name: String = "",
    var time: String = "",
    var updated_at: String = "",
    var village: String = "",
    var weather_condition: String = "",
    var width: String = ""
):Serializable{}

data class BioPointResponse(
    val data: BioPointData,
    val message: String,
    val status: String
) : Serializable {}

data class BioPointData(
    val list: List<BioPoint> = listOf(),
    val bio_diversity_survey_point_details: BioPoint
)