package com.terracon.survey.data.remote

import com.terracon.survey.model.Result
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.model.UserResponse
import com.terracon.survey.network.services.UserService
import com.terracon.survey.utils.ErrorUtils
import retrofit2.Response
import retrofit2.Retrofit

class UserRemoteDataSource (private val userService: UserService, private val retrofit: Retrofit) {

    suspend fun sendOTP(payload: UserApiRequestDTO): Result<UserResponse> {
        return getResponse(
            request = { userService.sendOTP(payload) },
            defaultErrorMessage = "Error fetching data")
    }

    suspend fun registerUser(payload: UserApiRequestDTO): Result<UserResponse> {
        return getResponse(
            request = { userService.registerUser(payload) },
            defaultErrorMessage = "Error fetching data")
    }
    suspend fun loginUser(payload: UserApiRequestDTO): Result<UserResponse> {
        return getResponse(
            request = { userService.loginUser(payload) },
            defaultErrorMessage = "Error fetching data")
    }
    suspend fun getUserDetails(payload: Map<String, String>): Result<UserResponse> {
        return getResponse(
            request = { userService.getUserDetails(payload) },
            defaultErrorMessage = "Error fetching data")
    }
    suspend fun getAllUsersByUserId(payload: UserApiRequestDTO): Result<List<User>> {
        return getResponse(
            request = { userService.getAllUsersByUserId() },
            defaultErrorMessage = "Error fetching list")
    }



    private suspend fun <T> getResponse(request: suspend () -> Response<T>, defaultErrorMessage: String): Result<T> {
        return try {
            println("I'm working in thread ${Thread.currentThread().name}")
            val result = request.invoke()
            if (result.isSuccessful) {
                return Result.success(result.body())
            } else {
                val errorResponse = ErrorUtils.parseError(result, retrofit)
                Result.error(errorResponse?.message ?: defaultErrorMessage, errorResponse)
            }
        } catch (e: Throwable) {
            Result.error(e.toString(), null)
        }
    }
}