package com.example.lesson_room_db_p2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.lesson_room_db_p2.ui.theme.LessonRoomDBP2Theme
import com.example.lesson_room_db_p2.view.ProjectScreen
import com.example.lesson_room_db_p2.viewModel.ProjectsViewModel

class MainActivity : ComponentActivity() {
    private lateinit var projectsViewModel: ProjectsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LessonRoomDBP2Theme {
                val context = LocalContext.current
                val provider = ViewModelProvider(this)
                projectsViewModel = provider[ProjectsViewModel::class.java]
                // Saving a list with project data
                val projectList =
                    projectsViewModel.getReadAllProjects().observeAsState(initial = listOf())

                ProjectScreen(
                    context = context,
                    projectList = projectList,
                    projectsViewModel = projectsViewModel
                )
            }
        }
    }
}