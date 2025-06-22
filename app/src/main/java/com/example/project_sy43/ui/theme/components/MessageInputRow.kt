package com.example.project_sy43.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputSection(
    currentMessageText: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isSending: Boolean,
    // --- Offer related parameters (optional, can be added later or be separate) ---
    currentOfferPrice: String,
    onOfferPriceChange: (String) -> Unit,
    currentOfferOptionalText: String,
    onOfferOptionalTextChange: (String) -> Unit,
    onSendOffer: () -> Unit,
    // --- Control which input mode is active if they are combined ---
    // isOfferModeActive: Boolean,
    // onToggleOfferMode: () -> Unit
) {
    Surface( // Adds a background and elevation, good for input areas
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            // TODO: Add UI to toggle between text message and offer input if desired
            // For now, let's assume you might have separate buttons or a tabbed approach,
            // or just always show both if the UI is simple.

            // Standard Text Message Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentMessageText,
                    onValueChange = onMessageChange,
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    enabled = !isSending, // Disable while sending
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send // This might trigger onSendMessage directly if you set up keyboard actions
                    ),
                    // If you want the send button to be part of the TextField (trailingIcon)
                    // trailingIcon = {
                    //    IconButton(onClick = onSendMessage, enabled = !isSending && currentMessageText.isNotBlank()) {
                    //        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
                    //    }
                    // }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSendMessage,
                    enabled = !isSending && currentMessageText.isNotBlank()
                ) {
                    if (isSending) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message"
                        )
                    }
                }
            }

            // --- Offer Input Fields (Example: shown below text input) ---
            // You'd typically have some UI element (e.g., a button) to reveal these
            // or switch to an "offer mode".
            // For simplicity, I'll just show them here. You can conditionally render them.

            Spacer(modifier = Modifier.height(8.dp)) // Add some space

            OutlinedTextField(
                value = currentOfferPrice,
                onValueChange = onOfferPriceChange,
                label = { Text("Offer Price ($)") },
                placeholder = { Text("e.g., 25.00") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSending
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = currentOfferOptionalText,
                onValueChange = onOfferOptionalTextChange,
                label = { Text("Optional message for offer") },
                placeholder = { Text("e.g., Condition, reason for offer") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done // Or ImeAction.Send to trigger send
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSending,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSendOffer,
                enabled = !isSending && currentOfferPrice.isNotBlank(), // Basic validation
                modifier = Modifier.align(Alignment.End)
            ) {
                if (isSending) { // You might want a more specific isSendingOffer state
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sending Offer...")
                    }
                } else {
                    Text("Send Offer")
                }
            }
        }
    }
}