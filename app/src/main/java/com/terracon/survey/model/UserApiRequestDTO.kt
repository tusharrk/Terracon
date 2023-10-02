package com.terracon.survey.model

data class UserApiRequestDTO(
    val mobile: String = "",
    val name: String = "",
    val otp: String = "",
    val password:String = "",
    val id: Int = 0,
    var project_id: String? = null
)