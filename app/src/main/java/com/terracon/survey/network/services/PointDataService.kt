package com.terracon.survey.network.services

import com.terracon.survey.model.FloraFaunaCategoryResponse
import com.terracon.survey.model.Project
import retrofit2.Response
import retrofit2.http.GET

interface PointDataService {
    @GET("/v1/10cb39a1-f3c3-42fb-9278-16c1baa31a5f")
    suspend fun submitPointDetails() : Response<List<Project>>

}