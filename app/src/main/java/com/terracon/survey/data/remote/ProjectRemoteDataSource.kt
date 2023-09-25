package com.terracon.survey.data.remote

import com.terracon.survey.model.FaunaMasterResponse
import com.terracon.survey.model.FloraMasterResponse
import com.terracon.survey.model.Project
import com.terracon.survey.model.ProjectResponse
import com.terracon.survey.model.Result
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.network.services.ProjectService
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.ErrorUtils
import retrofit2.Response
import retrofit2.Retrofit

class ProjectRemoteDataSource (private val projectService: ProjectService, private val retrofit: Retrofit) {

    suspend fun getAllProjects(payload: UserApiRequestDTO): Result<ProjectResponse> {
        return getResponse(
            request = { projectService.getAllProjects(AppUtils.getApiHeaderMap()) },
            defaultErrorMessage = "Error fetching list")

    }

    suspend fun getFloraData(payload: UserApiRequestDTO): Result<FloraMasterResponse> {
        return getResponse(
            request = { projectService.getFloraMasterList(AppUtils.getApiHeaderMap()) },
            defaultErrorMessage = "Error fetching list")
    }

    suspend fun getFaunaData(payload: UserApiRequestDTO): Result<FaunaMasterResponse> {
        return getResponse(
            request = { projectService.getFaunaMasterList(AppUtils.getApiHeaderMap()) },
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