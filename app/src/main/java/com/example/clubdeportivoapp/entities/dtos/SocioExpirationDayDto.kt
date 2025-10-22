package com.example.clubdeportivo.entities.dtos
import org.threeten.bp.LocalDate

data class SocioExpirationDayDto(
    val id: Int,
    val name: String,
    val lastName: String,
    val dni: String,
    val state: Boolean,
    val expirationDay: LocalDate
)
