package com.example.clubdeportivoapp.repositories

import android.content.ContentValues
import android.content.Context
import com.example.clubdeportivo.entities.Socio
import com.example.clubdeportivo.entities.dtos.SocioExpirationDayDto
import com.example.clubdeportivo.helpers.DataBaseHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SocioRepository(context: Context) {

    private val dbHelper = DataBaseHelper(context)

    fun existSocio(dni: String): Boolean {
        return dbHelper.readableDatabase.use { db ->
            val query = "SELECT * FROM socios WHERE dni = ?"
            db.rawQuery(query, arrayOf(dni)).use { cursor ->
                cursor.count > 0
            }
        }
    }    

    fun findSocioByDni(dni: String): Socio? {
        return dbHelper.readableDatabase.use { db ->
            val query = """SELECT * FROM socios WHERE dni = ?"""
            db.rawQuery(query, arrayOf(dni)).use { cursor ->
                if (cursor.moveToFirst()) {
                    Socio(
                        idSocio = cursor.getInt(cursor.getColumnIndexOrThrow("id_socio")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        lastName = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                        stateSocio = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) != 0,
                        aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("apto_fisico")) != 0
                    )
                } else {
                    null
                }
            }
        }
    }

    fun saveSocio(socio: Socio): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("nombre", socio.name)
                put("apellido", socio.lastName)
                put("dni", socio.dni)
                put("email", socio.email)
                put("telefono", socio.phone)
                put("apto_fisico", socio.aptoFisico)
                put("estado", socio.stateSocio)
            }
            val result = db.insert("socios", null, contentValues)
            result != -1L // SQLite devuelve -1 si falla
        }
    }

    fun updateSocio(socio: Socio): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("nombre", socio.name)
                put("apellido", socio.lastName)
                put("dni", socio.dni)
                put("email", socio.email)
                put("telefono", socio.phone)
                put("apto_fisico", socio.aptoFisico)
                put("estado", socio.stateSocio)
            }
            val whereClause = "id_socio = ?"
            val args = arrayOf(socio.idSocio.toString())

            val rowsAffected = db.update("socios", contentValues, whereClause, args)
            rowsAffected > 0
        }
    }

    fun listSocioByExpirationDay(date: LocalDate): List<SocioExpirationDayDto> {
        return dbHelper.readableDatabase.use { db ->
            val query = """
                SELECT s.id_socio, s.nombre, s.apellido, s.dni, c.fecha_prox_vencimiento, c.estado 
                FROM socios AS s INNER JOIN cuotas AS c ON s.id_socio = c.fk_socio 
                WHERE date(c.fecha_prox_vencimiento) = ?
            """

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = date.format(formatter)

            db.rawQuery(query, arrayOf(formattedDate)).use { cursor ->
                val socios = mutableListOf<SocioExpirationDayDto>()
                if (cursor.moveToFirst()) {
                    do {
                        val expirationDayStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_prox_vencimiento"))
                        val socio = SocioExpirationDayDto(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow("id_socio")),
                            name = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                            lastName = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                            dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                            state = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) != 0,
                            expirationDay = LocalDate.parse(expirationDayStr, formatter)
                        )
                        socios.add(socio)
                    } while (cursor.moveToNext())
                }
                socios
            }
        }
    }

    fun listSociosMora(date: LocalDate): List<SocioExpirationDayDto> {
        return dbHelper.readableDatabase.use { db ->
            val query = """
                SELECT s.id_socio, s.nombre, s.apellido, s.dni, c.fecha_prox_vencimiento, s.estado 
                FROM socios AS s 
                INNER JOIN cuotas c ON s.id_socio = c.fk_socio 
                INNER JOIN (SELECT fk_socio, MAX(fecha_prox_vencimiento) AS max_fecha FROM cuotas GROUP BY fk_socio) AS px 
                ON c.fk_socio = px.fk_socio AND c.fecha_prox_vencimiento = px.max_fecha 
                WHERE date(c.fecha_prox_vencimiento) < ? 
                ORDER BY c.fecha_prox_vencimiento DESC;
            """

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = date.format(formatter)

            db.rawQuery(query, arrayOf(formattedDate)).use { cursor ->
                val socios = mutableListOf<SocioExpirationDayDto>()
                if (cursor.moveToFirst()) {
                    do {
                        val expirationDayStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_prox_vencimiento"))
                        val socio = SocioExpirationDayDto(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow("id_socio")),
                            name = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                            lastName = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                            dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                            state = cursor.getInt(cursor.getColumnIndexOrThrow("estado")) != 0,
                            expirationDay = LocalDate.parse(expirationDayStr, formatter)
                        )
                        socios.add(socio)
                    } while (cursor.moveToNext())
                }
                socios
            }
        }
    }

    fun updateState(idSocio: Int?, newState: Boolean): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("estado", if (newState) 1 else 0)
            }
            val rowsAffected = db.update("socios", values, "id_socio = ?", arrayOf(idSocio.toString()))
            rowsAffected > 0
        }
    }
}