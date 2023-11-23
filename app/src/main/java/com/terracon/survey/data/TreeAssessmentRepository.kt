package com.terracon.survey.data


import com.michaelflisar.lumberjack.core.L
import com.terracon.survey.data.local.ProjectDao
import com.terracon.survey.data.local.TreeAssessmentDao
import com.terracon.survey.data.remote.ProjectRemoteDataSource
import com.terracon.survey.data.remote.TreeAssessmentRemoteDataSource
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointData
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.BioPointDetailsData
import com.terracon.survey.model.BioPointDetailsResponse
import com.terracon.survey.model.BioPointResponse
import com.terracon.survey.model.Fauna
import com.terracon.survey.model.FaunaMasterResponse
import com.terracon.survey.model.FileUploadResponseDTO
import com.terracon.survey.model.Flora
import com.terracon.survey.model.FloraFaunaCategoryResponse
import com.terracon.survey.model.FloraMasterResponse
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Project
import com.terracon.survey.model.ProjectResponse
import com.terracon.survey.model.Result
import com.terracon.survey.model.Species
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentPointData
import com.terracon.survey.model.TreeAssessmentResponse
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.model.TreeAssessmentSpeciesData
import com.terracon.survey.model.TreeAssessmentSpeciesResponse
import com.terracon.survey.model.UserApiRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody

