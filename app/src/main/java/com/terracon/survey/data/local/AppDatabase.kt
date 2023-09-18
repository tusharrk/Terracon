package com.terracon.survey.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.terracon.survey.model.Project
import com.terracon.survey.model.User

//@TypeConverters(GenreConverters::class)

@Database(entities = [User::class, Project::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val projectDao: ProjectDao
    abstract val pointDataDao: PointDataDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app.db").build()
                }
            }
            return instance!!
        }
    }
}