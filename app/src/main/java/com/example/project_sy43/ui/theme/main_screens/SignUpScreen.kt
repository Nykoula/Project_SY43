package com.example.project_sy43.ui.theme.main_screens

import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    CompositionLocalProvider(LocalConfiguration provides LocalConfiguration.current.apply {
        setLocale(Locale.FRANCE)
    }) {

        var lastName by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var showDatePickerDialog by remember { mutableStateOf(false) }
        var age by remember { mutableStateOf(0) }
        //var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
        //var dateOfBirth = selectedDateMillis?.let { convertMillisToDate(it) } ?: ""

        var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
        val dateOfBirth = selectedDateMillis?.let {
            val calendar = Calendar.getInstance(Locale.FRANCE).apply { timeInMillis = it }
            String.format(
                "%02d/%02d/%d" ,
                calendar.get(Calendar.DAY_OF_MONTH) ,
                calendar.get(Calendar.MONTH) + 1 ,
                calendar.get(Calendar.YEAR)
            )
        } ?: ""

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        val calendar = Calendar.getInstance(Locale.FRANCE) // Récupère la date et l'heure actuelles
        val dateFormat = SimpleDateFormat("dd-MM-yyyy") // Format désiré : "19-04-2025"
        val currentDate = dateFormat.format(calendar.time) // Convertit en chaîne formatée
        var isPasswordVisible by remember { mutableStateOf(false) } // État pour basculer visibilité

        // États pour les erreurs
        var firstNameError by remember { mutableStateOf("") }
        var lastNameError by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf("") }
        var passwordError by remember { mutableStateOf("") }
        var addressError by remember { mutableStateOf("") }
        var phoneNumberError by remember { mutableStateOf("") }
        var dateOfBirthError by remember { mutableStateOf("") }

        //recalcule l'age des que la date d'anniversaire change
        LaunchedEffect(selectedDateMillis) {
            age = selectedDateMillis?.let { calculateAge(it) } ?: 0
        }

        // Fonction de validation
        fun validateFields(): Boolean {
            var isValid = true

            // Reset des erreurs
            firstNameError = ""
            lastNameError = ""
            emailError = ""
            passwordError = ""
            addressError = ""
            phoneNumberError = ""
            dateOfBirthError = ""

            // Validation du prénom
            if (firstName.trim().isEmpty()) {
                firstNameError = "Le prénom est requis"
                isValid = false
            } else if (firstName.trim().length < 2) {
                firstNameError = "Le prénom doit contenir au moins 2 caractères"
                isValid = false
            }

            // Validation du nom
            if (lastName.trim().isEmpty()) {
                lastNameError = "Le nom est requis"
                isValid = false
            } else if (lastName.trim().length < 2) {
                lastNameError = "Le nom doit contenir au moins 2 caractères"
                isValid = false
            }

            // Validation de l'email
            if (email.trim().isEmpty()) {
                emailError = "L'email est requis"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                emailError = "Format d'email invalide"
                isValid = false
            }

            // Validation du mdp
            if (password.trim().isEmpty()) {
                passwordError = "Le mot de passe est requis"
                isValid = false
            } else if (password.trim().length < 6) {
                passwordError = "Le mot de passe doit contenir au moins 6 caractères"
                isValid = false
            }

            // Validation de l'adresse
            if (address.trim().isEmpty()) {
                addressError = "L'adresse est requise"
                isValid = false
            } else if (address.trim().length < 5) {
                addressError = "L'adresse doit contenir au moins 5 caractères"
                isValid = false
            }

            // Validation du téléphone
            if (phoneNumber.trim().isEmpty()) {
                phoneNumberError = "Le numéro de téléphone est requis"
                isValid = false
            } else if (!phoneNumber.trim().matches(Regex("^[0-9+\\-\\s()]{10,15}$"))) {
                phoneNumberError = "Format de téléphone invalide"
                isValid = false
            }

            return isValid
        }

        val context = LocalContext.current
        val authentification = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        println("Firebase connecté avec succès : ${authentification.app?.name}")

        Scaffold(
            modifier = Modifier.fillMaxSize() ,
            containerColor = Color.White ,
            //VintedTopBar("Sign Up",navController,)
            topBar = {
                VintedTopBar("Sign Up" , navController , true)
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(top = 16.dp , start = 16.dp , end = 16.dp) ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nom
                CreateField(
                    value = lastName ,
                    onValueChange = {
                        lastName = it
                        lastNameError = ""
                    } ,
                    label = "Last Name" ,
                    placeholder = "Last Name" ,
                    icon = Icons.Outlined.Person ,
                    keyboardType = KeyboardType.Text ,
                    error = lastNameError
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Prénom
                CreateField(
                    value = firstName ,
                    onValueChange = {
                        firstName = it
                        firstNameError = ""
                    } ,
                    label = "First Name" ,
                    placeholder = "First Name" ,
                    icon = Icons.Outlined.Person ,
                    keyboardType = KeyboardType.Text ,
                    error = firstNameError
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Date de naissance
                OutlinedTextField(
                    value = dateOfBirth ,
                    onValueChange = { } ,
                    label = { Text("Date Of Birth") } ,
                    placeholder = { Text("jj/mm/yyyy") } ,
                    readOnly = true ,
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange ,
                                contentDescription = "Select date of birth" ,
                                tint = Color(0xFF007782)
                            )
                        }
                    } ,
                    modifier = Modifier.fillMaxWidth() ,
                    isError = dateOfBirthError.isNotEmpty() ,
                    supportingText = {
                        if (dateOfBirthError.isNotEmpty()) {
                            Text(
                                text = dateOfBirthError ,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                if (showDatePickerDialog) {
                    DatePickerModal(
                        onDateSelected = {
                            selectedDateMillis = it
                        } ,
                        onDismiss = { showDatePickerDialog = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(dateOfBirth)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Age: $age")
                Spacer(modifier = Modifier.height(16.dp))

                // Email
                CreateField(
                    value = email ,
                    onValueChange = {
                        email = it
                        emailError = ""
                    } ,
                    label = "Email" ,
                    placeholder = "Email" ,
                    icon = Icons.Default.Email ,
                    keyboardType = KeyboardType.Email ,
                    error = emailError
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Mot de passe
                OutlinedTextField(
                    value = password ,
                    onValueChange = { password = it } ,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock ,
                            contentDescription = "Password Icon" ,
                            tint = Color(0xFF007782)
                        )
                    } ,
                    placeholder = { Text("Password") } ,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                isPasswordVisible = !isPasswordVisible
                            } // Bascule visibilité
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff ,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password" ,
                                tint = Color.Gray
                            )
                        }
                    } ,
                    modifier = Modifier
                        .fillMaxWidth() ,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) ,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation() , // Texte masqué ou non
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Numéro de téléphone
                CreateField(
                    value = phoneNumber ,
                    onValueChange = {
                        phoneNumber = it
                        phoneNumberError = ""
                    } ,
                    label = "Phone Number" ,
                    placeholder = "Phone Number" ,
                    icon = Icons.Outlined.Phone ,
                    keyboardType = KeyboardType.Phone ,
                    error = phoneNumberError
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Addresse
                CreateField(
                    value = address ,
                    onValueChange = {
                        address = it
                        addressError = ""
                    } ,
                    label = "Address" ,
                    placeholder = "Address" ,
                    icon = Icons.Outlined.Home ,
                    keyboardType = KeyboardType.Text ,
                    error = addressError
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!validateFields()) return@Button

                        authentification.createUserWithEmailAndPassword(email , password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = authentification.currentUser?.uid
                                        ?: return@addOnCompleteListener
                                    val user = mapOf(
                                        "idPerson" to userId ,
                                        "lastName" to lastName ,
                                        "firstName" to firstName ,
                                        "age" to age ,
                                        "email" to email ,
                                        "phoneNumber" to phoneNumber ,
                                        "address" to address ,
                                        "dateCreation" to currentDate ,
                                        "dateOfBirth" to dateOfBirth
                                    )

                                    db.collection("Person").document(userId)
                                        .set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context ,
                                                "Utilisateur enregistré !" ,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.navigate("login") {
                                                popUpTo("signup") { inclusive = true }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context ,
                                                "Erreur enregistrement" ,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                } else {
                                    Toast.makeText(
                                        context ,
                                        "Erreur : ${task.exception?.message}" ,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007782) ,
                        contentColor = Color.White
                    ) ,
                    shape = RoundedCornerShape(16.dp) ,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }
            }
        }
    }
}

