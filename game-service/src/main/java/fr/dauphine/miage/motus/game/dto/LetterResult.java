package fr.dauphine.miage.motus.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LetterResult {
    private char letter;
    private int position;
    private LetterStatus status;

    public enum LetterStatus {
        CORRECT, MISPLACED, ABSENT
    }
}
