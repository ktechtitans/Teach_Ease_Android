package com.example.teachease.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import kotlinx.coroutines.awaitAll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Course(
    val courseName: String = "",
    val courseDescription: String = "",
    val imageUrl: String = ""  // Store the image URL from Firestore
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var searchQuery by remember { mutableStateOf("") }
    val courses = remember { mutableStateListOf<Course>() }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Courses") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search input field
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search for a course") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotEmpty()) {
                            isLoading = true
                            isError = false
                            coroutineScope.launch {
                                courses.clear()
                                try {
                                    // Fetch all categories
                                    val categories = db.collection("categories").get().await()
                                    val searchResults = categories.documents.flatMap { categoryDoc ->
                                        val categoryId = categoryDoc.id
                                        // Query each `courses` subcollection for the search term
                                        db.collection("categories")
                                            .document(categoryId)
                                            .collection("courses")
                                            .whereGreaterThanOrEqualTo("courseName", searchQuery)
                                            .get()
                                            .await()
                                            .documents.mapNotNull { doc ->
                                                doc.toObject(Course::class.java)
                                            }
                                    }
                                    courses.addAll(searchResults)
                                } catch (e: Exception) {
                                    isError = true
                                    e.printStackTrace()
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (isError) {
                Text("Error occurred while fetching courses", color = MaterialTheme.colorScheme.error)
            } else if (courses.isEmpty()) {
                Text("No results found", fontSize = 16.sp)
            } else {
                // Display the search results
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    courses.forEach { course ->
                        CourseCard(course = course, navController = navController)
                    }
                }
            }
        }
    }
}
@Composable
fun CourseCard(course: Course, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to the SearchCourseDetailScreen with URL-encoded parameters
                val encodedCourseName = Uri.encode(course.courseName)
                val encodedCourseDescription = Uri.encode(course.courseDescription)
                val encodedImageUrl = Uri.encode(course.imageUrl)

                navController.navigate(
                    "search_course_detail/$encodedCourseName/$encodedCourseDescription/$encodedImageUrl"
                )
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = course.courseName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = course.courseDescription.take(50) + "...", fontSize = 14.sp)
        }
    }
}



//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchScreen(navController: NavHostController) {
//    val db = Firebase.firestore
//    var searchQuery by remember { mutableStateOf("") }
//    val courses = remember { mutableStateListOf<Course>() }
//    var isLoading by remember { mutableStateOf(false) }
//    var isError by remember { mutableStateOf(false) }
//    val coroutineScope = rememberCoroutineScope()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Search Courses") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Search input field
//            TextField(
//                value = searchQuery,
//                onValueChange = { searchQuery = it },
//                label = { Text("Search for a course") },
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Search
//                ),
//                keyboardActions = KeyboardActions(
//                    onSearch = {
//                        if (searchQuery.isNotEmpty()) {
//                            isLoading = true
//                            isError = false
//                            coroutineScope.launch {
//                                // Firestore query to search courses
//                                db.collection("courses")
//                                    .whereGreaterThanOrEqualTo("courseName", searchQuery)
//                                    .get()
//                                    .addOnSuccessListener { result ->
//                                        courses.clear()
//                                        for (document in result) {
//                                            val course = document.toObject(Course::class.java)
//                                            courses.add(course)
//                                        }
//                                        isLoading = false
//                                    }
//                                    .addOnFailureListener {
//                                        isError = true
//                                        isLoading = false
//                                    }
//                            }
//                        }
//                    }
//                ),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (isLoading) {
//                CircularProgressIndicator()
//            } else if (isError) {
//                Text("Error occurred while fetching courses", color = MaterialTheme.colorScheme.error)
//            } else if (courses.isEmpty()) {
//                Text("No results found", fontSize = 16.sp)
//            } else {
//                // Display the search results
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    courses.forEach { course ->
//                        CourseCard(course = course, navController = navController)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun CourseCard(course: Course, navController: NavHostController) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .clickable {
//                // Navigate to the SearchCourseDetailScreen with URL-encoded parameters
//                val encodedCourseName = Uri.encode(course.courseName)
//                val encodedCourseDescription = Uri.encode(course.courseDescription)
//                val encodedImageUrl = Uri.encode(course.imageUrl)
//
//                navController.navigate(
//                    "search_course_detail/$encodedCourseName/$encodedCourseDescription/$encodedImageUrl"
//                )
//            },
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(text = course.courseName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(text = course.courseDescription.take(50) + "...", fontSize = 14.sp)
//        }
//    }
//}
//
//


