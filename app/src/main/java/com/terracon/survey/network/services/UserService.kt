package com.terracon.survey.network.services

import com.terracon.survey.model.User
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("/v1/10cb39a1-f3c3-42fb-9278-16c1baa31a5f")
    suspend fun getAllUsersByUserId() : Response<List<User>>

//    @GET("/3/movie/{movie_id}")
//    suspend fun getMovie(@Path("movie_id") id: Int) : Response<MovieDesc>
}