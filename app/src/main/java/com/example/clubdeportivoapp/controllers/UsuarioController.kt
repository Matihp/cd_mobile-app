package com.example.clubdeportivoapp.controllers

import com.example.clubdeportivo.entities.Usuario
import com.example.clubdeportivo.entities.dtos.UsuarioDto
import com.example.clubdeportivoapp.repositories.UsuarioRepository

class UsuarioController(private val usuarioRepository: UsuarioRepository) {

    fun updateUsuario(user: Usuario): Boolean {
        var fk = 0 

        if (user.rol == "Administrador") {
            fk = 1
        } else if (user.rol == "Empleado") {
            fk = 2
        }

        val userEdit = UsuarioDto(
            idUsuario = user.idUsuario,
            username = user.username,
            password = user.password,
            fkRol = fk,
            name = user.name,
            lastName = user.lastName,
            dni = user.dni,
            email = user.email,
            phone = user.phone
        )
        return usuarioRepository.updateUsuario(userEdit)
    }

    fun findUsuarioByUsername(username: String): Usuario?{
        return usuarioRepository.findUsuarioByUsername(username)
    }
}