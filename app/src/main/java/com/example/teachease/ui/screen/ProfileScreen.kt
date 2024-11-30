package com.example.teachease.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val userId = Firebase.auth.currentUser?.uid

    var username by remember { mutableStateOf("") }
    var languages by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null) {
            // Fetch user profile data from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        username = document.getString("username") ?: ""
                        languages = document.getString("languages") ?: ""
                        bio = document.getString("bio") ?: ""
                        dob = document.getString("dateOfBirth") ?: ""
                        isLoading = false
                    } else {
                        isError = true
                        isLoading = false
                    }
                }
                .addOnFailureListener {
                    isError = true
                    isLoading = false
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // Sign-out function
    fun signOut() {
        Firebase.auth.signOut()
        navController.popBackStack()  // Navigate back to the login screen
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            // Loading Indicator while fetching profile data
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (isError) {
            // Error message if data fetching fails
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Failed to load profile", color = MaterialTheme.colorScheme.error)
            }
        } else {
            // Display user profile data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Add Spacer to create space from the top
                Spacer(modifier = Modifier.height(32.dp))  // Adds space above the first TextField

                // Username TextField
                TextField(
                    value = username,
                    onValueChange = {},
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    textStyle = TextStyle(color = Color.Black) // Text color set to black
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Date of Birth TextField
                TextField(
                    value = dob,
                    onValueChange = {},
                    label = { Text("Date of Birth") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    textStyle = TextStyle(color = Color.Black) // Text color set to black
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Languages TextField
                TextField(
                    value = languages,
                    onValueChange = {},
                    label = { Text("Languages") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    textStyle = TextStyle(color = Color.Black) // Text color set to black
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Bio TextField
                TextField(
                    value = bio,
                    onValueChange = {},
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    textStyle = TextStyle(color = Color.Black) // Text color set to black
                )
                Spacer(modifier = Modifier.height(80.dp))

                // Sign-out Button with custom color
                Button(
                    onClick = { signOut() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue) // Corrected background color for Material3
                ) {
                    Text("Sign Out", color = Color.White) // Button text color set to white
                }
            }
        }
    }
}
