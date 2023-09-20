package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey
    val id: Int = 0,
    val account_verify_flag: String = "",
    val created_at: String = "",
    val mobile: String = "",
    val name: String = "",
    val status: String = "",
    val token: String = "",
    val updated_at: String = ""
    ) : Serializable {


}

data class UserResponse(
    val data: Data,
    val message: String,
    val status: String
)

data class Data(
    val user: User
)
