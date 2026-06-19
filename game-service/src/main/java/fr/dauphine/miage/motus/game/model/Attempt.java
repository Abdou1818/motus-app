package fr.dauphine.miage.motus.game.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gameId;
    private int attemptNumber;
    private String proposedWord;

    @Column(columnDefinition = "TEXT")
    private String result; // JSON string

    private LocalDateTime createdAt = LocalDateTime.now();
}
