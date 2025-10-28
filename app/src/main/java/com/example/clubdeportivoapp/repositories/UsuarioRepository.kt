package com.example.clubdeportivoapp.repositories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.clubdeportivo.entities.Usuario
import com.example.clubdeportivo.entities.dtos.UsuarioDto
import com.example.clubdeportivo.helpers.DataBaseHelper
import com.example.clubdeportivoapp.utils.SecurityUtils

class UsuarioRepository(private val context: Context) {

    private val dbHelper = DataBaseHelper(context)

    fun login(username: String, password: String): Boolean {
        val hashedPassword = SecurityUtils.sha256(password)
        return dbHelper.readableDatabase.use { db ->
            val query = "SELECT * FROM usuarios WHERE username = ? AND password = ?"
            db.rawQuery(query, arrayOf(username, hashedPassword)).use { cursor ->
                cursor.count > 0
            }
        }
    }

    fun findUsuarioByUsername(username: String): Usuario? {
        return dbHelper.readableDatabase.use { db ->
            val query = """
                SELECT us.id, us.nombre AS nombre_user, us.apellido, us.dni, us.email, 
                       us.telefono, us.username, us.password, r.Nombre AS nombre_rol 
                FROM usuarios As us 
                INNER JOIN roles As r ON us.fk_rol = r.id_rol 
                WHERE username = ?
            """
            db.rawQuery(query, arrayOf(username)).use { cursor ->
                if (cursor.moveToFirst()) {
                    Usuario(
                        idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        rol = cursor.getString(cursor.getColumnIndexOrThrow("nombre_rol")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("nombre_user")),
                        lastName = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                    )
                } else {
                    null
                }
            }
        }
    }

    fun saveUserSession(usuario: Usuario): Boolean {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLogged", true)
            putString("username", usuario.username)
            putString("rol", usuario.rol)
            putString("name", usuario.name)
            putString("lastName", usuario.lastName)
            apply()
        }
        return true
    }

    fun updateUsuario(usuario: UsuarioDto): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("nombre", usuario.name)
                put("apellido", usuario.lastName)
                put("dni", usuario.dni)
                put("email", usuario.email)
                put("telefono", usuario.phone)
                put("username", usuario.username)
                put("password", usuario.password)
                put("fk_rol", usuario.fkRol)
            }
            val whereClause = "id = ?"
            val args = arrayOf(usuario.idUsuario.toString())

            val rowsAffected = db.update("usuarios", contentValues, whereClause, args)
            rowsAffected > 0
        }
    }
}