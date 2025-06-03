package com.example.project_sy43.ui.theme.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun Project_SY43Theme(
    // Ignore le thème sombre et utilise toujours le thème clair
    darkTheme: Boolean = false,
    // Désactivez les couleurs dynamiques pour s'assurer que le thème est cohérent
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Utilisez toujours le schéma de couleurs clair
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
