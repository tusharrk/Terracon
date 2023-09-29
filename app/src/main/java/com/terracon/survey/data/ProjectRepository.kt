package com.terracon.survey.data


import com.terracon.survey.data.local.ProjectDao
import com.terracon.survey.data.remote.ProjectRemoteDataSource
import com.terracon.survey.model.Fauna
import com.terracon.survey.model.FaunaMasterResponse
import com.terracon.survey.model.Flora
import com.terracon.survey.model.FloraFaunaCategoryResponse
import com.terracon.survey.model.FloraMasterResponse
import com.terracon.survey.model.Project
import com.terracon.survey.model.ProjectResponse
import com.terracon.survey.model.Result
import com.terracon.survey.model.UserApiRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProjectRepository(
    private val projectRemoteDataSource: ProjectRemoteDataSource,
    private val projectDao: ProjectDao
) {


    suspend fun getAllProjects(payload: UserApiRequestDTO): Flow<Result<ProjectResponse>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = projectRemoteDataSource.getAllProjects(payload)
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

    suspend fun getFloraData(payload: UserApiRequestDTO): Flow<Result<FloraMasterResponse>?> {
        return flow {
            emit(Result.loading())
            val result = projectRemoteDataSource.getFloraData(payload)
            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS && result.data?.status == "success") {
                result.data.let { it ->
                    projectDao.deleteAllFlora(it.data.data)
                    projectDao.insertFloraData(it.data.data)
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getFaunaData(payload: UserApiRequestDTO): Flow<Result<FaunaMasterResponse>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = projectRemoteDataSource.getFaunaData(payload)
            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS && result.data?.status == "success") {
                result.data.let { it ->
                    projectDao.deleteAllFauna(it.data.data)
                    projectDao.insertFaunaData(it.data.data)
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getFloraFaunaCategoriesLocal(): Flow<Result<FloraFaunaCategoryResponse>> {
        return flow {
            emit(Result.loading())
            val result = getBothCategories()
            Result.success(result)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    private fun getBothCategories(): Result<FloraFaunaCategoryResponse>{
        val categories = FloraFaunaCategoryResponse(
            floraCategoryList = projectDao.getFloraCategories(),
            faunaCategoryList = projectDao.getFaunaCategories()
        )
        return Result.success(categories)
    }

    suspend fun getFloraFaunaSpeciesListBasedOnType(type:String,subType:String): Flow<Result<List<String>>?> {
        return flow {
            emit(Result.loading())
            val result = getList(type,subType)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    private fun getList(type:String, subType:String):Result<List<String>>{
        var list = listOf<String>()
        if(type=="Fauna"){
            list = projectDao.getFaunaList(subType)
        }else{
            list = projectDao.getFloraList(subType)
        }
        return Result.success(list)
    }
//    suspend fun getFaunaSpeciesListBasedOnType(subType:String): Flow<Result<List<String>>?> {
//        return flow {
//            emit(Result.loading())
//            val result = projectDao.getFaunaList(subType).let {
//                Result.success(it)
//            }
//            emit(result)
//        }.flowOn(Dispatchers.IO)
//    }



    suspend fun getFaunaCategoriesLocal(): Flow<Result<List<String>>?> {
        return flow {
            emit(Result.loading())
            val result = projectDao.getFaunaCategories().let {
                Result.success(it)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getFloraCategoriesLocal(): Flow<Result<List<String>>?> {
        return flow {
            emit(Result.loading())
            val result = projectDao.getFloraCategories().let {
                Result.success(it)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

}