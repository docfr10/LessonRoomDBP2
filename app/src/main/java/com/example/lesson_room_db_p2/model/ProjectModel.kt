package com.example.lesson_room_db_p2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class ProjectModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val projectName: String
)