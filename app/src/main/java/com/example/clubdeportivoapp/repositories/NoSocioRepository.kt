package com.example.clubdeportivoapp.repositories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.clubdeportivo.entities.NoSocio
import com.example.clubdeportivo.entities.dtos.NoSocioEnabledDto
import com.example.clubdeportivo.helpers.DataBaseHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// --- 1. Definimos constantes para todas las "Magic Strings" ---
private const val TABLE_NO_SOCIOS = "noSocios"
private const val COL_ID_NO_SOCIO = "id_noSocio"
private const val COL_NOMBRE = "nombre"
private const val COL_APELLIDO = "apellido"
private const val COL_DNI = "dni"
private const val COL_EMAIL = "email"
private const val COL_TELEFONO = "telefono"
private const val COL_APTO_FISICO = "apto_fisico"

class NoSocioRepository(context: Context) {

    private val dbHelper = DataBaseHelper(context)

    fun saveNoSocio(noSocio: NoSocio): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_NOMBRE, noSocio.name)
            put(COL_APELLIDO, noSocio.lastName)
            put(COL_DNI, noSocio.dni)
            put(COL_EMAIL, noSocio.email)
            put(COL_TELEFONO, noSocio.phone)
            put(COL_APTO_FISICO, noSocio.aptoFisico)
        }
        val result = db.insert(TABLE_NO_SOCIOS, null, contentValues)
        return result != -1L
    }

    fun existNoSocio(dni: String): Boolean {
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val query = "SELECT 1 FROM $TABLE_NO_SOCIOS WHERE $COL_DNI = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(dni))

        return cursor.use {
            it.moveToFirst() 
        }
    }

    fun findNoSocioByDni(dni: String): NoSocio? {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT * FROM $TABLE_NO_SOCIOS WHERE $COL_DNI = ?"
        val cursor = db.rawQuery(query, arrayOf(dni))

        return cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COL_ID_NO_SOCIO))
                val name = it.getString(it.getColumnIndexOrThrow(COL_NOMBRE))
                val lastName = it.getString(it.getColumnIndexOrThrow(COL_APELLIDO))
                val email = it.getString(it.getColumnIndexOrThrow(COL_EMAIL))
                val phone = it.getString(it.getColumnIndexOrThrow(COL_TELEFONO))
                val dniNoSocio = it.getString(it.getColumnIndexOrThrow(COL_DNI))
                val aptoFisico = it.getInt(it.getColumnIndexOrThrow(COL_APTO_FISICO)) != 0
                NoSocio(id, aptoFisico, name, lastName, dniNoSocio, email, phone)
            } else {
                null
            }
        }
    }

    fun listNoSocioEnabled(date: LocalDate): List<NoSocioEnabledDto> {
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val query = """
            SELECT NS.$COL_ID_NO_SOCIO, NS.$COL_NOMBRE As nombre_nosocio, NS.$COL_APELLIDO, NS.$COL_DNI,
                   A.nombre As nombre_act, AN.dia_habilitado
            FROM $TABLE_NO_SOCIOS AS NS
            INNER JOIN act_nosocios AS AN on NS.$COL_ID_NO_SOCIO = AN.id_noSocio
            INNER JOIN actividades AS A on AN.id_act = A.id_actividad
            WHERE date(AN.dia_habilitado) = ?
        """.trimIndent()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(formatter)

        val cursor = db.rawQuery(query, arrayOf(formattedDate))

        val noSocios = mutableListOf<NoSocioEnabledDto>()

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(COL_ID_NO_SOCIO))
                    val nameNoSocio = it.getString(it.getColumnIndexOrThrow("nombre_nosocio"))
                    val lastName = it.getString(it.getColumnIndexOrThrow(COL_APELLIDO))
                    val dniNoSocio = it.getString(it.getColumnIndexOrThrow(COL_DNI))
                    val nameAct = it.getString(it.getColumnIndexOrThrow("nombre_act"))
                    val enabledDayStr = it.getString(it.getColumnIndexOrThrow("dia_habilitado"))
                    val enabledDay = LocalDate.parse(enabledDayStr, formatter)

                    val noSocio = NoSocioEnabledDto(noSocioId = id, nameNoSocio = nameNoSocio, lastName = lastName, dni = dniNoSocio, nameAct = nameAct, enableDay = enabledDay)
                    noSocios.add(noSocio)
                } while (it.moveToNext())
            }
        }
        return noSocios
    }
}