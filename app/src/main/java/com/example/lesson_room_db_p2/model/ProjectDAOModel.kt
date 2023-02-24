package com.example.lesson_room_db_p2.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProjectDAOModel {
    @Query("SELECT * FROM project_table")
    fun readAllProjects(): LiveData<List<ProjectModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addProject(projectModel: ProjectModel)
}