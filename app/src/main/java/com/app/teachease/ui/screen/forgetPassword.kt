package com.example.teachease.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = Firebase.auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "TeachEase",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )


        Text(
            text = "Enter a valid email, you will receive a reset password link",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp),
            lineHeight = 20.sp,
        )


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


        Button(
            onClick = {
                if (email.isNotEmpty()) {

                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Password reset link sent!", Toast.LENGTH_SHORT).show()

                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(Color.Blue)
        ) {
            Text(text = "Submit", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgetPasswordScreenPreview() {
    ForgetPasswordScreen(
        navController = TODO()
    )
}
