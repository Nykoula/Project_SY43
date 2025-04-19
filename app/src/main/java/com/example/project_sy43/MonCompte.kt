package com.example.project_sy43

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

class MonCompte : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.White,
                bottomBar = {
                    BottomAppBar(
                        //série d'icône placé à gauche
                        actions = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ){
                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Filled.Home,
                                        contentDescription = "Localized description",
                                        tint = Color(0xFF007782)
                                    )
                                }

                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = "Localized description"
                                    )
                                }

                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Filled.AddCircleOutline,
                                        contentDescription = "Localized description"
                                    )
                                }

                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Filled.MailOutline,
                                        contentDescription = "Localized description"
                                    )
                                }

                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Filled.PersonOutline,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        }
                    )
                }
                /*topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF007782),
                            titleContentColor = Color.White,
                        ),
                        title = { Text("Sign Up") },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (context is ComponentActivity) {
                                    context.finish()
                                }
                            }) {
                                Icon(
                                    //imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Go back",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }*/
            ) { innerPadding ->
                MonCompteScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }

    @Composable
    fun MonCompteScreen(modifier: Modifier = Modifier) {

    }
}