package com.example.project_sy43

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class AuthManagerTest {

    private lateinit var authManager: AuthManager

    @Before
    fun setUp() {
        // Initialiser AuthManager avant chaque test
        authManager = AuthManager()
    }

    @Test
    fun testSignUpSuccess() {
        // Tester l'inscription réussie
        val signUpSuccess = authManager.signUp("user@example.com", "password123")
        assertTrue(signUpSuccess)
    }

    @Test
    fun testSignUpFailure() {
        // Tester l'échec de l'inscription avec un utilisateur déjà existant
        authManager.signUp("user@example.com", "password123")
        val signUpFailure = authManager.signUp("user@example.com", "password123")
        assertFalse(signUpFailure)
    }

    @Test
    fun testLoginSuccess() {
        // Tester la connexion réussie
        authManager.signUp("user@example.com", "password123")
        val loginSuccess = authManager.login("user@example.com", "password123")
        assertTrue(loginSuccess)
    }

    @Test
    fun testLoginFailure() {
        // Tester l'échec de la connexion avec un mot de passe incorrect
        authManager.signUp("user@example.com", "password123")
        val loginFailure = authManager.login("user@example.com", "wrongpassword")
        assertFalse(loginFailure)
    }
}

class AuthManager {
    private val users = mutableListOf<User>()

    fun signUp(email: String, password: String): Boolean {
        // Vérifier si l'utilisateur existe déjà
        if (users.any { it.email == email }) {
            return false
        }

        // Créer un nouvel utilisateur
        val newUser = User(
            id = "USER-${users.size + 1}",
            email = email,
            password = password
        )

        // Ajouter l'utilisateur à la liste
        users.add(newUser)
        return true
    }

    fun login(email: String, password: String): Boolean {
        // Vérifier si l'utilisateur existe et si le mot de passe est correct
        return users.any { it.email == email && it.password == password }
    }
}

data class User(val id: String, val email: String, val password: String)
