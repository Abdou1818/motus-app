package fr.dauphine.miage.motus.game;

import fr.dauphine.miage.motus.game.dto.LetterResult;
import fr.dauphine.miage.motus.game.dto.LetterResult.LetterStatus;
import fr.dauphine.miage.motus.game.service.MotusAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MotusAlgorithmTest {

    private MotusAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new MotusAlgorithm();
    }

    @Test
    void allCorrect() {
        List<LetterResult> result = algorithm.evaluate("MOTUS", "MOTUS");
        assertThat(result).allMatch(r -> r.getStatus() == LetterStatus.CORRECT);
    }

    @Test
    void allAbsent() {
        List<LetterResult> result = algorithm.evaluate("ABCDE", "FGHIJ");
        assertThat(result).allMatch(r -> r.getStatus() == LetterStatus.ABSENT);
    }

    @Test
    void misplacedLetters() {
        // MOTUS vs STUMO: letters present but all wrong position
        List<LetterResult> result = algorithm.evaluate("MOTUS", "STUMO");
        // S at pos0: secret has S at pos4 → MISPLACED
        assertThat(result.get(0).getStatus()).isEqualTo(LetterStatus.MISPLACED);
    }

    @Test
    void duplicateHandling() {
        // Secret MIRAGE, proposal MIROIR
        // M=CORRECT, I=CORRECT, R=CORRECT, O=ABSENT, I=ABSENT (I used at pos1), R=ABSENT (R used at pos2)
        List<LetterResult> result = algorithm.evaluate("MIRAGE", "MIROIR");
        assertThat(result.get(0).getStatus()).isEqualTo(LetterStatus.CORRECT); // M
        assertThat(result.get(1).getStatus()).isEqualTo(LetterStatus.CORRECT); // I
        assertThat(result.get(2).getStatus()).isEqualTo(LetterStatus.CORRECT); // R
        assertThat(result.get(3).getStatus()).isEqualTo(LetterStatus.ABSENT);  // O
        assertThat(result.get(4).getStatus()).isEqualTo(LetterStatus.ABSENT);  // I (already used)
        assertThat(result.get(5).getStatus()).isEqualTo(LetterStatus.ABSENT);  // R (already used)
    }

    @Test
    void firstLetterAlwaysGiven() {
        List<LetterResult> result = algorithm.evaluate("BRAVO", "BRAVO");
        assertThat(result.get(0).getStatus()).isEqualTo(LetterStatus.CORRECT);
        assertThat(result.get(0).getLetter()).isEqualTo('B');
    }

    @Test
    void partialMatch() {
        List<LetterResult> result = algorithm.evaluate("MOTUS", "MOTTO");
        // M=CORRECT, O=CORRECT, T=CORRECT, T=ABSENT (only one T in MOTUS), O=MISPLACED? O already used at pos1
        assertThat(result.get(0).getStatus()).isEqualTo(LetterStatus.CORRECT); // M
        assertThat(result.get(1).getStatus()).isEqualTo(LetterStatus.CORRECT); // O
        assertThat(result.get(2).getStatus()).isEqualTo(LetterStatus.CORRECT); // T
        assertThat(result.get(3).getStatus()).isEqualTo(LetterStatus.ABSENT);  // second T
    }
}
