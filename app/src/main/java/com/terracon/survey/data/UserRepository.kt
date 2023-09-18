package com.terracon.survey.data


import com.terracon.survey.data.local.UserDao
import com.terracon.survey.data.remote.UserRemoteDataSource
import com.terracon.survey.model.Result
import com.terracon.survey.model.User
import com.terracon.survey.model.UserApiRequestDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepository (
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao
) {


    suspend fun getAllUsers(payload: UserApiRequestDTO): Flow<Result<List<User>>?> {
        return flow {
         //   emit(fetchUsersCached())
            emit(Result.loading())
            val result = userRemoteDataSource.getAllUsersByUserId(payload)
            //Cache to database if response is successful
            if (result.status == Result.Status.SUCCESS) {
                result.data?.let { it ->
                    userDao.deleteAll(it)
                //    userDao.insertAll(it)
                }
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    private fun fetchUsersCached(): Result<List<User>>? =
        userDao.getAll()?.let {
            Result.success(it)
        }

}