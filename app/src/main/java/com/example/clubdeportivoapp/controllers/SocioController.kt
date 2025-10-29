package com.example.clubdeportivoapp.controllers

import com.example.clubdeportivo.entities.Socio
import com.example.clubdeportivo.entities.dtos.SocioExpirationDayDto
import com.example.clubdeportivo.repositories.NoSocioRepository
import com.example.clubdeportivo.repositories.SocioRepository
import org.threeten.bp.LocalDate

class SocioController(private val socioRepository: SocioRepository,
                      private val noSocioRepository: NoSocioRepository) {

    fun enrollSocio(socio: Socio): Pair<Boolean, String> {
        if (socioRepository.existSocio(socio.dni)) {
            return Pair(false, "El cliente ya se encuentra registrado como socio.")
        }
        if (noSocioRepository.existNoSocio(socio.dni)) {
            return Pair(false, "El cliente ya se encuentra registrado como no socio.")
        }

        val success = socioRepository.saveSocio(socio)
        return if (success) {
            Pair(true, "Inscripción exitosa")
        } else {
            Pair(false, "Error no ha podido completarse la operación.")
        }
    }
    fun getSocio(dni: String): Socio?{
        val socio: Socio? = socioRepository.findSocioByDni(dni)
        return socio
    }

    fun updateState(idSocio: Int?, newState: Boolean): Boolean {
        return socioRepository.updateState(idSocio, newState)
    }    

    fun getListSociosByExpiationDay(date: LocalDate): MutableList<SocioExpirationDayDto>{
        return socioRepository.listSocioByExpirationDay(date)
    }

    fun getListSociosMora(date: LocalDate): MutableList<SocioExpirationDayDto>{
        return socioRepository.listSociosMora(date)
    }

}