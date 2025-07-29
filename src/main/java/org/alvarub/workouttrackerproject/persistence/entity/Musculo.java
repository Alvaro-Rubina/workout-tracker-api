package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Musculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "muscle_group_id", nullable = false)
    private ZonaMuscular muscleGroup;

}
