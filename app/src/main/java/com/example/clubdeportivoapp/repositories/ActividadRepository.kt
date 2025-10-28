package com.example.clubdeportivoapp.repositories

import android.content.ContentValues
import android.content.Context
import com.example.clubdeportivo.entities.Actividad
import com.example.clubdeportivo.helpers.DataBaseHelper
import java.time.LocalDate

class ActividadRepository(context: Context) {

    private val dbHelper = DataBaseHelper(context)

    fun findActividadByName(name: String): Actividad? {
        return dbHelper.readableDatabase.use { db ->
            val query = """SELECT * FROM actividades WHERE nombre = ?"""
            db.rawQuery(query, arrayOf(name)).use { cursor ->
                if (cursor.moveToFirst()) {
                    Actividad(
                        idActividad = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        value = cursor.getDouble(cursor.getColumnIndexOrThrow("valor")),
                        maxQuotaSocio = cursor.getInt(cursor.getColumnIndexOrThrow("max_cupo_socio")),
                        maxQuotaNoSocio = cursor.getInt(cursor.getColumnIndexOrThrow("max_cupo_no_socio")),
                        quotaSocioAvailable = cursor.getInt(cursor.getColumnIndexOrThrow("cupo_socio_disponible")),
                        quotaNoSocioAvailable = cursor.getInt(cursor.getColumnIndexOrThrow("cupo_no_socio_disponible"))
                    )
                } else {
                    null
                }
            }
        }
    }

    fun enrollSocioActividad(actividadId: Int?, state: Boolean, idSocio: Int?): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("id_act", actividadId)
                put("id_socio", idSocio)
                put("estado", state)
            }
            val result = db.insert("act_socios", null, contentValues)
            result != -1L
        }
    }

    fun enrollNoSocioActividad(actividadId: Int?, date: LocalDate?, amount: Double?, idNoSocio: Int?): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("id_act", actividadId)
                put("id_noSocio", idNoSocio)
                put("dia_habilitado", date.toString())
                put("monto_pagado", amount)
            }
            val result = db.insert("act_nosocios", null, contentValues)
            result != -1L
        }
    }

    fun getAllActividades(): List<Actividad> {
        return dbHelper.readableDatabase.use { db ->
            val query = "SELECT * FROM actividades"
            db.rawQuery(query, emptyArray()).use { cursor ->
                val actividades = mutableListOf<Actividad>()
                if (cursor.moveToFirst()) {
                    do {
                        val actividad = Actividad(
                            idActividad = cursor.getInt(cursor.getColumnIndexOrThrow("id_actividad")),
                            name = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                            value = cursor.getDouble(cursor.getColumnIndexOrThrow("valor")),
                            maxQuotaSocio = cursor.getInt(cursor.getColumnIndexOrThrow("max_cupo_socio")),
                            maxQuotaNoSocio = cursor.getInt(cursor.getColumnIndexOrThrow("max_cupo_no_socio")),
                            quotaSocioAvailable = cursor.getInt(cursor.getColumnIndexOrThrow("cupo_socio_disponible")),
                            quotaNoSocioAvailable = cursor.getInt(cursor.getColumnIndexOrThrow("cupo_no_socio_disponible"))
                        )
                        actividades.add(actividad)
                    } while (cursor.moveToNext())
                }
                actividades
            }
        }
    }

    fun createActividad(act: Actividad): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("nombre", act.name)
                put("valor", act.value)
                put("max_cupo_socio", act.maxQuotaSocio)
                put("max_cupo_no_socio", act.maxQuotaNoSocio)
                put("cupo_socio_disponible", act.quotaSocioAvailable)
                put("cupo_no_socio_disponible", act.quotaNoSocioAvailable)
            }

            val result = db.insert("actividades", null, contentValues)
            result != -1L
        }
    }

    fun updateActividad(actividad: Actividad): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("nombre", actividad.name)
                put("valor", actividad.value)
                put("max_cupo_socio", actividad.maxQuotaSocio)
                put("max_cupo_no_socio", actividad.maxQuotaNoSocio)
                put("cupo_socio_disponible", actividad.quotaSocioAvailable)
                put("cupo_no_socio_disponible", actividad.quotaNoSocioAvailable)
            }
            val whereClause = "id_actividad = ?"
            val args = arrayOf(actividad.idActividad.toString())

            val rowsAffected = db.update("actividades", contentValues, whereClause, args)
            rowsAffected > 0
        }
    }

    fun isSocioAlreadyEnrolled(actividadId: Int?, idSocio: Int?): Boolean {
        return dbHelper.readableDatabase.use { db ->
            val query = "SELECT * FROM act_socios WHERE id_act = ? AND id_socio = ?"
            db.rawQuery(query, arrayOf(actividadId.toString(), idSocio.toString())).use { cursor ->
                cursor.moveToFirst()
            }
        }
    }

    fun isNoSocioAlreadyEnrolled(actividadId: Int?, idNoSocio: Int?): Boolean {
        return dbHelper.readableDatabase.use { db ->
            val query = "SELECT * FROM act_nosocios WHERE id_act = ? AND id_noSocio = ?"
            db.rawQuery(query, arrayOf(actividadId.toString(), idNoSocio.toString())).use { cursor ->
                cursor.moveToFirst()
            }
        }
    }

    fun paymentDairy(actividadId: Int, noSocioId: Int?, date: LocalDate, amount: Double): Boolean {
        return dbHelper.writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put("id_act", actividadId)
                put("id_noSocio", noSocioId)
                put("dia_habilitado", date.toString())
                put("monto_pagado", amount)
            }
            val whereClause = "id_act = ? and id_noSocio = ?"
            val args = arrayOf(actividadId.toString(), noSocioId.toString())

            val rowsAffected = db.update("act_nosocios", contentValues, whereClause, args)
            rowsAffected > 0
        }
    }

    fun findNoSocioQuotaAvailable(actividadId: Int, date: LocalDate): Int {
        return dbHelper.readableDatabase.use { db ->
            val query = "SELECT count(*) FROM act_nosocios WHERE id_act = ? AND dia_habilitado = ?"
            db.rawQuery(query, arrayOf(actividadId.toString(), date.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getInt(0)  // Devuelve el valor de la columna 0
                } else {
                    0
                }
            }
        }
    }

}