package com.terracon.survey.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.terracon.survey.model.Fauna
import com.terracon.survey.model.Flora
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

    @Query("DELETE FROM Project")
    fun deleteAllData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFloraData(floraList: List<Flora>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFaunaData(faunaList: List<Fauna>)

    @Query("SELECT group_name FROM Fauna group by group_name")
    fun getFaunaCategories(): List<String>

    @Query("SELECT habit FROM Flora group by habit")
    fun getFloraCategories(): List<String>

    @Query("SELECT common_name FROM Fauna Where group_name = :subType ORDER BY common_name ASC")
    fun getFaunaList(subType:String): List<String>

    @Query("SELECT english_common_name FROM Flora Where habit = :subType ORDER BY english_common_name ASC")
    fun getFloraList(subType:String): List<String>

    @Delete
    fun deleteAllFlora(flora: List<Flora>)

    @Delete
    fun deleteAllFauna(fauna: List<Fauna>)

}