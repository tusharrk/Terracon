package com.terracon.survey.network.services

import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.BioPointDetailsResponse
import com.terracon.survey.model.BioPointResponse
import com.terracon.survey.model.Project
import com.terracon.survey.model.UserApiRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PointDataService {
    @POST("/api/bio-save-data-point-detail")
    suspend fun submitPointDetails(@HeaderMap headers: Map<String, String>, @Body pointBio: BioPointDetails) : Response<BioPointDetailsResponse>

    @POST("/api/bio-save-point-detail")
    suspend fun submitPoint(@HeaderMap headers: Map<String, String>, @Body pointBio: BioPoint) : Response<BioPointResponse>


    @GET("/api/bio-point-list")
    suspend fun getPointList(@HeaderMap headers: Map<String, String>, @Query("project_id") projectId:String) : Response<BioPointResponse>

}