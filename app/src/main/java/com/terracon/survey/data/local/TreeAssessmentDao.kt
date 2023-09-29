package com.terracon.survey.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.Species
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentSpecies

@Dao
interface TreeAssessmentDao {

    @Query("SELECT * FROM TreeAssessmentPoint ")
    fun getAll(): List<TreeAssessmentPoint>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<TreeAssessmentPoint>)

    @Delete
    fun delete(data: TreeAssessmentPoint)

    @Delete
    fun deleteAll(data: List<TreeAssessmentPoint>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoint(point: TreeAssessmentPoint):Long

    @Transaction
    @Query("UPDATE TreeAssessmentPoint SET isSynced =:isSynced Where id = :pointId")
    fun updateBioPointSyncStatus(pointId: Int,isSynced: Boolean) : Int

    @Query("SELECT * FROM TreeAssessmentPoint Where project_id = :projectId")
    fun getPointByProjectId(projectId: String): List<TreeAssessmentPoint>


    @Transaction
    @Query("SELECT * FROM TreeAssessmentSpecies Where tempId = :id")
    fun getSpeciesById(id: String): List<TreeAssessmentSpecies>

    @Query("SELECT * FROM TreeAssessmentSpecies Where tempId =:id and isSynced = 0")
    fun getSpeciesListById(id: String): List<TreeAssessmentSpecies>


    @Query("DELETE FROM TreeAssessmentSpecies WHERE tempId = :id")
    fun deleteSpecies(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTreePointSpeciesList(species: List<TreeAssessmentSpecies>)

    @Transaction
    @Query("UPDATE TreeAssessmentSpecies SET isSynced =:isSynced Where dbId = :id")
    fun updateSpeciesPointDetailsSyncStatus(isSynced: Boolean,id: Int)

    @Transaction
    @Query("UPDATE TreeAssessmentSpecies SET tree_assessment_survey_points_id =:pointId Where tempId = :id")
    fun updatePointIdInPointDetails(pointId: Int,id: Int)

    @Transaction
    @Query("UPDATE TreeAssessmentSpecies SET images =:imageUrl, isSynced =:isSynced Where dbId =:dbId ")
    fun updateSpeciesImageUrl(imageUrl: String,isSynced:Boolean,dbId:Int)

}