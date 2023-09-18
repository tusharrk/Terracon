package com.terracon.survey.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.terracon.survey.model.Project

@Dao
interface ProjectDao {

    @Query("SELECT * FROM Project ")
    fun getAll(): List<Project>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(projects: List<Project>)

    @Delete
    fun delete(project: Project)

    @Delete
    fun deleteAll(project: List<Project>)

}