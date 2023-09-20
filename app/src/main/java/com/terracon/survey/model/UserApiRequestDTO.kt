package com.terracon.survey.model

data class UserApiRequestDTO(
    val mobile: String = "",
    val name: String = "",
    val otp: String = "",
    val id: Int = 0
)