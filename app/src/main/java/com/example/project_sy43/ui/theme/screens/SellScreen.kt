package com.example.project_sy43.ui.theme.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Man
import androidx.compose.material.icons.outlined.Woman
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.SellViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.io.File

@Composable
fun SellScreen(navController: NavController, sellViewModel: SellViewModel = viewModel()) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Pour stocker le résultat de l'utilisateur
    val userId = FirebaseAuth.getInstance().currentUser?.uid //stocker l'id utilisateur
    var title by sellViewModel.productTitle
    var description by sellViewModel.productDescription
    var category by sellViewModel.selectedCategory
    var couleurs by sellViewModel.selectedColors
    var matieres by sellViewModel.selectedMaterial
    var size by sellViewModel.selectedSize
    var price by sellViewModel.productPrice
    var state by sellViewModel.selectedState
    var colis by sellViewModel.selectedColis

    // État pour chaque menu déroulant
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedState by remember { mutableStateOf(false) }
    var expandedColis by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    // Liste pour stocker toutes les photos sélectionnées
    val photoList = remember { mutableStateListOf<Uri>() }

    // État pour gérer l'URI de la photo en cours
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // IMPORTANT: Déclarer launcherCamera AVANT permissionLauncher
    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            Log.d("Photo", "Photo prise avec succès: $currentPhotoUri")
            // Ajouter seulement si pas déjà dans la liste
            if (!photoList.contains(currentPhotoUri!!)) {
                photoList.add(currentPhotoUri!!)
            }
        } else {
            Log.d("Photo", "Échec de la capture de l'image.")
            Toast.makeText(context, "Échec de la capture", Toast.LENGTH_SHORT).show()
        }
        currentPhotoUri = null
    }

    // Launcher pour demander les permissions (APRÈS launcherCamera)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission accordée, prendre la photo
            currentPhotoUri?.let { uri ->
                launcherCamera.launch(uri)
            }
        } else {
            Toast.makeText(context, "Permission appareil photo refusée", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher pour sélectionner une photo depuis la galerie
    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoList.add(it)
            Log.d("Photo", "Photo sélectionnée depuis la galerie: $it")
        }
    }

    fun validateFields(): Boolean {
        return when {
            title.isEmpty() -> {
                errorMessage = "Title is required"
                false
            }
            description.isEmpty() -> {
                errorMessage = "Description is required"
                false
            }
            price.isEmpty() -> {
                errorMessage = "Price is required"
                false
            }
            category.isEmpty() -> {
                errorMessage = "Category is required"
                false
            }
            state.isEmpty() -> {
                errorMessage = "State is required"
                false
            }
            couleurs.isEmpty() -> {
                errorMessage = "Color is required"
                false
            }
            matieres.isEmpty() -> {
                errorMessage = "Material is required"
                false
            }
            size.isEmpty() -> {
                errorMessage = "Size is required"
                false
            }
            colis.isEmpty() -> {
                errorMessage = "Colis is required"
                false
            }
            else -> true
        }
    }

    // Fonction pour prendre une photo
    fun takePhoto() {
        // Vérifier les permissions d'abord
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission déjà accordée
                try {
                    val uri = generateUniqueUri(context)
                    currentPhotoUri = uri
                    launcherCamera.launch(uri)
                } catch (e: Exception) {
                    Log.e("Photo", "Erreur lors de la génération de l'URI: ${e.message}")
                    Toast.makeText(context, "Erreur lors de la préparation de l'appareil photo", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // Demander la permission
                val uri = generateUniqueUri(context)
                currentPhotoUri = uri
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }



    Scaffold(
        topBar = {
            VintedTopBar(title = "Sell your item", navController, true)
        },
        bottomBar = {
            VintedBottomBar(navController, VintedScreen.Sell)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section pour ajouter des photos
            Column {
                // Bouton principal pour ajouter des photos
                Button(
                    onClick = { showPhotoOptions = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF007782)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .border(2.dp, Color(0xFF007782), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "add pictures",
                            tint = Color(0xFF007782),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Add pictures (${photoList.size})")
                    }

                }

                // Menu pour choisir entre appareil photo et galerie
                DropdownMenu(
                    expanded = showPhotoOptions,
                    onDismissRequest = { showPhotoOptions = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Take Photo") },
                        onClick = {
                            showPhotoOptions = false
                            takePhoto()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Choose from Gallery") },
                        onClick = {
                            showPhotoOptions = false
                            launcherGallery.launch("image/*")
                        }
                    )
                }

                // Affichage des photos sélectionnées (miniatures)
                if (photoList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(photoList) { uri ->
                            PhotoThumbnail(
                                uri = uri,
                                onRemove = { photoList.remove(uri) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InputFields(
                "Title", "ex: Blue T-shirt",
                value = title, onValueChange = { title = it })

            Spacer(modifier = Modifier.height(16.dp))

            InputFields(
                "Description", "ex: worn a few times, true to size",
                value = description, onValueChange = { description = it })

            Spacer(modifier = Modifier.height(16.dp))

            InputFields(
                "Price", "0,00 €",
                value = price, onValueChange = { price = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Le reste du code reste identique...
            // [Tous les autres composants restent les mêmes que dans la version précédente]

            // Boutons de menu déroulant
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Bouton catégorie
                Button(
                    onClick = { expandedCategory = !expandedCategory },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007782),
                        contentColor = Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (category.isNotEmpty()) category else "Category")
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (expandedCategory) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Arrow"
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF007782))
                ) {
                    listOf(
                        "Woman" to Icons.Outlined.Woman,
                        "Man" to Icons.Outlined.Man,
                        "Children" to Icons.Outlined.ChildCare
                    ).forEach { (text, icon) ->
                        DropdownMenuItem(
                            text = { Text(text = text, color = Color.White) },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                category = text
                                expandedCategory = false
                            }
                        )
                        Divider(thickness = 1.dp, color = Color.Gray)
                    }
                }
            }

            // Bouton état
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    onClick = { expandedState = !expandedState },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007782),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (state.isNotEmpty()) state else "État")
                        Icon(
                            imageVector = if (expandedState) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Arrow"
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandedState,
                    onDismissRequest = { expandedState = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF007782))
                ) {
                    listOf(
                        "Neuf avec étiquette" to "Article neuf, jamais porté/utilisé avec étiquettes ou dans son emballage d'origine.",
                        "Neuf sans étiquette" to "Article neuf, jamais porté/utilisé sans étiquettes ni emballage d'origine.",
                        "Très bon état" to "Article très peu porté/utilisé avec de légères imperfections.",
                        "Bon état" to "Article porté/utilisé quelques fois avec signes d'usure.",
                        "Satisfaisant" to "Article porté/utilisé plusieurs fois, avec imperfections visibles."
                    ).forEach { (titleState, descriptionState) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    state = titleState
                                    expandedState = false
                                }
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = titleState,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = descriptionState,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = Int.MAX_VALUE,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            RadioButton(
                                selected = (state == titleState),
                                onClick = {
                                    state = titleState
                                    expandedState = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White
                                )
                            )
                        }
                        Divider(thickness = 1.dp, color = Color.Gray)
                    }
                }
            }

            // Ligne pour la couleur
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        navController.navigate(VintedScreen.ColorScreen.name)
                    }
            ) {
                Text(text = "Color")
                Spacer(modifier = Modifier.weight(1f))
                val colorsText = if (sellViewModel.selectedColors.value.isEmpty()) "None"
                else sellViewModel.selectedColors.value.joinToString(", ")
                Text(text = colorsText)
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Ligne pour le choix des matières
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        navController.navigate(VintedScreen.Matieres.name)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Matières")
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(2f)
                ) {
                    val materialsText = if (sellViewModel.selectedMaterial.value.isEmpty()) "None"
                    else sellViewModel.selectedMaterial.value.joinToString(", ")
                    Text(
                        text = materialsText,
                        maxLines = Int.MAX_VALUE,
                        overflow = TextOverflow.Visible
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            Divider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Ligne pour la taille
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        navController.navigate(VintedScreen.Size.name)
                    }
            ) {
                Text(text = "Size")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = sellViewModel.selectedSize.value.ifEmpty { "None" })
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
            }

            // Liste déroulante pour le format du colis
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { expandedColis = !expandedColis },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007782),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (colis.isNotEmpty()) colis else "Format du colis")
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (expandedColis) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Arrow"
                        )
                    }
                }

                // Liste des choix du menu déroulant
                DropdownMenu(
                    expanded = expandedColis,
                    onDismissRequest = { expandedColis = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF007782))
                        .padding(horizontal = 8.dp)
                ) {
                    listOf(
                        "Petit" to "Convient pour un article qui tient dans une grande enveloppe.",
                        "Moyen" to "Convient pour un article qui tient dans une boîte à chaussures.",
                        "Grand" to "Convient pour un article qui tient dans un carton de déménagement."
                    ).forEach { (titleColis, descriptionColis) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    colis = titleColis
                                    expandedColis = false
                                }
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = titleColis,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = descriptionColis,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = Int.MAX_VALUE,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            RadioButton(
                                selected = (colis == titleColis),
                                onClick = {
                                    colis = titleColis
                                    expandedColis = false },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White
                                )
                            )
                        }
                        Divider(thickness = 1.dp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton pour publier l'article
            Button(
                onClick = {
                    if (validateFields()) {
                        if (photoList.isNotEmpty()) {
                            uploadPhotosToFirebase(photoList) { photoUrls ->
                                saveArticleToFirestore(
                                    userId.toString(),
                                    sellViewModel.productTitle.value,
                                    sellViewModel.productDescription.value,
                                    sellViewModel.productPrice.value,
                                    sellViewModel.selectedCategory.value,
                                    sellViewModel.selectedState.value,
                                    sellViewModel.selectedColors.value.toSet(),
                                    sellViewModel.selectedMaterial.value.toSet(),
                                    sellViewModel.selectedSize.value,
                                    sellViewModel.selectedColis.value,
                                    photoUrls
                                )
                                sellViewModel.reset()
                            }
                        } else {
                            // Gérer le cas sans photos
                            saveArticleToFirestore(
                                userId.toString(),
                                sellViewModel.productTitle.value,
                                sellViewModel.productDescription.value,
                                sellViewModel.productPrice.value,
                                sellViewModel.selectedCategory.value,
                                sellViewModel.selectedState.value,
                                sellViewModel.selectedColors.value.toSet(),
                                sellViewModel.selectedMaterial.value.toSet(),
                                sellViewModel.selectedSize.value,
                                sellViewModel.selectedColis.value,
                                emptyList()
                            )
                            sellViewModel.reset()
                        }
                        Toast.makeText(context, "Product added", Toast.LENGTH_LONG).show()
                        navController.navigate(VintedScreen.MonCompte.name)
                    } else {
                        showDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF007782)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .border(2.dp, Color(0xFF007782), RoundedCornerShape(16.dp))
            ) {
                Text(text = "Add")
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Error") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun PhotoThumbnail(
    uri: Uri,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier.size(80.dp)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Selected photo",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        // Bouton pour supprimer la photo
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove photo",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(
                    Color.Red.copy(alpha = 0.7f),
                    CircleShape
                )
                .padding(4.dp)
                .size(16.dp)
                .clickable { onRemove() }
        )
    }
}

