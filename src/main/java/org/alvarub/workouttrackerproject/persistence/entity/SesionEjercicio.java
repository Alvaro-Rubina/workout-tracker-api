package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class SesionEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "sets", nullable = false)
    private Long sets;

    @Column(name = "reps", nullable = false)
    private Long reps;

    @Column(name = "rest_between_sets", nullable = false)
    private Double restBetweenSets;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "sesion_id")
    private Sesion session;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Ejercicio exercise;

}
