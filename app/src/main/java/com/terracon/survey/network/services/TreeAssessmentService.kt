package com.terracon.survey.network.services

import com.terracon.survey.model.FileUploadResponseDTO
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentResponse
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.model.TreeAssessmentSpeciesResponse
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Url

interface TreeAssessmentService {

    @POST("/api/tree-save-point-detail")
    suspend fun saveTreePointData( @HeaderMap headers: Map<String, String>, @Body body: TreeAssessmentPoint) : Response<TreeAssessmentResponse>

    @POST("/api/tree-save-species-detail")
    suspend fun saveTreeSpeciesDetails( @HeaderMap headers: Map<String, String>, @Body body: TreeAssessmentSpecies) : Response<TreeAssessmentSpeciesResponse>

    @POST
    @Multipart
    suspend fun fileUpload(
        @HeaderMap headers: Map<String, String>,
        @Url url : String,
        @Part image: MultipartBody.Part,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>
    ): Response<FileUploadResponseDTO>
}