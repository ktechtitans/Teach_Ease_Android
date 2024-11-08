package com.example.teachease.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.compose.ui.graphics.Color
import androidx.core.text.HtmlCompat
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(courseName: String, courseDescription: String, imageUrl: String, navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Course Details")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
                .verticalScroll(rememberScrollState())  // Scrollable if content is long
        ) {
            // Course Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = courseName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Space between image and course name
            Spacer(modifier = Modifier.height(16.dp))

            // Course Name
            Text(
                text = courseName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Space between course name and description
            Spacer(modifier = Modifier.height(8.dp))

            // Render Course Description with HTML formatting
            HtmlText(htmlText = courseDescription)
        }
    }
}

@Composable
fun HtmlText(htmlText: String) {
    val spanned = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT)
    Text(
        text = spanned.toString(), // Converts spanned HTML to String (full HTML support may need custom render)
        fontSize = 16.sp,
        color = Color.Gray,
        overflow = TextOverflow.Ellipsis
    )
}








//package com.example.teachease.ui.screen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.rememberImagePainter
//import androidx.compose.ui.graphics.Color
//import androidx.navigation.NavHostController
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CourseDetailScreen(courseName: String, courseDescription: String, imageUrl: String, navController: NavHostController) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(text = "Course Details")
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState())  // Scrollable if content is long
//        ) {
//            // Course Image
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp),
//                elevation = CardDefaults.cardElevation(8.dp)
//            ) {
//                Image(
//                    painter = rememberImagePainter(data = imageUrl),
//                    contentDescription = courseName,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//
//            // Space between image and course name
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Course Name
//            Text(
//                text = courseName,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//            // Space between course name and description
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Full Course Description
//            Text(
//                text = courseDescription,
//                fontSize = 16.sp,
//                color = Color.Gray
//            )
//        }
//    }
//}
