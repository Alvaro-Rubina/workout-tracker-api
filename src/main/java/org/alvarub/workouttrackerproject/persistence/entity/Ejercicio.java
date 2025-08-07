package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "tips", length = 300)
    private String tips;

    @ElementCollection
    @CollectionTable(name = "exercise_instructions", joinColumns = @JoinColumn(name = "exercise_id"))
    @MapKeyColumn(name = "step_number")
    @Column(name = "instruction")
    @Builder.Default
    private Map<Integer, String> instructions = new LinkedHashMap<>();

    @ElementCollection
    @CollectionTable(name = "exercise_sampleVideos", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "sample_video")
    @Builder.Default
    private Set<String> sampleVideos = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "exercise_equipment",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id"))
    @Builder.Default
    private Set<Equipamiento> equipment = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "exercise_target_muscles",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "muscle_id"))
    @Builder.Default
    private Set<Musculo> targetMuscles = new HashSet<>();

}
