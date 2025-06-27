package com.example.project_sy43.ui.theme.main_screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.PersonViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UpdateProfile(
    personViewModel: PersonViewModel = viewModel() ,
    navController: NavController ,
    onCancel: () -> Unit
) {
    CompositionLocalProvider(LocalConfiguration provides LocalConfiguration.current.apply {
        setLocale(Locale.FRANCE)
    }) {
        val context = LocalContext.current
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
        var dateOfBirth by remember { mutableStateOf("") }
        var age by remember { mutableStateOf(0) }
        var isDataLoaded by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var showDatePickerDialog by remember { mutableStateOf(false) }
        var photoUrl by remember { mutableStateOf("") }
        var localPhotoUri by remember { mutableStateOf<Uri?>(null) }
        var cameraBitmap by remember { mutableStateOf<Bitmap?>(null) }

        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent() ,
            onResult = { uri -> localPhotoUri = uri }
        )

        val cameraPermissionState =
            rememberPermissionState(permission = android.Manifest.permission.CAMERA)
        val readStoragePermissionState =
            rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)

        LaunchedEffect(Unit) {
            if (!cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            }
            if (!readStoragePermissionState.status.isGranted) {
                readStoragePermissionState.launchPermissionRequest()
            }
        }


        LaunchedEffect(userId) {
            userId?.let { uid ->
                db.collection("Person").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            firstName = document.getString("firstName") ?: ""
                            lastName = document.getString("lastName") ?: ""
                            email = document.getString("email") ?: currentUser?.email ?: ""
                            address = document.getString("address") ?: ""
                            phoneNumber = document.getString("phoneNumber") ?: ""
                            dateOfBirth = document.getString("dateOfBirth") ?: ""
                            age = document.getLong("age")?.toInt() ?: 0
                            photoUrl = document.getString("photo") ?: ""
                            isDataLoaded = true
                        }
                    }
            }
        }

        LaunchedEffect(selectedDateMillis) {
            age = selectedDateMillis?.let { calculateAgeWithDateOfBirth(it) } ?: 0
        }

        fun uploadPhotoToStorage(onComplete: (String?) -> Unit) {
            if (localPhotoUri == null && cameraBitmap == null) {
                onComplete(null)
                return
            }
            val storageRef =
                storage.reference.child("profile_photos/${userId}_${System.currentTimeMillis()}.jpg")

            if (localPhotoUri != null) {
                storageRef.putFile(localPhotoUri!!)
                    .continueWithTask { task -> storageRef.downloadUrl }
                    .addOnSuccessListener { uri -> onComplete(uri.toString()) }
                    .addOnFailureListener { onComplete(null) }
            } else if (cameraBitmap != null) {
                val baos = ByteArrayOutputStream()
                cameraBitmap!!.compress(Bitmap.CompressFormat.JPEG , 100 , baos)
                val data = baos.toByteArray()
                storageRef.putBytes(data)
                    .continueWithTask { task -> storageRef.downloadUrl }
                    .addOnSuccessListener { uri -> onComplete(uri.toString()) }
                    .addOnFailureListener { onComplete(null) }
            }
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview() ,
            onResult = { bitmap ->
                if (bitmap != null) {
                    cameraBitmap = bitmap
                    localPhotoUri = null
                }
            }
        )


        fun saveProfile() {
            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || address.isBlank() || phoneNumber.isBlank()) {
                Toast.makeText(context , "Tous les champs doivent être remplis" , Toast.LENGTH_LONG)
                    .show()
                return
            }
            isLoading = true
            uploadPhotoToStorage { uploadedUrl ->
                val photoToSave = uploadedUrl ?: photoUrl
                userId?.let { uid ->
                    val updatedData = mapOf(
                        "firstName" to firstName.trim() ,
                        "lastName" to lastName.trim() ,
                        "email" to email.trim() ,
                        "age" to age ,
                        "address" to address.trim() ,
                        "phoneNumber" to phoneNumber.trim() ,
                        "dateOfBirth" to dateOfBirth.trim() ,
                        "photo" to photoToSave
                    )
                    db.collection("Person").document(uid).update(updatedData)
                        .addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context , "Profil mis à jour" , Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            isLoading = false
                            Toast.makeText(context , "Erreur: ${it.message}" , Toast.LENGTH_LONG)
                                .show()
                        }
                }
            }
        }

        Scaffold(
            topBar = { VintedTopBar("Modifier le profil" , navController , true) } ,
            bottomBar = { VintedBottomBar(navController , VintedScreen.Profile) }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) ,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isDataLoaded) {
                    Text("Chargement des données...")
                    return@Column
                }

                val painter = rememberAsyncImagePainter(
                    model = when {
                        localPhotoUri != null -> localPhotoUri
                        cameraBitmap != null -> null // TODO: optional preview bitmap
                        photoUrl.isNotBlank() -> photoUrl
                        else -> "https://via.placeholder.com/150"
                    }
                )

                Box(modifier = Modifier.fillMaxWidth() , contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painter ,
                            contentDescription = "Photo de profil" ,
                            contentScale = ContentScale.Crop ,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { imagePicker.launch("image/*") }) {
                                Text("Galerie")
                            }
                            Button(onClick = { cameraLauncher.launch(null) }) {
                                Text("Caméra")
                            }

                        }
                    }
                }

                EditableField("Prénom" , firstName , { firstName = it } , Icons.Outlined.Person)
                EditableField("Nom" , lastName , { lastName = it } , Icons.Outlined.Person)
                EditableField(
                    "Email" ,
                    email ,
                    { email = it } ,
                    Icons.Outlined.Email ,
                    keyboardType = KeyboardType.Email
                )
                EditableField("Adresse" , address , { address = it } , Icons.Outlined.Home)
                EditableField(
                    "Téléphone" ,
                    phoneNumber ,
                    { phoneNumber = it } ,
                    Icons.Outlined.Phone ,
                    keyboardType = KeyboardType.Phone
                )

                OutlinedTextField(
                    value = dateOfBirth ,
                    onValueChange = {} ,
                    label = { Text("Date de naissance") } ,
                    readOnly = true ,
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerDialog = true }) {
                            Icon(Icons.Default.DateRange , contentDescription = null)
                        }
                    } ,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showDatePickerDialog) {
                    DatePickerModal(
                        onDateSelected = {
                            selectedDateMillis = it
                            val calendar = Calendar.getInstance(Locale.FRANCE)
                                .apply { timeInMillis = it ?: 0L }
                            dateOfBirth = String.format(
                                "%02d/%02d/%04d" ,
                                calendar.get(Calendar.DAY_OF_MONTH) ,
                                calendar.get(Calendar.MONTH) + 1 ,
                                calendar.get(Calendar.YEAR)
                            )
                        } ,
                        onDismiss = { showDatePickerDialog = false }
                    )
                }

                Text("Âge: $age")

                Button(
                    onClick = { saveProfile() } ,
                    enabled = !isLoading ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007782))
                ) {
                    Text(text = if (isLoading) "Sauvegarde..." else "Sauvegarder")
                }
            }
        }
    }
}

@Composable
fun EditableField(
    label: String ,
    value: String ,
    onValueChange: (String) -> Unit ,
    icon: ImageVector ,
    error: String = "" ,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        OutlinedTextField(
            value = value ,
            onValueChange = onValueChange ,
            label = { Text(label) } ,
            leadingIcon = {
                Icon(icon , contentDescription = label , tint = Color(0xFF007782))
            } ,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType) ,
            modifier = Modifier.fillMaxWidth() ,
            isError = error.isNotEmpty() ,
            singleLine = true
        )
        if (error.isNotEmpty()) {
            Text(
                error ,
                color = MaterialTheme.colorScheme.error ,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun calculateAgeWithDateOfBirth(birthDateMillis: Long): Int {
    val dateOfBirth = Calendar.getInstance(Locale.FRANCE).apply { timeInMillis = birthDateMillis }
    val today = Calendar.getInstance(Locale.FRANCE)
    var age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) age--
    return age
}
