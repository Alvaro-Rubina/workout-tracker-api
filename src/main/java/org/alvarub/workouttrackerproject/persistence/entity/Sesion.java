package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Sesion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categoria category;

    @ManyToOne
    @JoinColumn(name = "routine_id")
    private Rutina routine;

    // TODO: El nombre de esta variable podria cambiar a simplemente exercises
    @OneToMany(mappedBy = "session",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    @Builder.Default
    private Set<SesionEjercicio> sessionExercises = new LinkedHashSet<>();

}
