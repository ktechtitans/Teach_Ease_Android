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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

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
        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Create new account",
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
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


        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )


        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
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
                if (password == confirmPassword) {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.e("FirebaseAuth", "Signup failed", task.exception)


                                Toast.makeText(
                                    context,
                                    "Signup successful! Welcome!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                 navController.navigate("create_profile")
                            } else {
                                val errorMessage = task.exception?.localizedMessage ?: "Signup failed"


                                Toast.makeText(
                                    context,
                                    "Signup failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Passwords do not match!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(Color.Blue)
        ) {
            Text(text = "Sign up", color = Color.White)
        }


        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Have an account?", color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Log in",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController.navigate("signin")

                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    val navController = rememberNavController()
    SignupScreen(navController)
}
