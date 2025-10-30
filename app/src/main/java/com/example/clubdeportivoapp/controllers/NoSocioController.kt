package com.example.clubdeportivoapp.controllers

import com.example.clubdeportivo.entities.NoSocio
import com.example.clubdeportivo.entities.dtos.NoSocioEnabledDto
import com.example.clubdeportivoapp.repositories.NoSocioRepository
import com.example.clubdeportivoapp.repositories.SocioRepository
import java.time.LocalDate

class NoSocioController(private val noSocioRepository: NoSocioRepository,
                        private val socioRepository: SocioRepository
) {

    fun enrollNoSocio(noSocio: NoSocio): Pair<Boolean, String> {
        if (socioRepository.existSocio(noSocio.dni)) {
            return Pair(false, "El cliente ya se encuentra registrado como socio.")
        }
        if (noSocioRepository.existNoSocio(noSocio.dni)) {
            return Pair(false, "El cliente ya se encuentra registrado como no socio.")
        }

        val success = noSocioRepository.saveNoSocio(noSocio)
        return if (success) {
            Pair(true, "Inscripción exitosa")
        } else {
            Pair(false, "Error no ha podido completarse la operación.")
        }
    }

    fun getNoSocio(dni: String): NoSocio? = noSocioRepository.findNoSocioByDni(dni)

    fun getListNoSociosDayEnabled(date: LocalDate): List<NoSocioEnabledDto> =
        noSocioRepository.listNoSocioEnabled(date)
}