package com.example.teachease.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore

import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = Firebase.firestore // Initialize Firestore



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "TeachEase",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Email input
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Password input
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = {
                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = Firebase.auth.currentUser?.uid
                            if (userId != null) {
                                // Check if profile exists in Firestore
                                db.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            // Profile exists, navigate to HomeScreen
                                            navController.navigate("home")
                                        } else {
                                            // Profile does not exist, navigate to CreateProfileScreen
                                            navController.navigate("create_profile")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            // Sign in failed, handle error
                            val exception = task.exception
                            handleSignInError(exception, context)
                            Log.e("FirebaseAuth", "Login failed", exception)  // Log the error
                        }
                    }
            },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .width(200.dp),
            colors = ButtonDefaults.buttonColors(Color.Blue)
        ) {
            Text(text = "Log in", color = Color.White)
        }


        // Forgot Password button
        Text(
            text = "Forgot Password?",
            color = Color.Blue,
            modifier = Modifier
                .clickable { /* Handle forgot password click */
                    navController.navigate("forgot_password")  // Navigate to ForgetPasswordScreen

                }
                .padding(top = 16.dp)
        )

        // "Sign up" Button
        Spacer(modifier = Modifier.height(24.dp)) // Add space between forgot password and sign up button
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Don’t have an account?", color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Sign up here",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController.navigate("signup")  // Navigate to SignUpScreen
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    val navController = rememberNavController()  // Use a fake NavController for preview
    SignInScreen(navController)  // Pass the fake NavController
}


// Function to handle Firebase Sign-In errors
fun handleSignInError(exception: Exception?, context: android.content.Context) {
    when (exception) {
        is FirebaseAuthException -> {
            when (exception.errorCode) {
                "ERROR_INVALID_EMAIL" -> {
                    Toast.makeText(context, "Invalid email format!", Toast.LENGTH_SHORT).show()
                }
                "ERROR_WRONG_PASSWORD" -> {
                    Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT).show()
                }
                "ERROR_USER_NOT_FOUND" -> {
                    Toast.makeText(context, "No user found with this email!", Toast.LENGTH_SHORT).show()
                }
                "ERROR_USER_DISABLED" -> {
                    Toast.makeText(context, "User account is disabled!", Toast.LENGTH_SHORT).show()
                }
                "ERROR_NETWORK_REQUEST_FAILED" -> {
                    Toast.makeText(context, "Network error, please try again!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Login failed: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else -> {
            Toast.makeText(context, "Login failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}