package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey
    var id: Int = 0,
    var name: String = "",
    var role: String = "",
    var status: String = "",
    var createdBy: String = "",
    var lastModifiedBy: String = "",
    var createdAt: String = "",
    var lastModifiedAt: String = "",
    ) : Serializable {


}

