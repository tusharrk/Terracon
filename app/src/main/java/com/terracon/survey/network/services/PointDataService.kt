package com.terracon.survey.network.services

import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.BioPointDetailsResponse
import com.terracon.survey.model.BioPointResponse
import com.terracon.survey.model.FileUploadResponseDTO
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Project
import com.terracon.survey.model.UserApiRequestDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PointDataService {
    @POST("/api/bio-save-data-point-detail")
    suspend fun submitPointDetails(
        @HeaderMap headers: Map<String, String>,
        @Body pointBio: BioPointDetails
    ): Response<BioPointDetailsResponse>

    @POST("/api/bio-save-point-detail")
    suspend fun submitPoint(
        @HeaderMap headers: Map<String, String>,
        @Body pointBio: BioPoint
    ): Response<BioPointResponse>


    @GET("/api/bio-point-list")
    suspend fun getPointList(
        @HeaderMap headers: Map<String, String>,
        @Query("project_id") projectId: String
    ): Response<BioPointResponse>


    @GET("/api/bio-get-data-point-detail")
    suspend fun getPointDetails(
        @HeaderMap headers: Map<String, String>,
        @Query("bio_diversity_survey_points_id") bio_diversity_survey_points_id: String,
        @Query("type") type: String,
        @Query("sub_type") subType: String,
        ): Response<BioPointDetailsResponse>

    @POST
    @Multipart
    suspend fun fileUpload(
        @HeaderMap headers: Map<String, String>,
        @Url url : String,
        @Part image: MultipartBody.Part,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<FileUploadResponseDTO>

}