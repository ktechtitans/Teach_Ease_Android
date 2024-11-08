import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var languages by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = Firebase.firestore
    val userId = Firebase.auth.currentUser?.uid

    // Calendar instance for DatePicker
    val calendar = Calendar.getInstance()

    // Language dropdown state
    var expanded by remember { mutableStateOf(false) }
    val availableLanguages = listOf("English", "Spanish", "French", "German", "Chinese")
    var selectedLanguage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Create Your Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_camera),
            contentDescription = "Profile Image",
            modifier = Modifier.size(100.dp).padding(bottom = 24.dp)
        )

        // Username Input
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter your username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Date of Birth Input using DatePickerDialog
        TextField(
            value = dob,
            onValueChange = { /* no manual input needed */ },
            label = { Text("Date of Birth") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable {
                    // Show the DatePickerDialog when clicked
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            // Format the date and set it in the TextField
                            val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                            calendar.set(year, month, dayOfMonth)
                            dob = dateFormat.format(calendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Text(text = if (selectedLanguage.isNotEmpty()) selectedLanguage else "Select Language")
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) }, // Correct usage of text parameter
                        onClick = {
                            selectedLanguage = language
                            languages = language  // Assign to state
                            expanded = false
                        }
                    )
                }
            }
        }

        // Bio Input
        TextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(vertical = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Save Profile Button
        Button(
            onClick = {
                if (userId != null) {
                    val userProfile = hashMapOf(
                        "username" to username,
                        "dateOfBirth" to dob,
                        "languages" to languages,
                        "bio" to bio
                    )

                    // Save profile to Firestore
                    db.collection("users").document(userId).set(userProfile)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
                            navController.navigate("home")  // Navigate to Home after saving profile
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(Color.Blue)
        ) {
            Text(text = "Save Profile", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProfileScreenPreview() {
    val navController = rememberNavController()  // Fake NavController for preview
    CreateProfileScreen(navController)
}
