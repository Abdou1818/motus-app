package fr.dauphine.miage.motus.game.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long playerId;
    private String playerUsername;
    private Long wordId;
    private String secretWord;
    private int wordLength;
    private int maxAttempts = 6;
    private int attemptsUsed = 0;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.IN_PROGRESS;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime finishedAt;

    public enum GameStatus {
        IN_PROGRESS, WON, LOST
    }
}
