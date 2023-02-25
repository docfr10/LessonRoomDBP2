package com.example.lesson_room_db_p2.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    context: Context,
    projectList: State<List<ProjectModel>>,
    projectsViewModel: ProjectsViewModel
) {
    val projectName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        // Parameters set to place the items in center
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Composable
        Icon(
            imageVector = Icons.Default.Create,
            contentDescription = "newNotification",
            tint = MaterialTheme.colorScheme.surfaceTint
        )
        // Text to Display the current Screen
        Text(text = "Create new project")
        // OutlinedTextField to type the new project name
        OutlinedTextField(
            value = projectName.value,
            isError = projectName.value.isEmpty(),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            onValueChange = { newText -> projectName.value = newText },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Type a project name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, // Keyboard type
                capitalization = KeyboardCapitalization.Sentences, // Letters type
                imeAction = ImeAction.Done // Keyboard action type
            )
        )
        // Displaying information about required field
        if (projectName.value.isEmpty()) {
            Text(
                text = "Required field",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 235.dp)
            )
        }
        // Button, to create project
        Button(onClick = {
            // Check the notification text for emptiness
            if (projectName.value.isNotEmpty()) {
                projectsViewModel.addProject(
                    projectModel = ProjectModel(
                        0,
                        projectName = projectName.value
                    )
                )
            } else Toast.makeText(context, "Type a project name", Toast.LENGTH_SHORT).show()
        }) { Text(text = "Create") }

        LazyColumn {
            items(projectList.value) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = it.projectName)
                        Text(
                            text = projectsViewModel.getFormattedTime(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        if (!projectsViewModel.getIsActive())
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Play",
                                modifier = Modifier.clickable { projectsViewModel.start() }
                            )
                        else
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Pause",
                                modifier = Modifier.clickable { projectsViewModel.pause() }
                            )
                    }
                }
            }
        }
    }
}