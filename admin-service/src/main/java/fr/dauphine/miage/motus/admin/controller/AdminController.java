package fr.dauphine.miage.motus.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final RestTemplate restTemplate;

    @Value("${admin.key:admin-secret}")
    private String adminKey;

    @Value("${game.service.url:http://localhost:8082}")
    private String gameServiceUrl;

    @Value("${player.service.url:http://localhost:8081}")
    private String playerServiceUrl;

    @Value("${score.service.url:http://localhost:8084}")
    private String scoreServiceUrl;

    private void checkAdminKey(String key) {
        if (key == null || !adminKey.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin key");
        }
    }

    @GetMapping("/games")
    public ResponseEntity<?> getGames(
            @RequestHeader(value = "X-Admin-Key", required = false) String key,
            @RequestParam(required = false) Long playerId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {
        checkAdminKey(key);
        try {
            String url;
            if (playerId != null) {
                url = gameServiceUrl + "/api/games/player/" + playerId;
            } else {
                StringBuilder sb = new StringBuilder(gameServiceUrl + "/api/games/all?");
                if (date   != null) sb.append("date=").append(date).append("&");
                if (status != null) sb.append("status=").append(status).append("&");
                url = sb.toString().replaceAll("[?&]$", "");
            }
            Object[] games = restTemplate.getForObject(url, Object[].class);
            return ResponseEntity.ok(games != null ? games : new Object[0]);
        } catch (Exception e) {
            log.warn("Game service unavailable: {}", e.getMessage());
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/players")
    public ResponseEntity<?> getPlayers(
            @RequestHeader(value = "X-Admin-Key", required = false) String key) {
        checkAdminKey(key);
        try {
            Object[] players = restTemplate.getForObject(playerServiceUrl + "/api/players", Object[].class);
            return ResponseEntity.ok(players != null ? players : new Object[0]);
        } catch (Exception e) {
            log.warn("Player service unavailable: {}", e.getMessage());
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getGlobalStats(
            @RequestHeader(value = "X-Admin-Key", required = false) String key) {
        checkAdminKey(key);
        Map<String, Object> stats = new HashMap<>();
        try {
            Object[] scores = restTemplate.getForObject(scoreServiceUrl + "/api/scores", Object[].class);
            stats.put("totalGames", scores != null ? scores.length : 0);
        } catch (Exception e) {
            stats.put("totalGames", "unavailable");
        }
        try {
            Object[] players = restTemplate.getForObject(playerServiceUrl + "/api/players", Object[].class);
            stats.put("totalPlayers", players != null ? players.length : 0);
        } catch (Exception e) {
            stats.put("totalPlayers", "unavailable");
        }
        try {
            Object leaderboard = restTemplate.getForObject(scoreServiceUrl + "/api/scores/leaderboard", Object.class);
            stats.put("leaderboard", leaderboard);
        } catch (Exception e) {
            stats.put("leaderboard", Collections.emptyList());
        }
        return ResponseEntity.ok(stats);
    }
}
