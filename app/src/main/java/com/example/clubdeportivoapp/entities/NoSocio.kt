package com.example.clubdeportivo.entities

data class NoSocio(
    val idNoSocio: Int? = null,
    val aptoFisico: Boolean,
    override val name: String,
    override val lastName: String,
    override val dni: String,
    override val email: String,
    override val phone: String
) : Persona(name, lastName, dni, email, phone){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NoSocio) return false

        return idNoSocio == other.idNoSocio &&
                name == other.name &&
                lastName == other.lastName &&
                aptoFisico == other.aptoFisico &&               
                dni == other.dni &&
                email == other.email &&
                phone == other.phone
    }

    override fun hashCode(): Int {
        return listOf(idNoSocio, name, lastName, aptoFisico, 
            dni, email, phone).hashCode()
    }
}
