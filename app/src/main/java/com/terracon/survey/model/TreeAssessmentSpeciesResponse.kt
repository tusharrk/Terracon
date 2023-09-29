package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TreeAssessmentSpecies(
    @PrimaryKey(autoGenerate = true)
    var dbId:Int? = null,
    var id:Int? = null,
    var tempId:Int? = null,
    var canopy_diameter: String? = null,
    var comment: String = "",
    var girth: String? = null,
    var gps_latitude: Int = 0,
    var gps_longitude: Int = 0,
    var height: String? = null,
    var images: String = "",
    var name: String = "",
    var serial_number :String = "",
    var tree_assessment_survey_points_id: Int = 0,
    var isSynced: Boolean = false
): Serializable{}


data class TreeAssessmentSpeciesResponse(
    val data: TreeAssessmentSpeciesData,
    val message: String,
    val status: String
) : Serializable {}

data class TreeAssessmentSpeciesData(
    val list: List<TreeAssessmentSpecies> = listOf(),
    val tree_assessment_survey_species: TreeAssessmentSpecies
)