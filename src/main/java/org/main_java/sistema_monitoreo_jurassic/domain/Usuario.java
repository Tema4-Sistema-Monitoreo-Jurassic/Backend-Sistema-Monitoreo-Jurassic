package org.main_java.sistema_monitoreo_jurassic.domain;

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String nombre;

    @Column(nullable = false)
    private String apellido1;

    @Column(nullable = false)
    private String apellido2;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private Integer telefono;

    @Column
    private String direccion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarios_id", nullable = false)
    private Rol usuarios;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Credenciales usuario;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

}