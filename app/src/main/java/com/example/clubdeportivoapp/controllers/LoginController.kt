package com.example.clubdeportivoapp.controllers

import android.content.Context
import com.example.clubdeportivo.entities.Usuario
import com.example.clubdeportivo.repositories.UsuarioRepository

class LoginController(
    private val context: Context,
    private val usuarioRepository: UsuarioRepository
) {

    fun loginIn(username: String, password: String): Boolean {
        val isValidLogin = usuarioRepository.login(username, password)
        
        if (!isValidLogin) {
            return false
        }

        try {
            val usuario = usuarioRepository.findUsuarioByUsername(username)
            if (usuario != null) {
                usuarioRepository.saveUserSession(usuario)
                return true
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }
}
