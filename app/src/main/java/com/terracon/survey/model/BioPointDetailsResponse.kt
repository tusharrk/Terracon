package com.terracon.survey.model

data class BioPointDetails(
    var bio_diversity_survey_points_id: Int? = null,
    var id: Int? = null,
    var species: List<Species> = listOf(),
    var sub_type: String = "",
    var type: String = ""
)


data class BioPointDetailsResponse(
    val data: BioPointDetailsData,
    val message: String,
    val status: String
)

data class BioPointDetailsData(
    val bio_diversity_survey_points: BioPointDetails
)

data class Species(
    var bio_diversity_survey_data_points_id: Int? = null,
    var comment: String = "",
    var count: String = "",
    var id: Int? = null,
    var images: String = "",
    var name: String = ""
)