class TreeAssessmentRepository(
    private val treeAssessmentRemoteDataSource: TreeAssessmentRemoteDataSource,
    private val treeAssessmentDao: TreeAssessmentDao
) {


    suspend fun submitTreePoint(
        payload: TreeAssessmentPoint,
        isUpdateLocal: Boolean = false
    ): Flow<Result<TreeAssessmentResponse>?> {
        return flow {
            emit(Result.loading())
            val result = treeAssessmentRemoteDataSource.saveTreePointData(payload)
            if (result.status == Result.Status.SUCCESS) {
                if (isUpdateLocal) {
                    result.data?.let {
                        var tempId = payload.dbId
                        payload.id = it.data.tree_assessment_survey_points.id
                        payload.isSynced = false
                        treeAssessmentDao.insertPoint(point = payload)
                        if (tempId != null && payload.id != null) {
                            treeAssessmentDao.updatePointIdInPointDetails(payload.id!!, tempId)
                        }
                    }
                }
                result.data?.data?.tree_assessment_survey_points?.dbId = payload.dbId
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun submitTreeSpecies(payload: TreeAssessmentSpecies): Flow<Result<TreeAssessmentSpeciesResponse>?> {
        return flow {
            emit(Result.loading())
            val result = treeAssessmentRemoteDataSource.saveTreeSpeciesDetails(payload)
            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS) {
                payload.dbId?.let {
                    treeAssessmentDao.updateSpeciesPointDetailsSyncStatus(
                        true,
                        it
                    )
                }

            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fileUpload(
        data: HashMap<String, RequestBody>,
        image: MultipartBody.Part,
        species: TreeAssessmentSpecies
    ): Flow<Result<FileUploadResponseDTO>?> {
        return flow {
            emit(Result.loading())
            val result = treeAssessmentRemoteDataSource.fileUpload(data, image)

            if (result.status == Result.Status.SUCCESS) {
                result.data?.let { it1 ->
                    species.dbId?.let { it2 ->
                        treeAssessmentDao.updateSpeciesImageUrl(
                            it1.fileUrl,
                            true,
                            it2
                        )
                    }
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun savePointInLocalDB(treeAssessmentPoint: TreeAssessmentPoint): Flow<Result<Long>> {
        return flow {
            emit(Result.loading())
            val result = treeAssessmentDao.insertPoint(treeAssessmentPoint).let {
                Result.success(it)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun savePointSpeciesInLocalDB(
        treeAssessmentPoint: TreeAssessmentPoint,
        speciesList: List<TreeAssessmentSpecies>
    ): Flow<Result<String>> {
        return flow {
            emit(Result.loading())

            if (treeAssessmentPoint.dbId != null) {
              //  treeAssessmentDao.deleteSpecies(treeAssessmentPoint.dbId.toString())
//                speciesList.forEach {
//                    it.tree_assessment_survey_points_id = treeAssessmentPoint.dbId!!
//                }
                treeAssessmentDao.insertTreePointSpeciesList(speciesList)
            }

            emit(Result.success("success"))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateSpeciesData(species: TreeAssessmentSpecies): Flow<Result<String>> {
        return flow {
            emit(Result.loading())
            L.d{"TAG_X-${species}" }
            //canopy_diameter: String,girth: String,gps_latitude: String,gps_longitude: String,height: String,name: String,serial_number:String,isImageSynced:String,imageUrl: String?,comment:String?,isSynced:Boolean,dbId:Int)

            species.dbId?.let { treeAssessmentDao.updateSpeciesData(canopy_diameter = species.canopy_diameter, girth = species.girth,gps_latitude = species.gps_latitude,gps_longitude = species.gps_longitude,height = species.height,name = species.name, serial_number = species.serial_number, isImageSynced = species.isImageSynced, imageUrl = species.images, comment = species.comment, isSynced = species.isSynced, dbId = it) }
            emit(Result.success("success"))
        }
    }



    suspend fun getPointList(projectId: String): Flow<Result<TreeAssessmentResponse>?> {
        return flow {
            emit(Result.loading())
            val result = treeAssessmentDao.getPointByProjectId(projectId).let {
                Result.success(
                    TreeAssessmentResponse(
                        data = TreeAssessmentPointData(
                            list = it,
                            tree_assessment_survey_points = TreeAssessmentPoint()
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

    suspend fun getPointDetailsFromLocal(treeAssessmentPoint: TreeAssessmentPoint): Flow<Result<TreeAssessmentSpeciesResponse>> {
        return flow {
            emit(Result.loading())

            val bioPointDetailsResponse: TreeAssessmentSpeciesResponse =
                TreeAssessmentSpeciesResponse(
                    data = TreeAssessmentSpeciesData(
                        treeAssessmentDao.getSpeciesById(treeAssessmentPoint.dbId.toString()),
                        TreeAssessmentSpecies()
                    ),
                    message = "success",
                    status = "success"
                )
            emit(Result.success(bioPointDetailsResponse))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllSpeciesFromLocal(pointId: Int): Flow<Result<List<TreeAssessmentSpecies>>> {
        return flow {
            emit(Result.loading())
            val pointDetails: List<TreeAssessmentSpecies> = treeAssessmentDao.getSpeciesById(pointId.toString())
            emit(Result.success(pointDetails))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun updateBioPointSyncStatusInLocalDB(point: TreeAssessmentPoint): Flow<Result<Int>?> {
        return flow {
            emit(Result.loading())
            var result  = point.id?.let {
                return@let treeAssessmentDao.updateBioPointSyncStatus(it,true).let {
                    Result.success(it)
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getImageListToUpload(treeAssessmentPoint: TreeAssessmentPoint): Flow<Result<List<TreeAssessmentSpecies>>?> {
        return flow {

            val result = treeAssessmentDao.getSpeciesListById(treeAssessmentPoint.dbId.toString()).let {
                Result.success(
                    it
                )
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getSpeciesListFromLocal(treeAssessmentPoint: TreeAssessmentPoint): Flow<Result<List<TreeAssessmentSpecies>>> {
        return flow {
            emit(Result.loading())
            val species : List<TreeAssessmentSpecies> = treeAssessmentDao.getSpeciesById(treeAssessmentPoint.dbId.toString())
            emit(Result.success(species))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteSpecieFromDB(species: TreeAssessmentSpecies): Flow<Result<String>> {
        return flow {
            emit(Result.loading())
            treeAssessmentDao.deleteSpeciesById(species.dbId.toString())
            emit(Result.success("success"))
        }.flowOn(Dispatchers.IO)
    }


}