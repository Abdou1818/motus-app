package fr.dauphine.miage.motus.score.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsDto {
    private Long playerId;
    private int gamesPlayed;
    private int gamesWon;
    private double winRate;
    private double avgAttempts;
}
