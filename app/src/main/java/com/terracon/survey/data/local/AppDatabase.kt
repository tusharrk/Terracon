package com.terracon.survey.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.Fauna
import com.terracon.survey.model.Flora
import com.terracon.survey.model.Project
import com.terracon.survey.model.Species
import com.terracon.survey.model.TreeAssessmentPoint
import com.terracon.survey.model.TreeAssessmentSpecies
import com.terracon.survey.model.User

//@TypeConverters(GenreConverters::class)

@Database(
    entities = [User::class, Project::class, Flora::class, Fauna::class, BioPoint::class, BioPointDetails::class, Species::class, TreeAssessmentPoint::class, TreeAssessmentSpecies::class],
    version = 11,
//      autoMigrations = [
//        AutoMigration (from = 10, to = 10)
//    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val projectDao: ProjectDao
    abstract val pointDataDao: PointDataDao
    abstract val treeAssessmentDao: TreeAssessmentDao



    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        val MIGRATION_1_2 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `TreeAssessmentSpecies` ADD isImageSynced INTEGER NOT NULL DEFAULT '0';")
                //database.execSQL("ALTER TABLE `TreeAssessmentSpecies` ALTER COLUMN gps_latitude VARCHAR(20) NULL;")

               // database.execSQL("CREATE TABLE IF NOT EXISTS `profile` (...)")
            }
        }
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app.db"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()


                }
            }
            return instance!!
        }
    }
}



