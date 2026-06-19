package fr.dauphine.miage.motus.game.dto;

import fr.dauphine.miage.motus.game.model.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStartResponseDto {
    private Long gameId;
    private Long playerId;
    private String firstLetter;
    private int wordLength;
    private int maxAttempts;
    private Game.GameStatus status;
}
