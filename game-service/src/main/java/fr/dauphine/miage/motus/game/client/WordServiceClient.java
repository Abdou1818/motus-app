package fr.dauphine.miage.motus.game.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WordServiceClient {

    private final RestTemplate restTemplate;

    @Value("${word.service.url:http://localhost:8083}")
    private String wordServiceUrl;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRandomWord(Integer length) {
        String url = wordServiceUrl + "/api/words/random" + (length != null ? "?length=" + length : "");
        return restTemplate.getForObject(url, Map.class);
    }

    @SuppressWarnings("unchecked")
    public boolean validateWord(String word) {
        try {
            Map<String, Object> body = Map.of("word", word);
            Map<String, Object> response = restTemplate.postForObject(
                    wordServiceUrl + "/api/words/validate", body, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("valid"));
        } catch (Exception e) {
            log.warn("Word service unavailable, accepting word: {}", word);
            return true; // fallback: accept word
        }
    }
}
