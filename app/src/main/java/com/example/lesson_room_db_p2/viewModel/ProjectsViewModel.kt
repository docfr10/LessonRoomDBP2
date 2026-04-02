package com.example.lesson_room_db_p2.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lesson_room_db_p2.model.AppDatabaseModel
import com.example.lesson_room_db_p2.model.ProjectModel
import com.example.lesson_room_db_p2.model.ProjectRepositoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val projectDAOModel = AppDatabaseModel.getDatabase(context = application).projectDAO()
    private val repositoryModel = ProjectRepositoryModel(projectDAOModel = projectDAOModel)
    private val readAllProjects: LiveData<List<ProjectModel>> = repositoryModel.readAllProjects()

    private val formattedTimes = mutableStateMapOf<Int, String>()
    private val activeStates = mutableStateMapOf<Int, Boolean>()
    private val timeMillisMap = mutableStateMapOf<Int, Long>()
    private val lastTimestampMap = mutableStateMapOf<Int, Long>()
    private val jobs = mutableMapOf<Int, Job>()

    fun getReadAllProjects(): LiveData<List<ProjectModel>> {
        return readAllProjects
    }

    fun addProject(projectModel: ProjectModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryModel.addProject(projectModel = projectModel)
        }
    }

    fun setNewProjectName(projectModel: ProjectModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryModel.setNewProjectName(projectModel = projectModel)
        }
    }

    fun deleteProject(projectModel: ProjectModel) {
        pause(projectModel.id)

        formattedTimes.remove(projectModel.id)
        activeStates.remove(projectModel.id)
        timeMillisMap.remove(projectModel.id)
        lastTimestampMap.remove(projectModel.id)
        jobs.remove(projectModel.id)

        viewModelScope.launch(Dispatchers.IO) {
            repositoryModel.deleteProject(projectModel = projectModel)
        }
    }

    private fun formatTime(timeMillis: Long): String {
        val seconds = timeMillis / 1000 % 60
        val minutes = timeMillis / 60000 % 60
        val hours = timeMillis / 3600000
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    fun getIsActive(projectId: Int): Boolean {
        return activeStates[projectId] ?: false
    }

    fun getFormattedTime(projectId: Int): String {
        return formattedTimes[projectId] ?: "00:00:00"
    }

    fun start(projectId: Int) {
        if (activeStates[projectId] == true) return

        activeStates[projectId] = true
        lastTimestampMap[projectId] = System.currentTimeMillis()

        jobs[projectId] = viewModelScope.launch {
            while (activeStates[projectId] == true) {
                delay(10L)

                val lastTimestamp = lastTimestampMap[projectId] ?: System.currentTimeMillis()
                val currentTime = System.currentTimeMillis()
                val currentMillis = timeMillisMap[projectId] ?: 0L

                val updatedMillis = currentMillis + (currentTime - lastTimestamp)
                timeMillisMap[projectId] = updatedMillis
                lastTimestampMap[projectId] = currentTime
                formattedTimes[projectId] = formatTime(updatedMillis)
            }
        }
    }

    fun pause(projectId: Int) {
        activeStates[projectId] = false
        jobs[projectId]?.cancel()
        jobs.remove(projectId)
    }
}