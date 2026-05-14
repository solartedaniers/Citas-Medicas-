package co.ucc.apicitasmedicas.model;

import jakarta.persistence.*;

/**
 * ╔══════════════════════════════════════════════════════════╗
 *  CLASE ABSTRACTA – Aplica:
 *   • Abstracción   : no se instancia directamente
 *   • Herencia      : Paciente, Profesional y Administrador la extienden
 *   • Encapsulamiento: atributos privados; solo expone getters
 *                     y setters donde el negocio lo permite
 *   • Polimorfismo  : describir() es abstracto, cada subclase lo implementa
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Estrategia SINGLE_TABLE → una sola tabla "usuarios" en BD.
 * La columna "tipo_usuario" discrimina el subtipo real.
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    // ── Identidad ────────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    // Almacena hash BCrypt — nunca el valor en texto plano
    @Column(nullable = false, length = 255)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    // ── Token de refresco guardado en BD ─────────────────────
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    // ── Estado de la cuenta ───────────────────────────────────
    @Column(nullable = false)
    private boolean activo = true;

    // ── Constructores ─────────────────────────────────────────
    protected Usuario() {}

    protected Usuario(String nombre, String correo, String contrasena, Rol rol) {
        this.nombre     = nombre;
        this.correo     = correo;
        this.contrasena = contrasena;
        this.rol        = rol;
    }

    // ── Getters (todos) ───────────────────────────────────────
    // Lectura siempre permitida; la contrasena se expone solo para validación interna

    public Long getId()           { return id; }
    public String getNombre()     { return nombre; }
    public String getCorreo()     { return correo; }
    public String getContrasena() { return contrasena; }
    public Rol getRol()           { return rol; }
    public String getRefreshToken() { return refreshToken; }
    public boolean isActivo()     { return activo; }

    // ── Setters controlados ───────────────────────────────────
    // Solo se expone setter donde el cambio es válido en el negocio.
    // NO se expone setter de "id" (lo asigna JPA) ni de "correo"
    // (inmutable tras el registro para evitar inconsistencias).

    public void setNombre(String nombre)           { this.nombre = nombre; }
    public void setContrasena(String contrasena)   { this.contrasena = contrasena; }
    public void setRol(Rol rol)                    { this.rol = rol; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setActivo(boolean activo)          { this.activo = activo; }

    // Setter de correo protegido: solo lo usan las subclases en su constructor
    protected void setCorreo(String correo)        { this.correo = correo; }

    // ── Método abstracto polimórfico ──────────────────────────
    /**
     * Cada subclase describe su tipo de usuario de forma propia.
     * Ejemplo de polimorfismo: Usuario u = new Paciente(...); u.describir();
     */
    public abstract String describir();
}
