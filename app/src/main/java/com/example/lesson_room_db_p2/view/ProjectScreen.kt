package com.example.lesson_room_db_p2.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lesson_room_db_p2.model.ProjectModel
import com.example.lesson_room_db_p2.viewModel.ProjectsViewModel
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProjectScreen(
    context: Context,
    projectList: State<List<ProjectModel>>,
    projectsViewModel: ProjectsViewModel
) {
    val projectName = remember { mutableStateOf("") }
    val searchText = remember { mutableStateOf("") }
    val projectIdText = remember { mutableStateOf("") }
    val currentMode = remember { mutableStateOf("all") }

    val bottomSheetScaffoldState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetScaffoldState)
    val scope = rememberCoroutineScope()

    val searchedProjects =
        projectsViewModel.searchProjects(searchText.value).observeAsState(initial = listOf())
    val sortedProjectsAsc =
        projectsViewModel.sortProjectsByNameAsc().observeAsState(initial = listOf())
    val sortedProjectsDesc =
        projectsViewModel.sortProjectsByNameDesc().observeAsState(initial = listOf())

    val projectId = projectIdText.value.toIntOrNull() ?: -1
    val foundProjectById =
        projectsViewModel.getProjectById(projectId).observeAsState(initial = null)

    val displayedProjects = when (currentMode.value) {
        "search" -> searchedProjects.value
        "sortAsc" -> sortedProjectsAsc.value
        "sortDesc" -> sortedProjectsDesc.value
        "byId" -> foundProjectById.value?.let { listOf(it) } ?: emptyList()
        else -> projectList.value
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            LazyColumn {
                items(displayedProjects) { project ->
                    val setNewProjectName = SwipeAction(
                        onSwipe = {
                            projectsViewModel.setNewProjectName(
                                projectModel = ProjectModel(
                                    id = project.id,
                                    projectName = "New project name"
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Set new project name"
                            )
                        },
                        background = MaterialTheme.colorScheme.surface
                    )

                    val deleteProject = SwipeAction(
                        onSwipe = { projectsViewModel.deleteProject(project) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete project"
                            )
                        },
                        background = MaterialTheme.colorScheme.error
                    )

                    SwipeableActionsBox(
                        swipeThreshold = 100.dp,
                        startActions = listOf(setNewProjectName),
                        endActions = listOf(deleteProject)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(text = project.projectName)

                                Text(
                                    text = projectsViewModel.getFormattedTime(project.id),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 25.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )

                                if (!projectsViewModel.getIsActive(project.id)) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        modifier = Modifier.clickable {
                                            projectsViewModel.start(project.id)
                                        }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = "Pause",
                                        modifier = Modifier.clickable {
                                            projectsViewModel.pause(project.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.background)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Create,
                contentDescription = "newNotification",
                tint = MaterialTheme.colorScheme.surfaceTint
            )

            Text(text = "Create new project")

            OutlinedTextField(
                value = projectName.value,
                isError = projectName.value.isEmpty(),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                onValueChange = { newText -> projectName.value = newText },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Type a project name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            if (projectName.value.isEmpty()) {
                Text(
                    text = "Required field",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 235.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (projectName.value.isNotEmpty()) {
                        projectsViewModel.addProject(
                            projectModel = ProjectModel(
                                0,
                                projectName = projectName.value
                            )
                        )
                        projectName.value = ""
                    } else {
                        Toast.makeText(
                            context,
                            "Type a project name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(text = "Create")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Search and selection",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by project name") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        currentMode.value = "search"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Search")
                }

                Button(
                    onClick = {
                        searchText.value = ""
                        projectIdText.value = ""
                        currentMode.value = "all"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = projectIdText.value,
                onValueChange = { projectIdText.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Find project by id") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (projectIdText.value.toIntOrNull() != null) {
                        currentMode.value = "byId"
                    } else {
                        Toast.makeText(context, "Enter valid id", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Find by id")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { currentMode.value = "sortAsc" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = "Sort asc"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("A-Z")
                }

                Button(
                    onClick = { currentMode.value = "sortDesc" },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = "Sort desc"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Z-A")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (bottomSheetScaffoldState.isCollapsed)
                            bottomSheetScaffoldState.expand()
                        else
                            bottomSheetScaffoldState.collapse()
                    }
                }
            ) {
                Text(text = "Open projects")
            }
        }
    }
}