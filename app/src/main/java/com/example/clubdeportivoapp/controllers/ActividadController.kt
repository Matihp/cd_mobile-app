package com.example.clubdeportivoapp.controllers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clubdeportivo.entities.Actividad
import com.example.clubdeportivo.entities.NoSocio
import com.example.clubdeportivo.entities.Socio
import com.example.clubdeportivoapp.repositories.ActividadRepository
import com.example.clubdeportivoapp.repositories.NoSocioRepository
import com.example.clubdeportivoapp.repositories.SocioRepository
import java.time.LocalDate
import java.time.ZoneId

class ActividadController(private val actividadRepository: ActividadRepository,
                          private val socioRepository: SocioRepository,
                          private val noSocioRepository: NoSocioRepository
) {

    fun enrollSocioActividad(nameActividad: String, dniSocio: String): Pair<Boolean, String> {

        val socio: Socio? = socioRepository.findSocioByDni(dniSocio)
        if (socio == null) {
            return Pair(false, "El socio con DNI $dniSocio no existe.")
        }

        if (!socio.stateSocio) {
            return Pair(false, "El socio esta suspendido por falta de pago.")
        }

        val actividad: Actividad? = actividadRepository.findActividadByName(nameActividad)
        if (actividad == null) {
            return Pair(false, "La actividad '$nameActividad' no existe.")
        }

        val isEnroll: Boolean = actividadRepository.isSocioAlreadyEnrolled(actividad.idActividad, socio.idSocio!!)
        if (isEnroll) {
            return Pair(false, "El socio ya está inscripto en esta actividad.")
        }

        if (actividad.quotaSocioAvailable <= 0) {
            return Pair(false, "No hay cupo disponible para socios en esta actividad.")
        }

        val success = actividadRepository.enrollSocioActividad(actividad.idActividad, true, socio.idSocio)
        return if (success) {
            actividad.quotaSocioAvailable -= 1
            actividadRepository.updateActividad(actividad)
            Pair(true, "Inscripción realizada con éxito.")
        } else {
            Pair(false, "Error al actualizar la actividad.")
        }
    }

    fun enrollNoSocioActividad(nameActividad: String, dniNoSocio: String): Pair<Boolean, String> {

        val noSocio: NoSocio? = noSocioRepository.findNoSocioByDni(dniNoSocio)
        if (noSocio == null) {
            return Pair(false, "El no socio con DNI $dniNoSocio no existe.")
        }

        val actividad: Actividad? = actividadRepository.findActividadByName(nameActividad)
        if (actividad == null) {
            return Pair(false, "La actividad '$nameActividad' no existe.")
        }

        val isEnroll: Boolean = actividadRepository.isNoSocioAlreadyEnrolled(actividad.idActividad, noSocio.idNoSocio!!)
        if (isEnroll) {
            return Pair(false, "El no socio ya está inscripto en esta actividad.")
        }

        if (actividad.quotaNoSocioAvailable <= 0) {
            return Pair(false, "No hay cupo disponible para no socios en esta actividad.")
        }
        
        val success = actividadRepository.enrollNoSocioActividad(actividad.idActividad, null, null, noSocio.idNoSocio)
        return if (success) {
            Pair(true, "Inscripción realizada con éxito.")
        } else {
            Pair(false, "Error al actualizar la actividad.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun paymentDailyActividad(nameActividad: String, dniNoSocio: String, amount: Double): Pair<Boolean, String> {

        val noSocio: NoSocio? = noSocioRepository.findNoSocioByDni(dniNoSocio)
        if (noSocio == null) {
            return Pair(false, "El cliente con DNI $dniNoSocio no existe.")
        }

        val actividad: Actividad? = actividadRepository.findActividadByName(nameActividad)
        if (actividad == null) {
            return Pair(false, "La actividad '$nameActividad' no existe.")
        }

        val isEnroll: Boolean = actividadRepository.isNoSocioAlreadyEnrolled(actividad.idActividad, noSocio.idNoSocio!!)
        if (!isEnroll) {
            return Pair(false, "El cliente no está inscripto en esta actividad.")
        }

        if (actividad.quotaNoSocioAvailable <= 0) {
            return Pair(false, "No hay cupo disponible para no socios en esta actividad.")
        }

        if (noSocio.idNoSocio == null || actividad.idActividad == null) {
            return Pair(false, "Error: El ID del no socio o de la actividad es nulo.") 
        }

        val date = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"))

        val count = actividadRepository.findNoSocioQuotaAvailable(actividad.idActividad, date)
        val success = actividadRepository.paymentDairy(actividad.idActividad, noSocio.idNoSocio, date, amount)
        return if (success) {
            if (count > 0) {
                actividad.quotaNoSocioAvailable -= 1
            } else if (count == 0) {
                actividad.quotaNoSocioAvailable = actividad.maxQuotaNoSocio - 1
            }        
            actividadRepository.updateActividad(actividad)
            Pair(true, "Pago realizado con éxito.")
        } else {
            Pair(false, "Error no pudo procesarse el pago.")
        }
    }

    fun createActividad(act: Actividad): Boolean{
        return actividadRepository.createActividad(act)
    }

    fun getallActividades():List<Actividad>{
        return actividadRepository.getAllActividades()
    }
}