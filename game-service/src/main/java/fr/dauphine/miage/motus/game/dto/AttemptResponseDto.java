package fr.dauphine.miage.motus.game.dto;

import fr.dauphine.miage.motus.game.model.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResponseDto {
    private Long attemptId;
    private int attemptNumber;
    private String proposedWord;
    private List<LetterResult> result;
    private Game.GameStatus gameStatus;
    private String secretWord; // only revealed when game is over
    private int remainingAttempts;
}
