package com.example.clubdeportivo.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val appContext = context

    companion object {
        private const val DATABASE_NAME = "proyecto_club_deportivo"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla Rol
        db.execSQL("""
            CREATE TABLE roles (
                id_rol INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT
            );
        """)

        // Tabla Usuarios
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                username TEXT,
                password TEXT,
                dni TEXT,
                email TEXT,
                telefono TEXT,
                fk_rol INTEGER,
                CONSTRAINT fk_usuario FOREIGN KEY(fk_rol) REFERENCES roles(id_rol)
            );
        """)

        // Tabla Socios
        db.execSQL("""
            CREATE TABLE socios (
                id_socio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                dni TEXT,
                email TEXT,
                telefono TEXT,
                apto_fisico INTEGER,
                estado INTEGER
            );
        """)

        // Tabla No Socio
        db.execSQL("""
            CREATE TABLE noSocios (
                id_no_socio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                dni TEXT,
                email TEXT,
                telefono TEXT,
                apto_fisico INTEGER
            );
        """)

        // Tabla Cuotas
        db.execSQL("""
            CREATE TABLE cuotas (
                id_cuota INTEGER PRIMARY KEY AUTOINCREMENT,
                valor_cuota REAL,
                fecha_pago TEXT,
                fecha_vencimiento TEXT,
                fecha_prox_vencimiento TEXT,
                forma_pago TEXT,
                cantidad_cuotas INTEGER,
                estado INTEGER,
                fk_socio INTEGER,
                CONSTRAINT fk_socio_cuota FOREIGN KEY(fk_socio) REFERENCES socios(id_socio)
            );
        """)

        // Tabla Actividades
        db.execSQL("""
            CREATE TABLE actividades (
                id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                valor REAL,
                max_cupo_socio INTEGER,
                max_cupo_no_socio INTEGER,
                cupo_socio_disponible INTEGER,
                cupo_no_socio_disponible INTEGER
            );
        """)

        // Tabla Act_Socios
        db.execSQL("""
            CREATE TABLE act_socios (
                id_act INTEGER,
                id_socio INTEGER,
                estado INTEGER,
                CONSTRAINT pk_act_Socio PRIMARY KEY (id_act, id_socio),
                CONSTRAINT fk_act_socios_act FOREIGN KEY (id_act) REFERENCES actividades (id_actividad),
                CONSTRAINT fk_act_socios FOREIGN KEY (id_socio) REFERENCES socios (id_socio)
            );
        """)

        // Tabla Act_NoSocios
        db.execSQL("""
            CREATE TABLE act_nosocios (
                id_act INTEGER,
                id_no_socio INTEGER,
                dia_habilitado TEXT,
                monto_pagado REAL,
                CONSTRAINT pk_act_noSocio PRIMARY KEY (id_act, id_no_socio),
                CONSTRAINT fk_act_noSocios_A FOREIGN KEY (id_act) REFERENCES actividades (id_actividad),
                CONSTRAINT fk_act_noSocios_NS FOREIGN KEY (id_no_socio) REFERENCES noSocios (id_no_socio)
            );
        """)

        executeScriptSQL(db, "db.sql")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS act_nosocios")
        db.execSQL("DROP TABLE IF EXISTS act_socios")
        db.execSQL("DROP TABLE IF EXISTS actividades")
        db.execSQL("DROP TABLE IF EXISTS cuotas")
        db.execSQL("DROP TABLE IF EXISTS noSocios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS roles")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    private fun executeScriptSQL(db: SQLiteDatabase, file: String) {
        try {
            appContext.assets.open(file).use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    val sqlComplete = reader.readText()
                    val commands = sqlComplete.split(";")

                    for (command in commands) {
                        val sql = command.trim()
                        if (sql.isNotEmpty()) {
                            db.execSQL(sql)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}