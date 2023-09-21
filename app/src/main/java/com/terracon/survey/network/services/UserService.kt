package com.terracon.survey.network.services

import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @GET("/v1/10cb39a1-f3c3-42fb-9278-16c1baa31a5f")
    suspend fun getAllUsersByUserId() : Response<List<User>>

    @POST("/api/send-OTP")
    suspend fun sendOTP(@Body body: UserApiRequestDTO) : Response<UserResponse>

    @POST("/api/register")
    suspend fun registerUser(@Body body: UserApiRequestDTO) : Response<UserResponse>

    @POST("/api/login")
    suspend fun loginUser(@Body body: UserApiRequestDTO) : Response<UserResponse>

}