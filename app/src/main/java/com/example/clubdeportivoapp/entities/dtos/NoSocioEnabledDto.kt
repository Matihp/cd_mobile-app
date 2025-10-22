package com.example.clubdeportivo.entities.dtos
import org.threeten.bp.LocalDate

data class NoSocioEnabledDto(
    val noSocioId: Int,
    val nameNoSocio: String,
    val lastName: String,
    val dni: String,
    val nameAct: String,
    val enableDay: LocalDate
)
