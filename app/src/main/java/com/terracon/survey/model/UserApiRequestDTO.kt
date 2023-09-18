package com.terracon.survey.model

data class UserApiRequestDTO(
    val receiverId: String = "",
    val userId: String = "",
    val usersId: String = "",
    val messageId: String = "",
    val senderId: String = ""
)