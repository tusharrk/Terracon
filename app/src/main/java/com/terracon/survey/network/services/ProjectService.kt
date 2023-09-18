package com.terracon.survey.network.services

import com.terracon.survey.model.FloraFaunaCategoryResponse
import com.terracon.survey.model.Project
import retrofit2.Response
import retrofit2.http.GET

interface ProjectService {
    @GET("/v1/10cb39a1-f3c3-42fb-9278-16c1baa31a5f")
    suspend fun getAllProjects() : Response<List<Project>>

    @GET("/v1/b7123c3f-0144-420d-9d0e-d541b046cbae")
    suspend fun getFloraFaunaCategories() : Response<FloraFaunaCategoryResponse>

//    @GET("/3/movie/{movie_id}")
//    suspend fun getMovie(@Path("movie_id") id: Int) : Response<MovieDesc>
}