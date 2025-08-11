package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.alvarub.workouttrackerproject.persistence.enums.Dificultad;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Rutina extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "previous_public_state")
    private Boolean previousPublicState;

    @Column(name = "favorites_count")
    @Builder.Default
    private Long likesCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Dificultad difficulty;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Categoria category;

    @OneToMany(mappedBy = "routine",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    @Builder.Default
    private List<Sesion> sessions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario user;

}
