package com.terracon.survey.data.remote

import com.terracon.survey.model.FileUploadResponseDTO
import com.terracon.survey.model.Result
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentResponse
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.model.TreeAssessmentSpeciesResponse
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.model.UserResponse
import com.terracon.survey.network.services.TreeAssessmentService
import com.terracon.survey.network.services.UserService
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.Config
import com.terracon.survey.utils.ErrorUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit

class TreeAssessmentRemoteDataSource (private val treeAssessmentService: TreeAssessmentService, private val retrofit: Retrofit) {

    suspend fun saveTreePointData(payload: TreeAssessmentPoint): Result<TreeAssessmentResponse> {
        return getResponse(
            request = { treeAssessmentService.saveTreePointData(AppUtils.getApiHeaderMap(),payload) },
            defaultErrorMessage = "Error fetching data")
    }

    suspend fun saveTreeSpeciesDetails(payload: TreeAssessmentSpecies): Result<TreeAssessmentSpeciesResponse> {
        return getResponse(
            request = { treeAssessmentService.saveTreeSpeciesDetails(AppUtils.getApiHeaderMap(),payload) },
            defaultErrorMessage = "Error fetching data")
    }

    suspend fun fileUpload(data: HashMap<String, RequestBody>, image: MultipartBody.Part): Result<FileUploadResponseDTO> {
        return getResponse(
            request = { treeAssessmentService.fileUpload(AppUtils.getApiHeaderMap(), url = Config.FILE_UPLOAD_URL, data =  data, image = image) },
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