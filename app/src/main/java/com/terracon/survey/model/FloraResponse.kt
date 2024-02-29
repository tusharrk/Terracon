package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//@Entity
//data class FloraFaunaCategoryResponse(
//    val faunaCategoryList: List<String>,
//    val floraCategoryList: List<String>
//): Serializable{}

data class FloraFaunaCategoryResponse(
    var faunaCategoryList: List<String>,
    var floraCategoryList: List<String>
): Serializable{}

data class FloraMasterResponse(
    val data: FloraData,
    val message: String,
    val status: String
) : Serializable{}

data class FloraData(
    val data: List<Flora> = listOf()
)

@Entity
data class Flora(
    @PrimaryKey
    val id: Int? = null,
//    val aesthetic: String? = "",
//    val authority: String? = "",
//    val canopy_shape: String? = "",
//    val ecological: String? = "",
//    val economic: String? = "",
//    val english_common_name: String? = "",
//    val family: String? = "",
//    val flower_colour: String? = "",
//    val flowering_season: String? = "",
//    val fruting_season: String? = "",
//    val gisd_status: String? = "",
//    val growth_rate: String? = "",
//    val habit: String? = "",
//    val hindi_local_names: String? = "",
//    val invasiveness_shown_in_india: String? = "",
//    val iucn_status: String? = "",
//    val marathi_local_names: String? = "",
//    val medicinal: String? = "",
//    val natural_survival_capicity: String? = "",
//    val nature_of_tree: String? = "",
//    val old_names: String? = "",
//    val orgin: String? = "",
//    val pollution_tolerance: String? = "",
//    val religious: String? = "",
//    val scientific_names: String? = "",
//    val sn: Int = 0,
//    val social: String? = "",
//    val tree_category: String? = "",
//    val use_of_tree: String? = "",
//    val wood_density: Double? = 0.0,

    val sn: String? = "",
    val scientific_names: String? = "",
    val english_common_name: String? = "",
    val family: String? = "",
    val type: String? = "",
    val sub_type: String? = "",
    val orgin: String? = "",
    val iucn_status: String? = "",
    val gisd_status: String? = "",
    val wood_density: String? = "",
)