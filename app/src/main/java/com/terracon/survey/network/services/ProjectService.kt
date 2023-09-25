package com.terracon.survey.network.services

import com.terracon.survey.model.FaunaMasterResponse
import com.terracon.survey.model.FloraMasterResponse
import com.terracon.survey.model.Project
import com.terracon.survey.model.ProjectResponse
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface ProjectService {
    @GET("/api/get-project-list")
    suspend fun getAllProjects(@HeaderMap headers: Map<String, String>,) : Response<ProjectResponse>

//    @GET("/v1/b7123c3f-0144-420d-9d0e-d541b046cbae")
//    suspend fun getFloraFaunaCategories(@HeaderMap headers: Map<String, String>, @Body body: UserApiRequestDTO) : Response<FloraFaunaCategoryResponse>

    @GET("/api/flora-master-list")
    suspend fun getFloraMasterList(@HeaderMap headers: Map<String, String>) : Response<FloraMasterResponse>

    @GET("/api/fauna-master-list")
    suspend fun getFaunaMasterList(@HeaderMap headers: Map<String, String>) : Response<FaunaMasterResponse>


//    @GET("/3/movie/{movie_id}")
//    suspend fun getMovie(@Path("movie_id") id: Int) : Response<MovieDesc>
}