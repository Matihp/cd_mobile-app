package com.example.clubdeportivo.entities

data class Actividad (
    val idActividad: Int?,
    val name: String,
    val value: Double,
    val maxQuotaSocio: Int,
    val maxQuotaNoSocio: Int,
    var quotaSocioAvailable: Int,
    var quotaNoSocioAvailable: Int,
)