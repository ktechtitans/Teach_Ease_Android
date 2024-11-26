package com.example.teachease.ui.screens

import android.R
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Category(
    val title: String = "",
    val courses: List<Course> = emptyList()
)

data class Course(
    val courseName: String = "",
    val courseDescription: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val db = Firebase.firestore
    val categories = remember { mutableStateListOf<Category>() }
    val scope = rememberCoroutineScope()

    // Fetch categories and their courses from Firestore
    LaunchedEffect(Unit) {
        scope.launch {
            categories.clear()
            try {
                val categoryDocuments = db.collection("categories").get().await().documents
                Log.d("FirestoreData", "Categories found: ${categoryDocuments.size}")

                val fetchedCategories = categoryDocuments.map { document ->
                    scope.async {
                        val title = document.getString("title") ?: "Untitled Category"
                        val categoryId = document.id
                        Log.d("FirestoreData", "Fetching courses for category: $title (ID: $categoryId)")

                        // Fetch courses in each category
                        val courseList = db.collection("categories").document(categoryId)
                            .collection("courses")
                            .get()
                            .await()
                            .mapNotNull { courseDocument ->
                                try {
                                    Course(
                                        courseName = courseDocument.getString("courseName") ?: "Unnamed Course",
                                        courseDescription = courseDocument.getString("courseDescription") ?: "",
                                        imageUrl = courseDocument.getString("imageUrl") ?: ""
                                    )
                                } catch (e: Exception) {
                                    Log.e("FirestoreData", "Error parsing course: ${e.message}")
                                    null
                                }
                            }

                        Category(title, courseList)
                    }
                }

                // Await all category fetching operations to complete
                categories.addAll(fetchedCategories.awaitAll())
                Log.d("FirestoreData", "Total categories loaded: ${categories.size}")

            } catch (e: Exception) {
                Log.e("FirestoreData", "Error fetching categories or courses: ${e.message}")
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // TeachEase Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TeachEase",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Welcome!",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

//            // Static Image Card (Featured Course)
//            item {
//                Card(
//                    shape = RoundedCornerShape(16.dp),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(180.dp)
//                        .padding(bottom = 16.dp),
//                    elevation = CardDefaults.cardElevation(8.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_menu_camera),  // Placeholder image
//                        contentDescription = "Featured Course",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.Bottom
//                    ) {
//                        Text(
//                            text = "Swift Programming",
//                            fontSize = 22.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White
//                        )
//                        Text(
//                            text = "Master iOS development with Swift.",
//                            fontSize = 14.sp,
//                            color = Color.White
//                        )
//                    }
//                }
//            }



            // Display categories and their courses
            items(categories.size) { index ->
                val category = categories[index]

                // Display category title
                Text(
                    text = category.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Display courses for the category in a LazyRow
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(category.courses.size) { courseIndex ->
                        val course = category.courses[courseIndex]
                        CategoryCard(
                            course = course,
                            onClick = {
                                navController.navigate(
                                    "course_detail/${Uri.encode(course.courseName)}/${Uri.encode(course.courseDescription)}/${Uri.encode(course.imageUrl)}"
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Add space between categories
            }
        }
    }
}

@Composable
fun CategoryCard(course: Course, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (course.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = course.imageUrl),
                    contentDescription = course.courseName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu_report_image),
                    contentDescription = "Error loading image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = course.courseName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = course.courseDescription.take(50),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier.height(60.dp),
        containerColor = Color.White
    ) {
        val selectedItem = remember { mutableStateOf(0) }

        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            selected = selectedItem.value == 0,
            onClick = { selectedItem.value = 0; navController.navigate("home") }
        )

        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            selected = selectedItem.value == 1,
            onClick = { selectedItem.value = 1; navController.navigate("search") }
        )

        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            selected = selectedItem.value == 2,
            onClick = { selectedItem.value = 2; navController.navigate("profile") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}








//package com.example.teachease.ui.screens
//
//import android.net.Uri
//import android.R
//import android.util.Log
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import coil.compose.rememberAsyncImagePainter
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
//import com.google.firebase.firestore.PropertyName
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.firestore.ktx.toObject
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.async
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//// Data classes for categories and courses
//data class Category(
//    val title: String = "",
//    val courses: List<Course> = emptyList()
//)
//
//data class Course(
//    @PropertyName("courseName") val courseName: String = "",
//    @PropertyName("courseDescription") val courseDescription: String = "",
//    @PropertyName("imageUrl") val imageUrl: String = ""
//)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(navController: NavHostController) {
//    val db = Firebase.firestore
//    val categories = remember { mutableStateListOf<Category>() }
//    val scope = rememberCoroutineScope()
//
//    // Fetch categories and their courses from Firestore
//    LaunchedEffect(Unit) {
//        scope.launch {
//            categories.clear()  // Clear the list before loading data
//
//            try {
//                // Fetch all categories
//                val categoryDocuments = db.collection("categories").get().await().documents
//                Log.d("FirestoreData", "Categories found: ${categoryDocuments.size}")
//
//                val fetchedCategories = categoryDocuments.map { document ->
//                    scope.async {
//                        val title = document.getString("title") ?: "Untitled Category"
//                        val categoryId = document.id
//                        Log.d("FirestoreData", "Fetching courses for category: $title (ID: $categoryId)")
//
//                        // Fetch courses in each category
//                        val courseList = db.collection("categories").document(categoryId)
//                            .collection("courses")
//                            .get()
//                            .await()
//                            .mapNotNull { courseDocument ->
//                                try {
//                                    val course = Course(
//                                        courseName = courseDocument.getString("courseName") ?: "Unnamed Course",
//                                        courseDescription = courseDocument.getString("courseDescription") ?: "",
//                                        imageUrl = courseDocument.getString("imageUrl") ?: ""
//                                    )
//                                    Log.d("FirestoreData", "Course fetched: ${course.courseName} (URL: ${course.imageUrl})")
//                                    course
//                                } catch (e: Exception) {
//                                    Log.e("FirestoreData", "Error parsing course: ${e.message}")
//                                    null
//                                }
//                            }
//
//                        Category(title, courseList)
//                    }
//                }
//
//                // Await all category fetching operations to complete
//                categories.addAll(fetchedCategories.awaitAll())
//                Log.d("FirestoreData", "Total categories loaded: ${categories.size}")
//
//            } catch (e: Exception) {
//                Log.e("FirestoreData", "Error fetching categories or courses: ${e.message}")
//            }
//        }
//    }
//
//    Scaffold(
//        bottomBar = { BottomNavBar(navController) }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//        ) {
//            items(categories.size) { index ->
//                val category = categories[index]
//
//                // Display category title
//                Text(
//                    text = category.title,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//
//                // Display courses for the category in a LazyRow
//                LazyRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    items(category.courses.size) { courseIndex ->
//                        val course = category.courses[courseIndex]
//                        CategoryCard(
//                            course = course,
//                            onClick = {
//                                navController.navigate(
//                                    "course_detail/${Uri.encode(course.courseName)}/${Uri.encode(course.courseDescription)}/${Uri.encode(course.imageUrl)}"
//                                )
//                            }
//                        )
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp)) // Add space between categories
//            }
//        }
//    }
//}
//
//@Composable
//fun CategoryCard(course: Course, onClick: () -> Unit) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        modifier = Modifier
//            .width(160.dp)
//            .height(200.dp)
//            .clickable { onClick() },
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // Check if imageUrl is empty
//            if (course.imageUrl.isNotEmpty()) {
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        model = course.imageUrl,
//                        error = painterResource(id = R.drawable.ic_menu_report_image), // Fallback if the URL fails
//                        placeholder = painterResource(id = R.drawable.ic_menu_camera), // Loading placeholder
//                        onError = { error ->
//                            Log.e("ImageLoad", "Failed to load image: ${course.imageUrl}, Error: ${error.result.throwable.message}")
//                        }
//                    ),
//                    contentDescription = course.courseName,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(120.dp)
//                )
//            } else {
//                // Log a message if imageUrl is empty
//                Log.e("ImageLoad", "Empty imageUrl for course: ${course.courseName}")
//                Image(
//                    painter = painterResource(id = R.drawable.ic_menu_report_image),
//                    contentDescription = "Error loading image",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(120.dp)
//                )
//            }
//            Column(
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text(
//                    text = course.courseName,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 4.dp)
//                )
//                Text(
//                    text = course.courseDescription.take(50),
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomNavBar(navController: NavHostController) {
//    NavigationBar(
//        modifier = Modifier.height(60.dp),
//        containerColor = Color.White
//    ) {
//        val selectedItem = remember { mutableStateOf(0) }
//
//        NavigationBarItem(
//            icon = {
//                Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
//            },
//            selected = selectedItem.value == 0,
//            onClick = { selectedItem.value = 0; navController.navigate("home") }
//        )
//
//        NavigationBarItem(
//            icon = {
//                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//            },
//            selected = selectedItem.value == 1,
//            onClick = { selectedItem.value = 1; navController.navigate("search") }
//        )
//
//        NavigationBarItem(
//            icon = {
//                Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
//            },
//            selected = selectedItem.value == 2,
//            onClick = { selectedItem.value = 2; navController.navigate("profile") }
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    val navController = rememberNavController()
//    HomeScreen(navController)
//}
//
//



















//package com.example.teachease.ui.screens
//
//import android.R
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import android.net.Uri
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
//import coil.compose.rememberImagePainter  // Import for Coil image loading
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.launch
//
//// Data class for courses
//data class Course(
//    val courseName: String = "",
//    val courseDescription: String = "",
//    val imageUrl: String = ""  // Store the image URL from Firestore
//)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(navController: NavHostController) {
//    val db = Firebase.firestore
//    val courses = remember { mutableStateListOf<Course>() } // Dynamic list of courses
//    val scope = rememberCoroutineScope() // For fetching Firestore data in coroutine
//
//    // Fetch data from Firestore
//    LaunchedEffect(Unit) {
//        scope.launch {
//            db.collection("courses").get()
//                .addOnSuccessListener { result ->
//                    val newCourses = result.map { document ->
//                        document.toObject(Course::class.java)
//                    }
//                    courses.clear()
//                    courses.addAll(newCourses)  // Update courses list
//                }
//        }
//    }
//
//    Scaffold(
//        bottomBar = { BottomNavBar(navController) }  // Add bottom navigation bar
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//        ) {
//            Spacer(modifier = Modifier.height(20.dp))  // Add space
//
//            // Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "TeachEase",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//                Text(
//                    text = "Welcome!",
//                    fontSize = 18.sp,
//                    color = Color.Gray
//                )
//            }
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Restored Large Image Card (Static)
//            Card(
//                shape = RoundedCornerShape(16.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(180.dp)
//                    .padding(bottom = 16.dp),
//                elevation = CardDefaults.cardElevation(8.dp)
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_menu_camera),  // Static placeholder image
//                    contentDescription = "Featured Course",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.Bottom
//                ) {
//                    Text(
//                        text = "Swift Programming",
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White
//                    )
//                    Text(
//                        text = "Master iOS development with Swift.",
//                        fontSize = 14.sp,
//                        color = Color.White
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Programming Categories Label
//            Text(
//                text = "Programming Courses",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Horizontal Slider (LazyRow) with dynamic course data
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(courses.size) { index ->
//                    val course = courses[index]
//                    CategoryCard(
//                        course = course,
//                        onClick = {
//                            // Navigate to Course Detail Screen
//
//                            navController.navigate(
//                                "course_detail/${Uri.encode(course.courseName)}/${Uri.encode(course.courseDescription)}/${Uri.encode(course.imageUrl)}"
//                            )
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun CategoryCard(course: Course, onClick: () -> Unit) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        modifier = Modifier
//            .width(160.dp)
//            .height(200.dp)
//            .clickable {
//                onClick() },  // Make card clickable
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // Using Coil to load the image from the URL
//            Image(
//                painter = rememberImagePainter(data = course.imageUrl),  // Load image from URL
//                contentDescription = course.courseName,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(120.dp)
//            )
//            Column(
//                modifier = Modifier.padding(8.dp)
//            ) {
//                Text(
//                    text = course.courseName,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 4.dp)
//                )
//                Text(
//                    text = course.courseDescription.take(50),  // Short description
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//            }
//        }
//    }
//}
//
//
//
//@Composable
//fun BottomNavBar(navController: NavHostController) {
//    NavigationBar(
//        modifier = Modifier.height(60.dp),
//        containerColor = Color.White
//    ) {
//        val selectedItem = remember { mutableStateOf(0) }
//
//        // Home button
//        NavigationBarItem(
//            icon = {
//                Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
//            },
//            selected = selectedItem.value == 0,
//            onClick = { selectedItem.value = 0; navController.navigate("home") }
//        )
//
//        // Search button
//        NavigationBarItem(
//            icon = {
//                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//            },
//            selected = selectedItem.value == 1,
//            onClick = {
//                selectedItem.value = 1
//                navController.navigate("search")
//            }
//        )
//
//
//        // Profile button
//        NavigationBarItem(
//            icon = {
//                Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
//            },
//            selected = selectedItem.value == 2,
//            onClick = {
//                selectedItem.value = 2
//                navController.navigate("profile")
//            }
//        )
//
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    val navController = rememberNavController()  // Use a fake NavController for preview
//    HomeScreen(navController)  // Pass the fake NavController
//}
//
//
