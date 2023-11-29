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

    @Transaction
    @Query("UPDATE BioPoint SET isSynced =:isSynced Where id = :bioPointId")
    fun updatePointSyncStatus(bioPointId: Int,isSynced: Boolean) : Int


    @Query("SELECT * FROM BioPoint Where project_id = :projectId")
    fun getBioPointByProjectId(projectId: String): List<BioPoint>


    //bio point details
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBioPointDetails(bioPointDetails: BioPointDetails): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBioPointDetailsSpeciesList(species: List<Species>)

    @Transaction
    @Query("SELECT * FROM BioPointDetails Where tempId = :id and type =:type and sub_type =:subType Limit 1")
    fun getBioPointDetailsById(id: String, type: String, subType: String): BioPointDetails

    @Transaction
    @Query("SELECT * FROM BioPointDetails Where tempId = :id ")
    fun getAllBioPointDetailsById(id: Int): List<BioPointDetails>

    @Transaction
    @Query("SELECT * FROM Species Where tempId = :id")
    fun getSpeciesById(id: String): List<Species>

    @Transaction
    @Query("SELECT * FROM BioPointDetails Where tempId = :id and type =:type and sub_type =:subType")
    fun getCountBioPointDetailsByIdTypeSubType(id: String, type: String, subType: String): List<BioPointDetails>


    @Query("DELETE FROM Species WHERE tempId = :id")
    fun deleteSpecies(id: String)

    @Query("DELETE FROM Species WHERE dbId = :id")
    fun deleteSpeciesById(id: String)


    @Transaction
    @Query("UPDATE BioPointDetails SET bio_diversity_survey_points_id =:bioPointId Where tempId = :id")
    fun updateBioPointIdInPointDetails(bioPointId: Int,id: Int)

    @Transaction
    @Query("UPDATE BioPointDetails SET isSynced =:isSynced Where dbId = :id")
    fun updateBioPointDetailsSyncStatus(isSynced: Boolean,id: Int)


    //species
    @Query("SELECT * FROM Species Where tempId in(:idList) and isSynced = 0")
    fun getSpeciesListById(idList: List<Int>): List<Species>

    @Transaction
    @Query("SELECT dbId FROM BioPointDetails Where tempId = :id and isSynced = 0")
    fun getBioPointDetailsById(id: String): List<Int>

    @Transaction
    @Query("UPDATE Species SET images =:imageUrl, isSynced =:isSynced Where dbId =:dbId ")
    fun updateSpeciesImageUrl(imageUrl: String,isSynced:Boolean,dbId:Int)

    @Transaction
    @Query("UPDATE Species SET name =:name, count =:count, images =:imageUrl, comment =:comment ,gps_latitude =:gps_latitude,gps_longitude =:gps_longitude, isSynced =:isSynced Where dbId =:dbId ")
    fun updateSpeciesData(name: String,count:String,imageUrl: String?,comment:String?,gps_latitude: String?,gps_longitude: String?,isSynced:Boolean,dbId:Int)
}