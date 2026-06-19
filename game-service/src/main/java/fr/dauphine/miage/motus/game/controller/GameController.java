package fr.dauphine.miage.motus.game.controller;

import fr.dauphine.miage.motus.game.dto.AttemptResponseDto;
import fr.dauphine.miage.motus.game.dto.GameStartResponseDto;
import fr.dauphine.miage.motus.game.model.Attempt;
import fr.dauphine.miage.motus.game.model.Game;
import fr.dauphine.miage.motus.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameStartResponseDto> startGame(@RequestBody Map<String, Object> body) {
        Long playerId = ((Number) body.get("playerId")).longValue();
        Integer wordLength = body.containsKey("wordLength") ? ((Number) body.get("wordLength")).intValue() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.startGame(playerId, wordLength));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Game>> getPlayerGames(@PathVariable Long playerId) {
        return ResponseEntity.ok(gameService.getPlayerGames(playerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Game>> getAllGames(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(gameService.getAllGames(date, status));
    }

    @PostMapping("/{id}/attempts")
    public ResponseEntity<AttemptResponseDto> submitAttempt(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String word = body.get("word");
        return ResponseEntity.ok(gameService.submitAttempt(id, word));
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<List<Attempt>> getAttempts(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getAttempts(id));
    }
}
