package com.terracon.survey.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class BioPointDetails(
    @PrimaryKey(autoGenerate = true)
    var dbId:Int? = null,
    var bio_diversity_survey_points_id: Int? = null,
    var id: Int? = null,
    @Ignore
    var species: List<Species> = listOf(),
    var sub_type: String = "",
    var type: String = "",
    var isSynced:Boolean = false
)


data class BioPointDetailsResponse(
    val data: BioPointDetailsData,
    val message: String,
    val status: String
)

data class BioPointDetailsData(
    val bio_diversity_survey_data_points: BioPointDetails?
)
@Entity
data class Species(
    @PrimaryKey(autoGenerate = true)
    var dbId:Int? = null,
    var id: Int? = null,
    var bio_diversity_survey_data_points_id: Int? = null,
    var comment: String? = null,
    var count: String = "",
    var images: String? = null,
    var name: String = "",
    var isSynced: Boolean = false
)

data class BioPointDetailsWithSpecies(
    @Embedded val bioPointDetails: BioPointDetails,
    @Relation(
        parentColumn = "dbId",
        entityColumn = "bio_diversity_survey_data_points_id"
    )
    val species: List<Species>
)
