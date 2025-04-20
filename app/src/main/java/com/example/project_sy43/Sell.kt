package com.example.project_sy43

import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
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
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.getValue
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults


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

    @Composable
    fun SellScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
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
                        val uri =
                            generateUniqueUri(context) // Générer une URI unique pour chaque photo
                        launcher.launch(uri)
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
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "add pictures",
                            tint = Color(0xFF007782), // Sets icon color
                            modifier = Modifier.size(24.dp) // Sets icon size
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add pictures"
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                //InputFields()
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Title"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BasicTextField(
                        value = title,
                        onValueChange = { newText -> title = newText }, // Met à jour le texte
                        decorationBox = { innerTextField ->
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp) // Espacement entre le texte et la ligne
                                ) {
                                    if (title.isEmpty()) {
                                        Text(
                                            text = "ex: Blue T-shirt", // Placeholder visible
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

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Description"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BasicTextField(
                        value = title,
                        onValueChange = { newText -> title = newText }, // Met à jour le texte
                        decorationBox = { innerTextField ->
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp) // Espacement entre le texte et la ligne
                                ) {
                                    if (title.isEmpty()) {
                                        Text(
                                            text = "ex: worn a few times, true to size", // Placeholder visible
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

                Button(
                    onClick = {

                    }, // Contour bleu de 2 dp
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF007782)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .border(2.dp, Color(0xFF007782), RoundedCornerShape(16.dp))
                ) {

                    Text(
                        text = "Add"
                    )
                }
            }
        }
    }

    /*@Composable
    fun InputFields(modifier: Modifier = Modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Title"
            )
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = title,
                onValueChange = { newText -> title = newText }, // Met à jour le texte
                decorationBox = { innerTextField ->
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp) // Espacement entre le texte et la ligne
                        ) {
                            if (title.isEmpty()) {
                                Text(
                                    text = "ex: Blue T-shirt", // Placeholder visible
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
    }*/

}