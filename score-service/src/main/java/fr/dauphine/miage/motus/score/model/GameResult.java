package fr.dauphine.miage.motus.score.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gameId;
    private Long playerId;
    private String playerUsername;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private int attemptsUsed;
    private int wordLength;
    private LocalDateTime playedAt = LocalDateTime.now();

    public enum GameStatus {
        WON, LOST
    }
}
