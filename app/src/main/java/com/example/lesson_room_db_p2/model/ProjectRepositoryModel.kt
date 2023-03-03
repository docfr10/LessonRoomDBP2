package com.example.lesson_room_db_p2.model

import androidx.lifecycle.LiveData

class ProjectRepositoryModel(private val projectDAOModel: ProjectDAOModel) {
    fun readAllProjects(): LiveData<List<ProjectModel>> {
        return projectDAOModel.readAllProjects()
    }

    fun addProject(projectModel: ProjectModel) {
        projectDAOModel.addProject(projectModel = projectModel)
    }

    fun setNewProjectName(projectModel: ProjectModel) {
        projectDAOModel.setNewProjectName(projectModel = projectModel)
    }

    fun deleteProject(projectModel: ProjectModel) {
        projectDAOModel.deleteProject(projectModel = projectModel)
    }
}