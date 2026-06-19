package fr.dauphine.miage.motus.player.service;

import fr.dauphine.miage.motus.player.dto.PlayerStatsDto;
import fr.dauphine.miage.motus.player.model.Player;
import fr.dauphine.miage.motus.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final RestTemplate restTemplate;

    @Value("${score.service.url:http://localhost:8084}")
    private String scoreServiceUrl;

    public Player register(Player player) {
        if (playerRepository.existsByUsername(player.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (playerRepository.existsByEmail(player.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        player.setCreatedAt(LocalDateTime.now());
        return playerRepository.save(player);
    }

    public Player getPlayer(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player updatePlayer(Long id, Player updated) {
        Player player = getPlayer(id);
        if (updated.getUsername() != null) player.setUsername(updated.getUsername());
        if (updated.getEmail() != null) player.setEmail(updated.getEmail());
        return playerRepository.save(player);
    }

    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }
        playerRepository.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    public PlayerStatsDto getPlayerStats(Long id) {
        Player player = getPlayer(id);
        try {
            Map<String, Object> stats = restTemplate.getForObject(
                    scoreServiceUrl + "/api/scores/player/" + id + "/stats",
                    Map.class);
            PlayerStatsDto dto = new PlayerStatsDto();
            dto.setPlayerId(id);
            dto.setUsername(player.getUsername());
            if (stats != null) {
                dto.setGamesPlayed(((Number) stats.getOrDefault("gamesPlayed", 0)).intValue());
                dto.setGamesWon(((Number) stats.getOrDefault("gamesWon", 0)).intValue());
                dto.setWinRate(((Number) stats.getOrDefault("winRate", 0.0)).doubleValue());
                dto.setAvgAttempts(((Number) stats.getOrDefault("avgAttempts", 0.0)).doubleValue());
            }
            return dto;
        } catch (Exception e) {
            PlayerStatsDto dto = new PlayerStatsDto();
            dto.setPlayerId(id);
            dto.setUsername(player.getUsername());
            return dto;
        }
    }
}
