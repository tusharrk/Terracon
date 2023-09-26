package com.terracon.survey.data.remote

import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.BioPointDetailsResponse
import com.terracon.survey.model.BioPointResponse
import com.terracon.survey.model.FileUploadResponseDTO
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.UserApiRequestDTO
import com.terracon.survey.network.services.PointDataService
import com.terracon.survey.network.services.ProjectService
import com.terracon.survey.utils.AppUtils
import com.terracon.survey.utils.Config
import com.terracon.survey.utils.ErrorUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit

class PointDataRemoteDataSource (private val pointDataService: PointDataService, private val retrofit: Retrofit) {

    suspend fun submitPointDetails(payload: BioPointDetails): Result<BioPointDetailsResponse> {
        return getResponse(
            request = { pointDataService.submitPointDetails(AppUtils.getApiHeaderMap(),payload) },
            defaultErrorMessage = "Error fetching list")
    }
    suspend fun submitPoint(payload: BioPoint): Result<BioPointResponse> {
        return getResponse(
            request = { pointDataService.submitPoint(AppUtils.getApiHeaderMap(),payload) },
            defaultErrorMessage = "Error fetching list")
    }

    suspend fun getPointList(projectId: String): Result<BioPointResponse> {
        return getResponse(
            request = { pointDataService.getPointList(AppUtils.getApiHeaderMap(),projectId) },
            defaultErrorMessage = "Error fetching list")
    }

    suspend fun getPointDetails(payload: PointDTO): Result<BioPointDetailsResponse> {
        return getResponse(
            request = { pointDataService.getPointDetails(AppUtils.getApiHeaderMap(),payload.bio_diversity_survey_points_id,payload.type,payload.sub_type) },
            defaultErrorMessage = "Error fetching list")
    }

    suspend fun fileUpload(data: HashMap<String, RequestBody>,image: MultipartBody.Part): Result<FileUploadResponseDTO> {
        return getResponse(
            request = { pointDataService.fileUpload(AppUtils.getApiHeaderMap(), url = Config.FILE_UPLOAD_URL, data =  data, image = image) },
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