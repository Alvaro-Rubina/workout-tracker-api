package org.alvarub.workouttrackerproject.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true ,nullable = false)
    private String email;

    @Column(name = "body_weigh")
    private Long bodyWeight;

    @Column(name = "completed_workouts")
    private Long completedWorkouts;

    /*private String auth0Id;*/

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_access")
    @Builder.Default
    private LocalDateTime lastAccess = LocalDateTime.now();

    @ManyToMany
    @JsonIgnoreProperties("users")
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

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