@Composable
fun CreateField(
    value: String ,
    onValueChange: (String) -> Unit ,
    label: String ,
    placeholder: String ,
    icon: ImageVector ,
    keyboardType: KeyboardType = KeyboardType.Text ,
    error: String = ""
) {
    Column {
        OutlinedTextField(
            value = value ,
            onValueChange = onValueChange ,
            label = { Text(label) } ,
            modifier = Modifier.fillMaxWidth() ,
            placeholder = { Text(placeholder) } ,
            leadingIcon = {
                Icon(
                    imageVector = icon ,
                    contentDescription = label ,
                    tint = Color(0xFF007782)
                )
            } ,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType) ,
            isError = error.isNotEmpty()
        )

        if (error.isNotEmpty()) {
            Text(
                text = error ,
                color = MaterialTheme.colorScheme.error ,
                style = MaterialTheme.typography.bodySmall ,
                modifier = Modifier.padding(start = 16.dp , top = 4.dp)
            )
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/mm/yyyy" , Locale.FRANCE)
    return formatter.format(Date(millis))
}

fun calculateAge(birthDateMillis: Long): Int {
    val dateOfBirth = Calendar.getInstance(Locale.FRANCE).apply { timeInMillis = birthDateMillis }
    val today = Calendar.getInstance(Locale.FRANCE)

    var age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR)

    // Si la date d'anniversaire n'est pas encore passée cette année, on réduit d'un an
    if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit ,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val localeContext = remember(context) {
        ContextWrapper(context).apply {
            val config = resources.configuration
            config.setLocale(Locale.FRANCE)
            createConfigurationContext(config)
        }
    }

    val datePickerState = rememberDatePickerState()

    // Ici on utilise le context avec locale FR en composant la boîte
    CompositionLocalProvider(LocalContext provides localeContext) {
        DatePickerDialog(
            onDismissRequest = onDismiss ,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text("OK")
                }
            } ,
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}