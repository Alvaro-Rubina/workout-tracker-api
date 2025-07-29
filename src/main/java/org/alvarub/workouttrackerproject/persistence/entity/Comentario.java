package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario user;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Rutina routine;

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    private Comentario replyTo;

    @OneToMany(mappedBy = "replyTo", orphanRemoval = true)
    private java.util.List<Comentario> replies = new ArrayList<>();
}
