package fr.dauphine.miage.motus.score.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDto {
    private Long playerId;
    private String username;
    private long gamesPlayed;
    private long gamesWon;
    private double winRate;
    private double avgAttempts;
}