@Composable
fun InputFields(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = label)
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = if (label == "Price") {
                KeyboardOptions(keyboardType = KeyboardType.Decimal)
            } else {
                KeyboardOptions.Default
            },
            singleLine = true,
            decorationBox = { innerTextField ->
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerTextField()
                    }
                    Divider(
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        )
    }
}

fun generateUniqueUri(context: Context): Uri {
    val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    if (outputDirectory != null && !outputDirectory.exists()) {
        outputDirectory.mkdirs()
    }
    val photoFile = File.createTempFile(
        "photo_${System.currentTimeMillis()}",
        ".jpg",
        outputDirectory
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        photoFile
    )
}


fun uploadPhotosToFirebase(
    uriList: List<Uri>,
    onUploadSuccess: (List<String>) -> Unit
) {
    val storage = Firebase.storage
    val storageRef = storage.reference
    val uploadedUrls = mutableListOf<String>()
    var uploadCount = 0

    if (uriList.isEmpty()) {
        onUploadSuccess(emptyList())
        return
    }

    uriList.forEach { uri ->
        val photoRef = storageRef.child("Post/${System.currentTimeMillis()}_${uri.lastPathSegment}")
        Log.d("FirebaseUpload", "Uploading URI: $uri")

        photoRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()
                    Log.d("FirebaseUpload", "Uploaded URL: $url")
                    uploadedUrls.add(url)
                    uploadCount++

                    if (uploadCount == uriList.size) {
                        onUploadSuccess(uploadedUrls)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseUpload", "Upload failed: ${exception.message}")
                uploadCount++

                if (uploadCount == uriList.size) {
                    onUploadSuccess(uploadedUrls)
                }
            }
    }
}



fun saveArticleToFirestore(
    userId: String,
    title: String,
    description: String,
    price: String,
    category: String,
    state: String,
    couleurs: Set<String>,
    matieres: Set<String>,
    size: String,
    colis: String,
    photoUrls: List<String>
) {
    Log.d("Firestore", "Photo URLs: $photoUrls")

    val db = Firebase.firestore
    val article = hashMapOf(
        "userId" to userId,
        "title" to title,
        "description" to description,
        "price" to price.toDoubleOrNull(),
        "category" to category,
        "size" to size,
        "state" to state,
        "color" to couleurs.toList(),
        "material" to matieres.toList(),
        "colis" to colis,
        "photos" to photoUrls
    )

    db.collection("Post")
        .add(article)
        .addOnSuccessListener { documentReference ->
            Log.d("Firestore", "Article enregistré avec ID : ${documentReference.id}")
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Erreur lors de l'enregistrement : ${exception.message}")
        }
}

