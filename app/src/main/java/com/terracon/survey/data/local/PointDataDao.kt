package com.terracon.survey.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.BioPointDetailsResponse
import com.terracon.survey.model.BioPointDetailsWithSpecies
import com.terracon.survey.model.Project
import com.terracon.survey.model.Species

@Dao
interface PointDataDao {

    @Query("SELECT * FROM BioPoint ")
    fun getAll(): List<BioPoint>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bioPoint: List<BioPoint>)


    @Delete
    fun delete(bioPoint: BioPoint)

    @Delete
    fun deleteAll(bioPoint: List<BioPoint>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBioPoint(bioPoint: BioPoint):Long

    @Query("SELECT * FROM BioPoint Where project_id = :projectId")
    fun getBioPointByProjectId(projectId: String): List<BioPoint>


    //bio point details
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBioPointDetails(bioPointDetails: BioPointDetails): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBioPointDetailsSpeciesList(species: List<Species>)

    @Transaction
    @Query("SELECT * FROM BioPointDetails Where bio_diversity_survey_points_id = :id and type =:type and sub_type =:subType Limit 1")
    fun getBioPointDetailsById(id: String, type: String, subType: String): BioPointDetails

    @Transaction
    @Query("SELECT * FROM Species Where bio_diversity_survey_data_points_id = :id")
    fun getSpeciesById(id: String): List<Species>

    @Transaction
    @Query("SELECT * FROM BioPointDetails Where bio_diversity_survey_points_id = :id and type =:type and sub_type =:subType")
    fun getCountBioPointDetailsByIdTypeSubType(id: String, type: String, subType: String): List<BioPointDetails>


    @Query("DELETE FROM Species WHERE bio_diversity_survey_data_points_id = :id")
    fun deleteSpecies(id: String)
}