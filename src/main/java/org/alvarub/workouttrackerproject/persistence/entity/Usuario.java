package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Usuario extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true ,nullable = false)
    private String email;

    @Column(name = "body_weight")
    private Long bodyWeight;

    @Column(name = "completed_workouts")
    @Builder.Default
    private Long completedWorkouts = 0L;

    @Column(name = "auth0_id", unique = true, nullable = false)
    private String auth0Id;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "picture") // Avatar de Auth0
    private String picture;

    @Column(name = "last_access")
    @Builder.Default
    private LocalDateTime lastAccess = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Rol role;

    // RUTINAS CREADAS - FAVORITAS - GUARDADAS
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @Builder.Default
    private Set<Rutina> createdRoutines = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "user_favorite_routines",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "routine_id"))
    @Builder.Default
    private Set<Rutina> likedRoutines = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_saved_routines",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "routine_id"))
    @Builder.Default
    private Set<Rutina> savedRoutines = new HashSet<>();

    @Column(name = "completed_routines")
    private Long completedRoutines;

    @ManyToMany
    @JoinTable(name = "user_favorite_exercises",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id"))
    @Builder.Default
    private Set<Ejercicio> favoriteExercises = new HashSet<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @Builder.Default
    private List<Agenda> schedules = new ArrayList<>();

    /*NOTE: Dejo comentada la relacion en esta parte porque no me parece necesario que la relacion esté aca tambien.
    *  Después veo si descomento*/
    /*@OneToMany(mappedBy = "user", orphanRemoval = true)
    @Builder.Default
    private Set<Comentario> comments = new LinkedHashSet<>();*/

    @ManyToMany
    @JoinTable(
            name = "user_liked_comments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
            )
    @Builder.Default
    private Set<Comentario> likedComments = new HashSet<>();
}
