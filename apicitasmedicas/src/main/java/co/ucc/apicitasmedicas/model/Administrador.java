package co.ucc.apicitasmedicas.model;

import jakarta.persistence.*;

/**
 * Subtipo de Usuario que representa al administrador del sistema.
 *
 * Herencia     : extiende Usuario
 * Polimorfismo : implementa describir()
 * Responsabilidad única: modelar el rol administrador (gestiona usuarios y especialidades)
 *
 * No tiene atributos propios adicionales; su diferenciación es el Rol.ADMINISTRADOR
 * y las rutas /api/admin/** que le corresponden.
 */
@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    protected Administrador() {
        super();
    }

    public Administrador(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena, Rol.ADMINISTRADOR);
    }

    @Override
    public String describir() {
        return "Administrador: " + getNombre() + " | Correo: " + getCorreo();
    }
}
