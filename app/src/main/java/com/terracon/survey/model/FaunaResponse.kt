package com.terracon.survey.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


data class FaunaMasterResponse(
    val data: FaunaData,
    val message: String,
    val status: String
) : Serializable {}

data class FaunaData(
    val data: List<Fauna> = listOf()
)
@Entity
data class Fauna(
    @PrimaryKey
    val id: Int? = null,
    val common_name: String = "",
    val family: String = "",
    val group_name: String = "",
    val iucn_status: String = "",
    val scientific_name: String = "",
    val sr_no: Int = 0,
    val taxa: String = "",
    val wpa_status: String = ""
)