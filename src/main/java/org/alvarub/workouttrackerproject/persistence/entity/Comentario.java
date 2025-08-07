package org.alvarub.workouttrackerproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "likes")
    private Long likes;

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
    @Builder.Default
    private List<Comentario> replies = new ArrayList<>();
}
