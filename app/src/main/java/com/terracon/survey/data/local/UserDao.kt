package com.terracon.survey.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.terracon.survey.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User ")
    fun getAll(): List<User>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<User>)

    @Delete
    fun delete(movie: User)

    @Delete
    fun deleteAll(movie: List<User>)

}