package com.example.project_sy43

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Man
import androidx.compose.material.icons.outlined.Woman
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.getValue
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


@OptIn(ExperimentalMaterial3Api::class)
class Sell : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.White,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF007782),
                            titleContentColor = Color.White,
                        ),
                        title = { Text("Sell your item") },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (context is ComponentActivity) {
                                    context.finish()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Go back",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                SellScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }

    fun generateUniqueUri(context: android.content.Context): Uri {
        // 1. Obtenir le répertoire pour les photos
        val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // 2. Créer un fichier avec un nom unique (timestamp pour éviter les doublons)
        val photoFile = File(outputDirectory, "photo_${System.currentTimeMillis()}.jpg")

        // 3. Générer une URI compatible avec l'appareil via FileProvider
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Configurez le provider dans AndroidManifest.xml
            photoFile
        )
    }

    fun uploadPhotoToFirebase(uri: Uri, onUploadSuccess: (String) -> Unit) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val photoRef = storageRef.child("Post/${uri.lastPathSegment}")

        photoRef.putFile(uri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { url ->
                    onUploadSuccess(url.toString()) // URL publique de la photo
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseUpload", "Échec du téléversement : $exception")
            }
    }

    fun saveArticleToFirestore(title: String, description: String, price: String, photoUrl: String)
    {
        val db = Firebase.firestore
        val article = hashMapOf(
            "title" to title,
            "description" to description,
            "price" to price,
            "photos" to photoUrl
        )

        db.collection("Post")
            .add(article)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Article enregistré avec ID : ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erreur lors de l'enregistrement : $exception")
            }
    }

    @Composable
    fun SellScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Category") }
        var size by remember { mutableStateOf("Size") }
        var couleur by remember { mutableStateOf(setOf<String>()) }
        var brand by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var clothes by remember { mutableStateOf("") }
        var state by remember { mutableStateOf("State") }

        var expandedCategory by remember { mutableStateOf(false) }
        var expandedSize by remember { mutableStateOf(false) }
        var expandedState by remember { mutableStateOf(false) }
        var expandedColor by remember { mutableStateOf(false) }
        val photoList = remember { mutableStateListOf<Uri>() }//Uniform Resource Identifier)

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()//TakePicture() pour ouvrir l'appareil photo
        ) { success ->
            if (success) {
                Log.d("Photo", "Image enregistrée :")
            } else {
                Log.d("Photo", "Échec de la capture de l'image.")
            }
        }

        val launcherColor = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val selectedColors = data?.getStringArrayListExtra("selectedColors")?.toSet() ?: emptySet() //récupere les couleurs
                couleur = selectedColors // Stocke les couleurs choisies
            } else {
                Log.d("ColorFail", "Échec du choix des couleurs.")
            }
        }

        if (context is ComponentActivity) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()) // Permet de scroller si le contenu dépasse
                    .padding(top = 80.dp) // padding de la top bar
                    .padding(16.dp), // padding local
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val uri = generateUniqueUri(context) // Générer une URI unique pour chaque photo
                        launcher.launch(uri)
                        photoList.add(uri) // Ajouter l'URI à la liste
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF007782)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .border( 2.dp,
                            Color(0xFF007782),
                            RoundedCornerShape(16.dp)
                        ) // Contour bleu arrondit de 2 dp
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "add pictures",
                            tint = Color(0xFF007782), // Sets icon color
                            modifier = Modifier.size(24.dp) // Sets icon size
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text( text = "Add pictures" )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
                InputFields("Title", "ex: Blue T-shirt",
                    value = title, onValueChange = { title = it })

                Spacer(modifier = Modifier.width(16.dp))
                InputFields("Description", "ex: worn a few times, true to size",
                    value = description, onValueChange = { description = it })

                Spacer(modifier = Modifier.width(16.dp))
                InputFields("Prix", "0,00 €",
                    value = price, onValueChange = { price = it })

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Button(onClick = { expandedCategory = !expandedCategory },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007782),
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = category)
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
                            .fillMaxWidth() // Étend le menu déroulant sur toute la largeur du conteneur
                            .background(Color(0xFF007782))
                    ) {

                        listOf(
                            "Woman" to Icons.Outlined.Woman,
                            "Man" to Icons.Outlined.Man,
                            "Children" to Icons.Outlined.ChildCare
                        ).forEach { (text, icon) ->
                            DropdownMenuItem(
                                text = { Text(text = text, color = Color.White) },
                                leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Color.White) },
                                onClick = {
                                    category = text // Met à jour la catégorie sélectionnée
                                    expandedCategory = false // Ferme le menu
                                }
                            )
                            Divider(thickness = 1.dp, color = Color.Gray)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(onClick = { expandedSize = !expandedSize },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007782),
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = size)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (expandedSize) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                                contentDescription = "Arrow"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expandedSize,
                        onDismissRequest = { expandedSize = false },
                        modifier = Modifier
                            .fillMaxWidth() // Étend le menu déroulant sur toute la largeur du conteneur
                            .background(Color(0xFF007782))
                    ) {

                        listOf("XXXS/30/2", "XXS/32/4", "XS/34/6", "S/36/8", "M/38/10", "L/40/12", "XL/42/14", "XXL/44/16", "XXXL/46/18", "4XL/48/20", "5XL/50/22", "6XL/52/24", "7XL/54/26", "8XL/56/28", "9XL/58/30", "Unique size", "other"
                        ).forEach { selectedSize ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    size = selectedSize // Met à jour la sélection
                                    expandedSize = false // Ferme le menu
                                }
                            ){
                                Text(text = selectedSize, color = Color.White)
                                Spacer(modifier = Modifier.weight(1f))
                                RadioButton(
                                    selected = (size == selectedSize),
                                    onClick = { size = selectedSize }
                                )
                            }
                            Divider(thickness = 1.dp, color = Color.Gray)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(onClick = { expandedState = !expandedState },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007782),
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween, // Répartit les éléments
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = state)
                            Spacer(modifier = Modifier.weight(1f))
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
                            .fillMaxWidth() // Étend le menu déroulant sur toute la largeur du conteneur
                            .background(Color(0xFF007782))
                    ) {

                        listOf("Neuf avec étiquette" to "Article neuf, jamais porté/utilisé avec " +
                                "étiquettes ou dans son emballage d'origine.",
                            "Neuf sans étiquette" to "Article neuf, jamais porté/utilisé sans " +
                                    "étiquettes ni emballage d'origine.",
                            "Très bon état" to "Article très peu porté/utilisé qui peut " +
                                    "présenter de légères imperfections, " +
                                    "mais qui reste en très bon état. Précise " +
                                    "avec des photos et une description " +
                                    "détaillée, les défauts de ton article.",
                            "Bon état" to "Article porté/utilisé quelques fois, " +
                                    "présentant des imperfections et des " +
                                    "signes d'usure. Précise avec des " +
                                    "photos et une description détaillée, les " +
                                    "défauts de ton article.",
                            "Satisfaisant" to "Article porté/utilisé plusieurs fois, " +
                                    "présentant des imperfections et des " +
                                    "signes d'usure. Précise avec des " +
                                    "photos et une description détaillée, les " +
                                    "défauts de ton article."
                        ).forEach { (titleState, descriptionState) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    state = titleState // Met à jour la sélection
                                    expandedState = false // Ferme le menu
                                }
                            ){
                                Column (

                                ){
                                    Text(text = titleState,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold)
                                    Text(text = descriptionState,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = Int.MAX_VALUE, // Permet d'avoir autant de lignes que nécessaire
                                        overflow = TextOverflow.Ellipsis) // Assure que le texte passe bien à la ligne)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                RadioButton(
                                    selected = (state == titleState),
                                    onClick = { state = titleState }
                                )
                            }
                            Divider(thickness = 1.dp, color = Color.Gray)
                        }
                    }
                }

                Row {
                    Text(text = "Color")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = "Arrow",
                        modifier = Modifier.clickable {
                            val intentColor = Intent(context, ColorActivity::class.java)
                            launcherColor.launch(intentColor)
                        }
                    )
//                    couleur.forEach{
//                        it ->
//                        Column {
//                            Text(text = it)
//                        }
//                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (photoList.isNotEmpty()) {
                            uploadPhotoToFirebase(photoList.first()) { photoUrl ->
                                saveArticleToFirestore(title, description, price, photoUrl)
                            }
                        }
                    }, // Contour bleu de 2 dp
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF007782)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .border(2.dp, Color(0xFF007782), RoundedCornerShape(16.dp))
                ) {

                    Text( text = "Add" )
                }
            }
        }
    }

    @Composable
    fun InputFields(label: String,
                    placeholder: String,
                    value: String, // La valeur actuelle du champ
                    onValueChange: (String) -> Unit) {// Callback pour mettre à jour la valeur) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label
            )
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange, // Met à jour le texte
                decorationBox = { innerTextField ->
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp) // Espacement entre le texte et la ligne
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder, // Placeholder visible
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField() // Champ de texte réel
                        }
                        Divider( // Ligne grise sous le champ
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                    }
                }
            )
        }
    }
}