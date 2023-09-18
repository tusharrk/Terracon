package com.terracon.survey.model

import androidx.room.Entity
import java.io.Serializable

@Entity
data class FloraFaunaCategoryResponse(
    val faunaCategoryList: List<String>,
    val floraCategoryList: List<String>
): Serializable{}