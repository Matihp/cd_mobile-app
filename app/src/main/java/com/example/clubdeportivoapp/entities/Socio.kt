package com.example.clubdeportivo.entities

data class Socio(
    val idSocio: Int? = null,
    var stateSocio: Boolean,
    val aptoFisico: Boolean,
    override val name: String,
    override val lastName: String,
    override val dni: String,
    override val email: String,
    override val phone: String
) : Persona(name, lastName, dni, email, phone){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Socio) return false

        return idSocio == other.idSocio &&
                stateSocio == other.stateSocio &&
                name == other.name &&
                lastName == other.lastName &&
                aptoFisico == other.aptoFisico &&             
                dni == other.dni &&
                email == other.email &&
                phone == other.phone
    }

    override fun hashCode(): Int {
        return listOf(idSocio,name,lastName,stateSocio,aptoFisico, 
            dni,email,phone).hashCode()
    }
}
