package com.example.project_sy43.ui.theme.main_screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.project_sy43.navigation.VintedScreen
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

@SuppressLint("MissingPermission")
@Composable
fun PurchaseScreen(
    db: FirebaseFirestore,
    navController: NavHostController,
    itemId: String,
    itemName: String
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var buyerName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var deliveryLocation by remember { mutableStateOf(LatLng(48.8566, 2.3522)) } // Paris
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val location: Location? = fusedLocationClient.lastLocation.await()
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
                }
            } catch (e: Exception) {
                Log.e("Map", "Erreur récupération localisation utilisateur : ${e.message}")
            }
        } else {
            Log.w("Map", "Permission localisation refusée")
        }
    }

    // Géocodage de l’adresse saisie
    LaunchedEffect(address) {
        if (address.length > 5) {
            val geo = Geocoder(context, Locale.getDefault())
            try {
                val results = withContext(Dispatchers.IO) { geo.getFromLocationName(address, 1) }
                results?.firstOrNull()?.let {
                    deliveryLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(deliveryLocation, 15f)
                }
            } catch (e: Exception) {
                Log.e("Map", "Erreur géocodage : ${e.message}")
            }
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Article : $itemName", style = MaterialTheme.typography.titleLarge)
        Text("ID article : $itemId", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = buyerName,
            onValueChange = { buyerName = it },
            label = { Text("Votre nom") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Adresse de livraison") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (itemId.isNotBlank()) {
                    db.collection("Post").document(itemId)
                        .update("available", false)
                        .addOnSuccessListener {
                            navController.navigate(VintedScreen.MonCompte.name) {
                                popUpTo(0)
                            }
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("PurchaseScreen", "Erreur mise à jour disponibilité", e)
                        }
                } else {
                    navController.navigate(VintedScreen.MonCompte.name) {
                        popUpTo(0)
                    }
                }
            }
        ) {
            Text("Acheter")
        }


        Spacer(Modifier.height(16.dp))
        Text("Carte :", style = MaterialTheme.typography.titleMedium)

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = userLocation != null)
        ) {
            Marker(
                state = MarkerState(position = deliveryLocation),
                title = "Adresse de livraison"
            )
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Votre position"
                )
            }
        }
    }
}
