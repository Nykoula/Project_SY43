package com.example.project_sy43.ui.theme.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Man
import androidx.compose.material.icons.outlined.Woman
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_sy43.ColorActivity
import com.example.project_sy43.Matieres
import com.example.project_sy43.Size
import androidx.navigation.NavController
import androidx.compose.material3.Scaffold
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_sy43.navigation.VintedScreen
import com.example.project_sy43.ui.theme.components.VintedBottomBar
import com.example.project_sy43.ui.theme.components.VintedTopBar
import com.example.project_sy43.viewmodel.SellViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.io.File

@Composable
fun SellScreen(navController: NavController, sellViewModel: SellViewModel = viewModel()) {

    val context = LocalContext.current

    //pour stocker le résultat de l'utilisateur
    var title by sellViewModel.productTitle
    var description by sellViewModel.productDescription
    var category by sellViewModel.selectedCategory
    var couleurs by sellViewModel.selectedColors
    var matieres by sellViewModel.selectedMaterial
    var size by sellViewModel.selectedSize
    var price by sellViewModel.productPrice
    var state by sellViewModel.selectedState
    var colis by sellViewModel.selectedColis
    //var brand by remember { mutableStateOf("") }
    //var clothes by remember { mutableStateOf("") }

    //état pour chaque menu déroulant
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedState by remember { mutableStateOf(false) }
    var expandedColis by remember { mutableStateOf(false) }
    val photoList = remember { mutableStateListOf<Uri>() }//Uniform Resource Identifier)

    val launcherPicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()//TakePicture() pour ouvrir l'appareil photo
    ) { success ->
        if (success) {
            Log.d("Photo", "Image enregistrée :")
        } else {
            Log.d("Photo", "Échec de la capture de l'image.")
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
                .verticalScroll(rememberScrollState()) // Permet de scroller si le contenu dépasse
                .padding(innerPadding)
                //.padding(top = 80.dp) // padding de la top bar
                .padding(16.dp), // padding local
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Bouton pour ajouter une image
            Button(
                onClick = {
                    val uri = generateUniqueUri(context) // Générer une URI unique pour chaque photo
                    launcherPicture.launch(uri)
                    photoList.add(uri) // Ajouter l'URI à la liste
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF007782)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .border(
                        2.dp,
                        Color(0xFF007782),
                        RoundedCornerShape(16.dp)
                    ) // Contour bleu arrondit de 2 dp
            ) {
                Row {
                    // icône + text du bouton
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "add pictures",

                        tint = Color(0xFF007782), // Sets icon color
                        modifier = Modifier.size(24.dp) // Sets icon size
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add pictures")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            InputFields(
                "Title", "ex: Blue T-shirt",
                value = title, onValueChange = { title = it })
            //A SUPP
            //Text(text = sellViewModel.productTitle.value.ifEmpty { "None" })

            Spacer(modifier = Modifier.width(16.dp))
            InputFields(
                "Description", "ex: worn a few times, true to size",
                value = description, onValueChange = { description = it })
            //A SUPP
            //Text(text = sellViewModel.productDescription.value.ifEmpty { "None" })

            Spacer(modifier = Modifier.width(16.dp))
            InputFields(
                "Price", "0,00 €",
                value = price, onValueChange = { price = it })
            //A SUPP
            //Text(text = sellViewModel.productPrice.value.ifEmpty { "None" })

            Spacer(modifier = Modifier.width(16.dp))

            //boutons de menu déroulant
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                //Bouton catégorie
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
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                category = text // Met à jour la catégorie sélectionnée
                                expandedCategory = false // Ferme le menu
                            }
                        )
                        Divider(thickness = 1.dp, color = Color.Gray)
                    }
                }
            }

            //A SUPP
            //Text(text = sellViewModel.selectedCategory.value.ifEmpty { "None" })

            //bouton état
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
                        .clip(RoundedCornerShape(8.dp)) // pour arrondir et éviter les débordements
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
                        "Bon état" to "Article porté/utilisé quelques fois avec signes d’usure.",
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
                                onClick = { state = titleState },
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

            //A SUPP
            //Text(text = sellViewModel.selectedState.value.ifEmpty { "None" })


            //ligne pour la couleur
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
                //A SUPP
                val colorsText = if (sellViewModel.selectedColors.value.isEmpty()) "None"
                else sellViewModel.selectedColors.value.joinToString(", ")
                Text(text = colorsText)
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
                //Text(text = "Couleurs sélectionnées : ${couleurs.joinToString()}")


            }

            Spacer(modifier = Modifier.width(16.dp))
            Divider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))

            //ligne pour le choix des matières
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        navController.navigate(VintedScreen.Matieres.name)
                    }
            ) {
                Text(text = "Matières")
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
                //A SUPP
                val materialsText = if (sellViewModel.selectedMaterial.value.isEmpty()) "None"
                else sellViewModel.selectedMaterial.value.joinToString(", ")
                Text(text = materialsText)
            }

            Spacer(modifier = Modifier.width(16.dp))
            Divider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))

            //ligne pour la taille
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
                //A SUPP
                Text(text = sellViewModel.selectedSize.value.ifEmpty { "None" }) // Affiche la taille ou "None"
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "Arrow"
                )
            }

            //liste déroulante pour le format du colis
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
                        horizontalArrangement = Arrangement.SpaceBetween, // Répartit les éléments
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(text = if (colis.isNotEmpty()) colis else "Format du colis")
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (expandedColis) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Arrow"
                        )
                    }
                }

                //liste des choix du menu déroulant
                DropdownMenu(
                    expanded = expandedColis,
                    onDismissRequest = { expandedColis = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF007782))
                        .padding(horizontal = 8.dp) // évite le débordement
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
                                .padding(8.dp) // marge intérieure
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
                                onClick = { colis = titleColis },
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

            //A SUPP
            //Text(text = sellViewModel.selectedColis.value.ifEmpty { "None" })

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (sellViewModel.productPhotoUri != null) {
                        uploadPhotoToFirebase(sellViewModel.productPhotoUri!!) { photoUrl ->
                            saveArticleToFirestore(
                                sellViewModel.productTitle.value,
                                sellViewModel.productDescription.value,
                                sellViewModel.productPrice.value,
                                sellViewModel.selectedCategory.value,
                                sellViewModel.selectedState.value,
                                sellViewModel.selectedColors.value.toSet(),
                                sellViewModel.selectedMaterial.value.toSet(),
                                sellViewModel.selectedSize.value,
                                sellViewModel.selectedColis.value,
                                photoUrl
                            )
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

                Text(text = "Add")
            }
        }
    }
}

@Composable
fun InputFields(
    label: String,
    placeholder: String,
    value: String, // La valeur actuelle du champ
    onValueChange: (String) -> Unit
) {// Callback pour mettre à jour la valeur) {
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

fun saveArticleToFirestore(
    title: String, description: String, price: String, category: String,
    state: String, couleurs: Set<String>,matieres: Set<String>,
    size: String, colis: String, photoUrl: String
) {
    val db = Firebase.firestore
    val article = hashMapOf(
        "title" to title,
        "description" to description,
        "price" to price, //"" : nom dans la database
        "category" to category, // to : nom de la variable
        "size" to size,
        "state" to state,
        "color" to couleurs,//color est de type array dans la database
        "material" to matieres,//material est de type array dans la database
        "colis" to colis,
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