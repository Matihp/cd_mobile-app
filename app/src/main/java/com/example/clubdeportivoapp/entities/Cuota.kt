package com.example.clubdeportivo.entities
import org.threeten.bp.LocalDate

data class Cuota(
    val idCuota: Int? = null,
    val payDay: LocalDate,
    val paymentMethod: String,
    val numberCuotas: Int,
    val valueCuota: Double,
    val state: Boolean,
    val expirationDate: LocalDate,
    val nextExpirationDate: LocalDate,
)
