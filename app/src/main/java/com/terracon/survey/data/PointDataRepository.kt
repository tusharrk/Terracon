package com.terracon.survey.data


import android.graphics.Point
import android.widget.GridLayout.Spec
import com.michaelflisar.lumberjack.L
import com.terracon.survey.data.local.PointDataDao
import com.terracon.survey.data.local.ProjectDao
import com.terracon.survey.data.remote.PointDataRemoteDataSource
import com.terracon.survey.data.remote.ProjectRemoteDataSource
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointData
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.BioPointDetailsData
import com.terracon.survey.model.BioPointDetailsResponse
import com.terracon.survey.model.BioPointDetailsWithSpecies
import com.terracon.survey.model.BioPointResponse
import com.terracon.survey.model.FileUploadResponseDTO
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.Species
import com.terracon.survey.model.UserApiRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PointDataRepository(
    private val pointDataRemoteDataSource: PointDataRemoteDataSource,
    private val pointDataDao: PointDataDao
) {


    suspend fun submitPoint(
        payload: BioPoint,
        isUpdateLocal: Boolean = false
    ): Flow<Result<BioPointResponse>?> {
        return flow {
            emit(Result.loading())
            val result = pointDataRemoteDataSource.submitPoint(payload)
            if (result.status == Result.Status.SUCCESS) {
                if (isUpdateLocal) {
                    result.data?.let {
                        var tempId = payload.dbId
                        payload.id = it.data.bio_diversity_survey_point_details.id
                        payload.isSynced = true
                        pointDataDao.insertBioPoint(bioPoint = payload)
                        if (tempId != null && payload.id != null) {
                            pointDataDao.updateBioPointIdInPointDetails(payload.id!!, tempId)
                        }


                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun submitPointDetails(payload: BioPointDetails): Flow<Result<BioPointDetailsResponse>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = pointDataRemoteDataSource.submitPointDetails(payload)
            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS) {
//                result.data?.let { it ->
//                    projectDao.deleteAll(it)
//                    userDao.insertAll(it)
//                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPointDetails(payload: PointDTO): Flow<Result<BioPointDetailsResponse>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = pointDataRemoteDataSource.getPointDetails(payload)
            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS) {
//                result.data?.let { it ->
//                    projectDao.deleteAll(it)
//                    userDao.insertAll(it)
//                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPointList(projectId: String): Flow<Result<BioPointResponse>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            // val result = pointDataRemoteDataSource.getPointList(projectId)
            val result = pointDataDao.getBioPointByProjectId(projectId).let {
                Result.success(
                    BioPointResponse(
                        data = BioPointData(
                            list = it,
                            bio_diversity_survey_point_details = BioPoint()
                        ), message = "success", status = "success"
                    )
                )
            }

            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS) {
//                result.data?.let { it ->
//                    projectDao.deleteAll(it)
//                    userDao.insertAll(it)
//                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fileUpload(
        data: HashMap<String, RequestBody>,
        image: MultipartBody.Part
    ): Flow<Result<FileUploadResponseDTO>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = pointDataRemoteDataSource.fileUpload(data, image)
            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS) {
//                result.data?.let { it ->
//                    projectDao.deleteAll(it)
//                    userDao.insertAll(it)
//                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    //offline functions
    suspend fun saveBioPointInLocalDB(bioPoint: BioPoint): Flow<Result<Long>> {
        return flow {
            emit(Result.loading())
            val result = pointDataDao.insertBioPoint(bioPoint).let {
                Result.success(it)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveBioPointDetailsInLocalDB(
        pointDTO: PointDTO,
        bioPointDetails: BioPointDetails
    ): Flow<Result<String>> {
        return flow {
            emit(Result.loading())

            var count = pointDataDao.getCountBioPointDetailsByIdTypeSubType(
                pointDTO.bio_diversity_survey_points_id,
                pointDTO.type,
                pointDTO.sub_type
            )

            if (count.isNotEmpty()) {
                pointDataDao.deleteSpecies(count[0].dbId.toString())
                bioPointDetails.species.forEach {
                    it.bio_diversity_survey_data_points_id = count[0].dbId?.toInt()
                }
                pointDataDao.insertBioPointDetailsSpeciesList(bioPointDetails.species)
            } else {
                val result = pointDataDao.insertBioPointDetails(bioPointDetails).let { id ->
                    bioPointDetails.species.forEach {
                        it.bio_diversity_survey_data_points_id = id.toInt()
                    }
                    pointDataDao.insertBioPointDetailsSpeciesList(bioPointDetails.species)
                }
            }


            emit(Result.success("success"))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPointDetailsFromLocal(pointDTO: PointDTO): Flow<Result<BioPointDetailsResponse>> {
        return flow {
            emit(Result.loading())

            val pointDetails: BioPointDetails = pointDataDao.getBioPointDetailsById(
                pointDTO.bio_diversity_survey_points_id,
                pointDTO.type,
                pointDTO.sub_type
            )
            if (pointDetails != null) {
                pointDetails.species = pointDataDao.getSpeciesById(pointDetails.dbId.toString())
            }

            val bioPointDetailsResponse: BioPointDetailsResponse =
                BioPointDetailsResponse(
                    data = BioPointDetailsData(pointDetails),
                    message = "success",
                    status = "success"
                )
            emit(Result.success(bioPointDetailsResponse))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getImageListToUpload(pointDTO: PointDTO): Flow<Result<List<Species>>?> {
        return flow {
            emit(Result.loading())
            val pointDetails: List<Int> = pointDataDao.getBioPointDetailsById(
                pointDTO.bio_diversity_survey_points_id
            )
            val result = pointDataDao.getSpeciesListById(pointDetails).let {
                Result.success(
                    it
                )
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


}
