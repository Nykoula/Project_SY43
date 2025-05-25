package com.example.project_sy43.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.PersonViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfile(
    personViewModel: PersonViewModel = viewModel(),
    navController: NavController,
    onCancel: () -> Unit
) {
    CompositionLocalProvider(LocalConfiguration provides LocalConfiguration.current.apply {
        setLocale(Locale.FRANCE)
    }) {
        val context = LocalContext.current
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        // États pour les champs modifiables
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var showDatePickerDialog by remember { mutableStateOf(false) }
        var age by remember { mutableStateOf(0) }
        //var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
        //var dateOfBirth = selectedDateMillis?.let { convertMillisToDate(it) } ?: ""
        var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
        var dateOfBirth = selectedDateMillis?.let {
            val calendar = Calendar.getInstance(Locale.FRANCE).apply { timeInMillis = it }
            String.format(
                "%02d/%02d/%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
        } ?: ""
        var address by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }

        // États pour les erreurs
        var firstNameError by remember { mutableStateOf("") }
        var lastNameError by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf("") }
        var ageError by remember { mutableStateOf("") }
        var addressError by remember { mutableStateOf("") }
        var phoneError by remember { mutableStateOf("") }
        var dateOfBirthError by remember { mutableStateOf("") }

        // État pour le bouton de chargement
        var isLoading by remember { mutableStateOf(false) }
        var isDataLoaded by remember { mutableStateOf(false) }

        // Charger les données existantes depuis Firestore
        LaunchedEffect(userId) {
            userId?.let { uid ->
                db.collection("Person").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            firstName = document.getString("firstName") ?: ""
                            lastName = document.getString("lastName") ?: ""
                            email = document.getString("email") ?: currentUser?.email ?: ""
                            age = document.getLong("age")?.toInt() ?: 0
                            address = document.getString("address") ?: ""
                            phoneNumber = document.getString("phoneNumber") ?: ""
                            dateOfBirth = document.getString("dateOfBirth") ?: ""
                            isDataLoaded = true
                        }
                    }
                    .addOnFailureListener {
                        // Fallback sur les données de Auth si disponibles
                        email = currentUser?.email ?: ""
                        firstName = personViewModel.person?.firstName ?: ""
                        lastName = personViewModel.person?.lastName ?: ""
                        isDataLoaded = true
                    }
            }
        }

        //recalcule l'age des que la date d'anniversaire change
        LaunchedEffect(selectedDateMillis) {
            age = selectedDateMillis?.let { calculateAgeWithDateOfBirth(it) } ?: 0
        }

        // Fonction de validation
        fun validateFields(): Boolean {
            var isValid = true

            // Reset des erreurs
            firstNameError = ""
            lastNameError = ""
            emailError = ""
            ageError = ""
            addressError = ""
            phoneError = ""
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
                phoneError = "Le numéro de téléphone est requis"
                isValid = false
            } else if (!phoneNumber.trim().matches(Regex("^[0-9+\\-\\s()]{10,15}$"))) {
                phoneError = "Format de téléphone invalide"
                isValid = false
            }

            return isValid
        }

        // Fonction pour sauvegarder les modifications
        fun saveProfile() {
            if (!validateFields()) return

            isLoading = true

            userId?.let { uid ->
                val updatedData = hashMapOf(
                    "firstName" to firstName.trim(),
                    "lastName" to lastName.trim(),
                    "email" to email.trim(),
                    "age" to age,
                    "address" to address.trim(),
                    "phoneNumber" to phoneNumber.trim(),
                    "dateOfBirth" to dateOfBirth.trim()
                )

                db.collection("Person").document(uid)
                    .update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        isLoading = false
                        Toast.makeText(context, "Profil mis à jour avec succès", Toast.LENGTH_LONG)
                            .show()
                        navController.popBackStack() // Retourner à l'écran profil
                    }
                    .addOnFailureListener { exception ->
                        isLoading = false
                        Toast.makeText(
                            context,
                            "Erreur lors de la mise à jour: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                VintedTopBar(title = "Modifier le profil", navController, true)
            },
            bottomBar = {
                VintedBottomBar(navController, VintedScreen.Profile)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                if (!isDataLoaded) {
                    // Indicateur de chargement
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Chargement des données...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    // Formulaire de modification
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Text(
                                text = "Informations personnelles",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007782)
                            )

                            // Prénom
                            EditableField(
                                label = "Prénom",
                                value = firstName,
                                onValueChange = {
                                    firstName = it
                                    firstNameError = ""
                                },
                                icon = Icons.Outlined.Person,
                                error = firstNameError,
                                keyboardType = KeyboardType.Text
                            )

                            // Nom
                            EditableField(
                                label = "Nom",
                                value = lastName,
                                onValueChange = {
                                    lastName = it
                                    lastNameError = ""
                                },
                                icon = Icons.Outlined.Person,
                                error = lastNameError,
                                keyboardType = KeyboardType.Text
                            )

                            // Email
                            EditableField(
                                label = "Email",
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = ""
                                },
                                icon = Icons.Outlined.Email,
                                error = emailError,
                                keyboardType = KeyboardType.Email
                            )

                            // Date de naissance
                            OutlinedTextField(
                                value = dateOfBirth,
                                onValueChange = { },
                                label = { Text("Date Of Birth") },
                                placeholder = { Text("jj/mm/yyyy") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePickerDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select date of birth",
                                            tint = Color(0xFF007782)
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isError = dateOfBirthError.isNotEmpty(),
                                supportingText = {
                                    if (dateOfBirthError.isNotEmpty()) {
                                        Text(
                                            text = dateOfBirthError,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            )

                            if (showDatePickerDialog) {
                                DatePickerModal(
                                    onDateSelected = {
                                        selectedDateMillis = it
                                    },
                                    onDismiss = { showDatePickerDialog = false }
                                )
                            }

                            Text(dateOfBirth)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Age: $age")

                            /*
                            OutlinedTextField(
                                value = dateOfBirth,
                                onValueChange = { },
                                label = { Text("Date Of Birth") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select date of birth",
                                            tint = Color(0xFF007782)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                            )

                            if (showDatePicker) {
                                Popup(
                                    onDismissRequest = { showDatePicker = false },
                                    alignment = Alignment.TopStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(y = 64.dp)
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(16.dp)
                                    ) {
                                        DatePicker(
                                            state = datePickerState,
                                            showModeToggle = false
                                        )
                                    }
                                }
                            }*/

                            // Âge
                            /*EditableField(
                                label = "Âge",
                                value = age.toString(),
                                onValueChange = {
                                    val newAge = it.toIntOrNull()
                                    if (newAge != null) {
                                        age = newAge
                                    } else {
                                        ageError = "Âge invalide"
                                    }
                                    ageError = ""
                                },
                                icon = Icons.Outlined.Cake,
                                error = ageError,
                                keyboardType = KeyboardType.Number
                            )*/

                            // Adresse
                            EditableField(
                                label = "Adresse",
                                value = address,
                                onValueChange = {
                                    address = it
                                    addressError = ""
                                },
                                icon = Icons.Outlined.Home,
                                error = addressError,
                                keyboardType = KeyboardType.Text
                            )

                            // Téléphone
                            EditableField(
                                label = "Numéro de téléphone",
                                value = phoneNumber,
                                onValueChange = {
                                    phoneNumber = it
                                    phoneError = ""
                                },
                                icon = Icons.Outlined.Phone,
                                error = phoneError,
                                keyboardType = KeyboardType.Phone
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Bouton de sauvegarde
                            Button(
                                onClick = { saveProfile() },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF007782),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isLoading) "Sauvegarde en cours..." else "Sauvegarder les modifications",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    error: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF007782)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            isError = error.isNotEmpty(),
            singleLine = true //forcer le champ a rester sur une seule ligne
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

fun convertMillisToDateOfBirth(millis: Long): String {
    val formatter = SimpleDateFormat("dd/mm/yyyy", Locale.FRANCE)
    return formatter.format(Date(millis))
}

fun calculateAgeWithDateOfBirth(birthDateMillis: Long): Int {
    val dateOfBirth = Calendar.getInstance(Locale.FRANCE).apply { timeInMillis = birthDateMillis }
    val today = Calendar.getInstance(Locale.FRANCE)

    var age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR)

    // Si la date d'anniversaire n'est pas encore passée cette année, on réduit d'un an
    if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}