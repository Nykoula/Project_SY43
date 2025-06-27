package com.example.project_sy43.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputSection(
    currentMessageText: String ,
    onMessageChange: (String) -> Unit ,
    onSendMessage: () -> Unit ,
    isSending: Boolean ,
    currentOfferPrice: String ,
    onOfferPriceChange: (String) -> Unit ,
    currentOfferOptionalText: String ,
    onOfferOptionalTextChange: (String) -> Unit ,
    onSendOffer: () -> Unit ,
    showOfferSection: Boolean = false ,
    onToggleOfferSection: () -> Unit = {}
) {
    Surface(
        tonalElevation = 3.dp ,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp , vertical = 8.dp)) {
            // Section des messages texte
            Row(
                modifier = Modifier.fillMaxWidth() ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentMessageText ,
                    onValueChange = onMessageChange ,
                    placeholder = { Text("Type a message...") } ,
                    modifier = Modifier.weight(1f) ,
                    enabled = !isSending ,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences ,
                        imeAction = ImeAction.Send ,
                        keyboardType = KeyboardType.Text
                    ) ,
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Bouton + pour afficher/masquer la section d'offre
                IconButton(
                    onClick = onToggleOfferSection ,
                    enabled = !isSending
                ) {
                    Icon(
                        Icons.Default.Add ,
                        contentDescription = "Faire une offre" ,
                        tint = if (showOfferSection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Bouton d'envoi de message
                IconButton(
                    onClick = onSendMessage ,
                    enabled = !isSending && currentMessageText.isNotBlank()
                ) {
                    if (isSending) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Icon(
                            Icons.AutoMirrored.Filled.Send ,
                            contentDescription = "Send Message"
                        )
                    }
                }
            }

            // Section d'offre (affichée conditionnellement)
            if (showOfferSection) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth() ,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Faire une nouvelle offre" ,
                            style = MaterialTheme.typography.titleSmall ,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentOfferPrice ,
                            onValueChange = onOfferPriceChange ,
                            label = { Text("Prix proposé (€)") } ,
                            placeholder = { Text("ex: 25.00") } ,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal ,
                                imeAction = ImeAction.Next
                            ) ,
                            singleLine = true ,
                            modifier = Modifier.fillMaxWidth() ,
                            enabled = !isSending
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentOfferOptionalText ,
                            onValueChange = onOfferOptionalTextChange ,
                            label = { Text("Message (optionnel)") } ,
                            placeholder = { Text("Ajouter un message à votre offre...") } ,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences ,
                                imeAction = ImeAction.Send
                            ) ,
                            modifier = Modifier.fillMaxWidth() ,
                            enabled = !isSending ,
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth() ,
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = onToggleOfferSection ,
                                enabled = !isSending
                            ) {
                                Text("Annuler")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = onSendOffer ,
                                enabled = !isSending && currentOfferPrice.isNotBlank() ,
                            ) {
                                if (isSending) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp) ,
                                            strokeWidth = 2.dp ,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Envoi...")
                                    }
                                } else {
                                    Text("Envoyer l'offre")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}