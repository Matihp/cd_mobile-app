package com.example.clubdeportivoapp.controllers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clubdeportivo.entities.Cuota
import com.example.clubdeportivo.entities.Socio
import com.example.clubdeportivoapp.repositories.CuotaRepository
import com.example.clubdeportivoapp.repositories.SocioRepository
import java.time.LocalDate

class CuotaController(private val cuotaRepository: CuotaRepository,
                      private val socioRepository: SocioRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun payCuota(paymentMethod: String, numberCuotas: Int, valueCuota: Double, dni: String): Boolean {

        val socio: Socio? = socioRepository.findSocioByDni(dni)
        if (socio == null || socio.idSocio == null) {
            return false
        }

        val payday: LocalDate = LocalDate.now()
        val existCuota: Boolean = cuotaRepository.existCuotaSocio(socio.idSocio)

        val expirationDate: LocalDate
        if (existCuota) {
            val lastExpiration = cuotaRepository.findExpirationDate(socio.idSocio)
            
            if (lastExpiration == null) {
                //el pago fallo 
                return false 
            }
            expirationDate = lastExpiration
        } else {
            // primer cuota del socio
            expirationDate = LocalDate.now()
        }

        val nextExpirationDate: LocalDate = expirationDate.plusMonths(1)

        val cuota = Cuota(
            idCuota = null,
            amount = valueCuota,
            payday = payday,
            paymentMethod = paymentMethod,
            numberCuotas = numberCuotas,
            state = true
            expirationDate = expirationDate,
            nextExpirationDate = nextExpirationDate,
        )

        return cuotaRepository.saveCuota(cuota, socio.idSocio)
    }
}