package com.terracon.survey.data


import android.graphics.Point
import android.widget.GridLayout.Spec
import com.michaelflisar.lumberjack.core.L
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
                        payload.isSynced = false
                        pointDataDao.insertBioPoint(bioPoint = payload)
                        if (tempId != null && payload.id != null) {
                            pointDataDao.updateBioPointIdInPointDetails(payload.id!!, tempId)
                        }


                    }
                }
                result.data?.data?.bio_diversity_survey_point_details?.dbId = payload.dbId

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
                payload.dbId?.let { pointDataDao.updateBioPointDetailsSyncStatus(true, it) }
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
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fileUpload(
        data: HashMap<String, RequestBody>,
        image: MultipartBody.Part,
        species: Species
    ): Flow<Result<FileUploadResponseDTO>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = pointDataRemoteDataSource.fileUpload(data, image)


            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS) {
                result.data?.let { it1 ->
                    species.dbId?.let { it2 ->
                        pointDataDao.updateSpeciesImageUrl(
                            it1.fileUrl,
                            true,
                            it2
                        )
                    }
                }
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

    suspend fun updateBioPointSyncStatusInLocalDB(bioPoint: BioPoint): Flow<Result<Int>?> {
        return flow {
            emit(Result.loading())
           var result  = bioPoint.id?.let {
               return@let pointDataDao.updatePointSyncStatus(it,true).let {
                    Result.success(it)
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
    suspend fun saveBioPointDetailsInLocalDB(
        pointDTO: PointDTO,
        bioPointDetails: BioPointDetails,
    ): Flow<Result<BioPointDetails>> {
        return flow {
            emit(Result.loading())

            var count = pointDataDao.getCountBioPointDetailsByIdTypeSubType(
                pointDTO.bio_diversity_survey_points_id,
                pointDTO.type,
                pointDTO.sub_type
            )

            if (count.isNotEmpty()) {
               // pointDataDao.deleteSpecies(count[0].dbId.toString())
                bioPointDetails.species.forEach {
                    it.tempId = count[0].dbId?.toInt()
                }
                pointDataDao.insertBioPointDetailsSpeciesList(bioPointDetails.species)
            } else {
                val result = pointDataDao.insertBioPointDetails(bioPointDetails).let { id ->
                    bioPointDetails.species.forEach {
                        it.tempId = id.toInt()
                    }
                    bioPointDetails.dbId = id.toInt()
                    pointDataDao.insertBioPointDetailsSpeciesList(bioPointDetails.species)
                }
            }


            emit(Result.success(bioPointDetails))
        }.flowOn(Dispatchers.IO)
    }
    suspend fun updateSpeciesData(species: Species): Flow<Result<String>> {
        return flow {
            emit(Result.loading())
            L.d{"TAG_X-${species}" }

            species.dbId?.let { pointDataDao.updateSpeciesData(name = species.name, count = species.count, imageUrl = species.images, comment = species.comment, gps_longitude = species.gps_longitude, gps_latitude = species.gps_latitude, isSynced = species.isSynced, dbId = it) }
            emit(Result.success("success"))
        }
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

    suspend fun getSpeciesListFromLocal(pointDetails: BioPointDetails): Flow<Result<List<Species>>> {
        return flow {
            emit(Result.loading())
            val species : List<Species> = pointDataDao.getSpeciesById(pointDetails.dbId.toString())
            emit(Result.success(species))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteSpecieFromDB(species: Species): Flow<Result<String>> {
        return flow {
            emit(Result.loading())
            pointDataDao.deleteSpeciesById(species.dbId.toString())
            emit(Result.success("success"))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllPointDetailsFromLocal(bioId: Int): Flow<Result<List<BioPointDetails>>> {
        return flow {
            emit(Result.loading())

            val pointDetails: List<BioPointDetails> = pointDataDao.getAllBioPointDetailsById(bioId)
            if (pointDetails.isNotEmpty()) {
                pointDetails.forEach {
                    it.species = pointDataDao.getSpeciesById(it.dbId.toString())
                }
            }

//            val bioPointDetailsResponse: BioPointDetailsResponse =
//                BioPointDetailsResponse(
//                    data = BioPointDetailsData(pointDetails),
//                    message = "success",
//                    status = "success"
//                )
            emit(Result.success(pointDetails))
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
