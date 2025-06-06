package com.example.project_sy43.ui.theme.main_screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    navController: NavController,
    conversationId: String
    // Potentially add ConversationViewModel: ConversationViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat - ID: $conversationId") }) // Placeholder title
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Displaying conversation for ID: $conversationId\n(Implementation pending)")
            // Here you will implement the message list, input field, etc.
        }
    }
}