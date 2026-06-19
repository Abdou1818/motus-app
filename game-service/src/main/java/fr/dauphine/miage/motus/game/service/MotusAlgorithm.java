package fr.dauphine.miage.motus.game.service;

import fr.dauphine.miage.motus.game.dto.LetterResult;
import fr.dauphine.miage.motus.game.dto.LetterResult.LetterStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MotusAlgorithm {

    /**
     * Computes CORRECT/MISPLACED/ABSENT for each letter in the proposal.
     * Handles duplicate letters correctly: a letter is marked MISPLACED only
     * as many times as it appears in the secret word (minus CORRECT matches).
     */
    public List<LetterResult> evaluate(String secret, String proposal) {
        int len = secret.length();
        LetterResult[] results = new LetterResult[len];
        boolean[] secretUsed = new boolean[len];
        boolean[] proposalMatched = new boolean[len];

        // Pass 1: mark CORRECT
        for (int i = 0; i < len; i++) {
            results[i] = new LetterResult(proposal.charAt(i), i, LetterStatus.ABSENT);
            if (proposal.charAt(i) == secret.charAt(i)) {
                results[i].setStatus(LetterStatus.CORRECT);
                secretUsed[i] = true;
                proposalMatched[i] = true;
            }
        }

        // Pass 2: mark MISPLACED
        for (int i = 0; i < len; i++) {
            if (proposalMatched[i]) continue;
            for (int j = 0; j < len; j++) {
                if (!secretUsed[j] && proposal.charAt(i) == secret.charAt(j)) {
                    results[i].setStatus(LetterStatus.MISPLACED);
                    secretUsed[j] = true;
                    break;
                }
            }
        }

        List<LetterResult> list = new ArrayList<>();
        for (LetterResult r : results) list.add(r);
        return list;
    }
}
