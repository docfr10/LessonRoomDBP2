package com.example.lesson_room_db_p2.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProjectDAOModel {
    // Выдача всех проектов
    @Query("SELECT * FROM project_table")
    fun readAllProjects(): LiveData<List<ProjectModel>>

    // Добавление нового проекта и игнорирование одинаковых имен
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addProject(projectModel: ProjectModel)

    // Обновление названия проекта
    @Update
    fun setNewProjectName(projectModel: ProjectModel)

    // Удаление проекта
    @Delete
    fun deleteProject(projectModel: ProjectModel)

    // Поиск проектов по части имени
    @Query("SELECT * FROM project_table WHERE projectName LIKE '%' || :searchText || '%'")
    fun searchProjects(searchText: String): LiveData<List<ProjectModel>>

    // Получить один проект по id
    @Query("SELECT * FROM project_table WHERE id = :projectId LIMIT 1")
    fun getProjectById(projectId: Int): LiveData<ProjectModel?>

    // Сортировка по имени от А до Я
    @Query("SELECT * FROM project_table ORDER BY projectName ASC")
    fun sortProjectsByNameAsc(): LiveData<List<ProjectModel>>

    // Сортировка по имени от Я до А
    @Query("SELECT * FROM project_table ORDER BY projectName DESC")
    fun sortProjectsByNameDesc(): LiveData<List<ProjectModel>>
}