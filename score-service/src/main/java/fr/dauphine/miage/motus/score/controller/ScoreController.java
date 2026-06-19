package fr.dauphine.miage.motus.score.controller;

import fr.dauphine.miage.motus.score.dto.LeaderboardEntryDto;
import fr.dauphine.miage.motus.score.dto.PlayerStatsDto;
import fr.dauphine.miage.motus.score.model.GameResult;
import fr.dauphine.miage.motus.score.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping
    public ResponseEntity<GameResult> recordResult(@RequestBody GameResult result) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scoreService.recordResult(result));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<GameResult>> getPlayerResults(@PathVariable Long playerId) {
        return ResponseEntity.ok(scoreService.getPlayerResults(playerId));
    }

    @GetMapping("/player/{playerId}/stats")
    public ResponseEntity<PlayerStatsDto> getPlayerStats(@PathVariable Long playerId) {
        return ResponseEntity.ok(scoreService.getPlayerStats(playerId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        return ResponseEntity.ok(scoreService.getLeaderboard());
    }

    @GetMapping
    public ResponseEntity<List<GameResult>> getAllResults() {
        return ResponseEntity.ok(scoreService.getAllResults());
    }
}
