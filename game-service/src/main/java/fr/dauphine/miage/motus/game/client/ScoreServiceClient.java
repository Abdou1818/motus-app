package fr.dauphine.miage.motus.game.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreServiceClient {

    private final RestTemplate restTemplate;

    @Value("${score.service.url:http://localhost:8084}")
    private String scoreServiceUrl;

    public void recordResult(Long gameId, Long playerId, String playerUsername,
                              String status, int attemptsUsed, int wordLength) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("gameId", gameId);
            body.put("playerId", playerId);
            body.put("playerUsername", playerUsername != null ? playerUsername : "player" + playerId);
            body.put("status", status);
            body.put("attemptsUsed", attemptsUsed);
            body.put("wordLength", wordLength);
            body.put("playedAt", LocalDateTime.now().toString());
            restTemplate.postForObject(scoreServiceUrl + "/api/scores", body, Map.class);
        } catch (Exception e) {
            log.warn("Score service unavailable, result not recorded: {}", e.getMessage());
        }
    }
}
