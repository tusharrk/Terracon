package com.terracon.survey.data


import com.terracon.survey.data.local.ProjectDao
import com.terracon.survey.data.remote.ProjectRemoteDataSource
import com.terracon.survey.model.FloraFaunaCategoryResponse
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.UserApiRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProjectRepository (
    private val projectRemoteDataSource: ProjectRemoteDataSource,
    private val projectDao: ProjectDao
) {


    suspend fun getAllProjects(payload: UserApiRequestDTO): Flow<Result<List<Project>>?> {
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

    suspend fun getFloraFaunaCategories(payload: UserApiRequestDTO): Flow<Result<FloraFaunaCategoryResponse>?> {
        return flow {
            //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = projectRemoteDataSource.getFloraFaunaCategories(payload)
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

}