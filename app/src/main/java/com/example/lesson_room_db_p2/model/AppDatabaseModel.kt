package com.example.lesson_room_db_p2.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProjectModel::class], version = 1, exportSchema = false)
abstract class AppDatabaseModel : RoomDatabase() {
    abstract fun projectDAO(): ProjectDAOModel

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseModel? = null

        fun getDatabase(context: Context): AppDatabaseModel {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabaseModel::class.java, "notify_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}