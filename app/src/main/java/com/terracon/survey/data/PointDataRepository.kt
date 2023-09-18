package com.terracon.survey.data


import com.terracon.survey.data.local.PointDataDao
import com.terracon.survey.data.local.ProjectDao
import com.terracon.survey.data.remote.PointDataRemoteDataSource
import com.terracon.survey.data.remote.ProjectRemoteDataSource
import com.terracon.survey.model.FloraFaunaCategoryResponse
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.UserApiRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PointDataRepository (
    private val pointDataRemoteDataSource: PointDataRemoteDataSource,
    private val pointDataDao: PointDataDao
) {


    suspend fun submitPointDetails(payload: UserApiRequestDTO): Flow<Result<List<Project>>?> {
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



}