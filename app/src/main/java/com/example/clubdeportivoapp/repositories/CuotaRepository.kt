package com.example.clubdeportivoapp.repositories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.clubdeportivo.entities.Cuota
import com.example.clubdeportivo.helpers.DataBaseHelper
import java.time.LocalDate

// (Opcional) Definir constantes para las columnas evita "magic strings"
private const val TABLE_CUOTAS = "cuotas"
private const val COL_VALOR_CUOTA = "valor_cuota"
private const val COL_FECHA_PAGO = "fecha_pago"
private const val COL_FECHA_VENCIMIENTO = "fecha_vencimiento"
private const val COL_FECHA_PROX_VENCIMIENTO = "fecha_prox_vencimiento"
private const val COL_FORMA_PAGO = "forma_pago"
private const val COL_CANTIDAD_CUOTAS = "cantidad_cuotas"
private const val COL_ESTADO = "estado"
private const val COL_FK_SOCIO = "fk_socio"

class CuotaRepository(context: Context) {

    // Asegúrate de usar context.applicationContext cuando instancies este repo
    private val dbHelper = DataBaseHelper(context)

    /**
     * Guarda una nueva cuota en la base de datos.
     * Asume que idSocio no es nulo.
     */
    fun saveCuota(cuota: Cuota, idSocio: Int): Boolean {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        
        val contentValues = ContentValues().apply {
            put(COL_VALOR_CUOTA, cuota.valueCuota)
            put(COL_FECHA_PAGO, cuota.payDay.toString())
            put(COL_FECHA_VENCIMIENTO, cuota.expirationDate.toString())
            put(COL_FECHA_PROX_VENCIMIENTO, cuota.nextExpirationDate.toString())
            put(COL_FORMA_PAGO, cuota.paymentMethod)
            put(COL_CANTIDAD_CUOTAS, cuota.numberCuotas)
            put(COL_ESTADO, cuota.state)
            put(COL_FK_SOCIO, idSocio)
        }
        val result = db.insert(TABLE_CUOTAS, null, contentValues)
        
        // NO LLAMAR A db.close()
        return result != -1L
    }

    /**
     * Verifica si un socio (por ID) ya tiene cuotas registradas.
     * Asume que id no es nulo.
     */
    fun existCuotaSocio(id: Int): Boolean {
        val db: SQLiteDatabase = dbHelper.readableDatabase

        // Consulta simplificada (sin JOIN innecesario)
        val query = "SELECT COUNT(*) FROM $TABLE_CUOTAS WHERE $COL_FK_SOCIO = ?"
        
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        // Usamos .use para garantizar que el cursor se cierre
        return cursor.use {
            if (it.moveToFirst()) {
                it.getInt(0) > 0 // true si count > 0
            } else {
                false // No debería pasar con COUNT(*), pero es seguro
            }
        }
        // NO LLAMAR A db.close()
    }

    /**
     * Encuentra la última fecha de próximo vencimiento para un socio.
     * Asume que id no es nulo.
     */
    fun findExpirationDate(id: Int): LocalDate? {
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val query = "SELECT $COL_FECHA_PROX_VENCIMIENTO FROM $TABLE_CUOTAS" +
                " WHERE $COL_FK_SOCIO = ?" +
                " ORDER BY $COL_FECHA_PROX_VENCIMIENTO DESC LIMIT 1"
        
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        // Usamos .use para garantizar que el cursor se cierre
        return cursor.use {
            if (it.moveToFirst()) {
                LocalDate.parse(it.getString(0))
            } else {
                null // Socio existe pero no tiene cuotas previas
            }
        }
        // NO LLAMAR A db.close()
    }
}