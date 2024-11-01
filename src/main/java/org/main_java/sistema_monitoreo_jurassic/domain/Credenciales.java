package org.main_java.sistema_monitoreo_jurassic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "Credenciales")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Credenciales {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "usuario")
    private Usuario usuario;  // Relación con Usuario
}
