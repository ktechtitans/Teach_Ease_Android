package com.example.teachease.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCourseDetailScreen(courseName: String, courseDescription: String, imageUrl: String, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = courseName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = courseName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = courseDescription,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
