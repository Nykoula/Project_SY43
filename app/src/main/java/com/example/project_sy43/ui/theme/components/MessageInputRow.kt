package com.example.project_sy43.ui.theme.components

import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import androidx.wear.compose.foundation.weight
import androidx. compose. material. icons. Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

@OptIn(ExperimentalMaterial3Api::class) // For TextField and IconButton from Material 3
@Composable
fun MessageInputRow(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    modifier: Modifier = Modifier // Allow passing a modifier from the call site
) {
    Surface( // Adds a background and elevation, good for input areas
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(), // Handle inset for navigation bar if needed
        shadowElevation = 4.dp // Optional: add some shadow
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                //colors = TextFieldDefaults.outlinedTextFieldColors(
                //    focusedBorderColor = MaterialTheme.colorScheme.primary,
                //    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                //),
                maxLines = 5
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendClicked,
                enabled = text.isNotBlank(), // Disable button if text is empty
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send, // Use AutoMirrored for LTR/RTL support
                    contentDescription = "Send message"
                )
            }
        }
    }
}