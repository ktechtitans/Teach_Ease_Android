package com.example.teachease

import CreateProfileScreen
import SignupScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable  // Make sure this import is here
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.teachease.ui.screen.CourseDetailScreen
import com.example.teachease.ui.screen.ProfileScreen
import com.example.teachease.ui.screen.SearchCourseDetailScreen
import com.example.teachease.ui.screen.SearchScreen
import com.example.teachease.ui.screens.*  // Your screen files
import com.example.teachease.ui.theme.TeachEaseTheme  // Your theme file
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.auth // Initialize Firebase Authentication

        setContent {
            TeachEaseTheme {
                TeachEaseApp()  // Initialize the main navigation host
            }
        }
    }
}

@Composable
fun TeachEaseApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "signin"
    ) {
        // Sign-in Screen
        composable("signin") {
            SignInScreen(navController)
        }

        // Sign-up Screen
        composable("signup") {
            SignupScreen(navController)
        }

        // Forgot Password Screen
        composable("forgot_password") {
            ForgetPasswordScreen(navController)
        }

        // Home Screen
        composable("home") {
            HomeScreen(navController)
        }

        // Profile Screen
        composable("create_profile") {
            CreateProfileScreen(navController)  // Ensure this route is correctly defined
        }
        // Add this route inside your NavHost in MainActivity.kt
        composable(
            "course_detail/{courseName}/{courseDescription}/{imageUrl}",
            arguments = listOf(
                navArgument("courseName") { type = NavType.StringType },
                navArgument("courseDescription") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseName = backStackEntry.arguments?.getString("courseName") ?: ""
            val courseDescription = backStackEntry.arguments?.getString("courseDescription") ?: ""
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

            CourseDetailScreen(
                courseName = courseName,
                courseDescription = courseDescription,
                imageUrl = imageUrl,
                navController = navController
            )
        }

        composable("profile") {
            ProfileScreen(navController)
        }
        composable("search") {
            SearchScreen(navController)
        }

        composable(
            "search_course_detail/{courseName}/{courseDescription}/{imageUrl}",
            arguments = listOf(
                navArgument("courseName") { type = NavType.StringType },
                navArgument("courseDescription") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseName = backStackEntry.arguments?.getString("courseName") ?: ""
            val courseDescription = backStackEntry.arguments?.getString("courseDescription") ?: ""
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

            SearchCourseDetailScreen(courseName, courseDescription, imageUrl, navController)
        }







    }

}