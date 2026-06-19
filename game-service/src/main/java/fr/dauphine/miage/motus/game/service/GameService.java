package fr.dauphine.miage.motus.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dauphine.miage.motus.game.client.ScoreServiceClient;
import fr.dauphine.miage.motus.game.client.WordServiceClient;
import fr.dauphine.miage.motus.game.dto.AttemptResponseDto;
import fr.dauphine.miage.motus.game.dto.GameStartResponseDto;
import fr.dauphine.miage.motus.game.dto.LetterResult;
import fr.dauphine.miage.motus.game.model.Attempt;
import fr.dauphine.miage.motus.game.model.Game;
import fr.dauphine.miage.motus.game.repository.AttemptRepository;
import fr.dauphine.miage.motus.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final AttemptRepository attemptRepository;
    private final WordServiceClient wordServiceClient;
    private final ScoreServiceClient scoreServiceClient;
    private final MotusAlgorithm motusAlgorithm;
    private final ObjectMapper objectMapper;

    public GameStartResponseDto startGame(Long playerId, Integer wordLength) {
        Map<String, Object> wordData = wordServiceClient.getRandomWord(wordLength);
        if (wordData == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Word service unavailable");
        }

        String secretWord = ((String) wordData.get("value")).toUpperCase();
        Long wordId = ((Number) wordData.get("id")).longValue();

        Game game = new Game();
        game.setPlayerId(playerId);
        game.setWordId(wordId);
        game.setSecretWord(secretWord);
        game.setWordLength(secretWord.length());
        game.setMaxAttempts(6);
        game.setAttemptsUsed(0);
        game.setStatus(Game.GameStatus.IN_PROGRESS);
        game.setCreatedAt(LocalDateTime.now());
        game = gameRepository.save(game);

        GameStartResponseDto dto = new GameStartResponseDto();
        dto.setGameId(game.getId());
        dto.setPlayerId(playerId);
        dto.setFirstLetter(String.valueOf(secretWord.charAt(0)));
        dto.setWordLength(secretWord.length());
        dto.setMaxAttempts(game.getMaxAttempts());
        dto.setStatus(game.getStatus());
        return dto;
    }

    public Game getGame(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
    }

    public List<Game> getPlayerGames(Long playerId) {
        return gameRepository.findByPlayerId(playerId);
    }

    public List<Game> getAllGames(String date, String status) {
        LocalDateTime from = null, to = null;
        if (date != null && !date.isBlank()) {
            java.time.LocalDate d = java.time.LocalDate.parse(date);
            from = d.atStartOfDay();
            to   = d.plusDays(1).atStartOfDay();
        }
        Game.GameStatus gameStatus = null;
        if (status != null && !status.isBlank()) {
            try { gameStatus = Game.GameStatus.valueOf(status.toUpperCase()); } catch (Exception ignored) {}
        }

        if (from != null && gameStatus != null) return gameRepository.findByCreatedAtBetweenAndStatus(from, to, gameStatus);
        if (from != null)                       return gameRepository.findByCreatedAtBetween(from, to);
        if (gameStatus != null)                 return gameRepository.findByStatus(gameStatus);
        return gameRepository.findAll();
    }

    public AttemptResponseDto submitAttempt(Long gameId, String proposedWord) {
        Game game = getGame(gameId);

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game is already finished");
        }

        proposedWord = proposedWord.toUpperCase().trim();

        if (proposedWord.length() != game.getSecretWord().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Word length must be " + game.getSecretWord().length());
        }

        // Règle Motus : le mot proposé doit commencer par la même lettre que le mot secret
        char secretFirst   = game.getSecretWord().charAt(0);
        char proposedFirst = proposedWord.charAt(0);
        if (proposedFirst != secretFirst) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le mot doit commencer par la lettre " + secretFirst);
        }

        if (!wordServiceClient.validateWord(proposedWord)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Word not in dictionary");
        }

        long attemptCount = attemptRepository.countByGameId(gameId);
        int attemptNumber = (int) attemptCount + 1;

        List<LetterResult> letterResults = motusAlgorithm.evaluate(game.getSecretWord(), proposedWord);

        String resultJson;
        try {
            resultJson = objectMapper.writeValueAsString(letterResults);
        } catch (JsonProcessingException e) {
            resultJson = "[]";
        }

        Attempt attempt = new Attempt();
        attempt.setGameId(gameId);
        attempt.setAttemptNumber(attemptNumber);
        attempt.setProposedWord(proposedWord);
        attempt.setResult(resultJson);
        attempt.setCreatedAt(LocalDateTime.now());
        attempt = attemptRepository.save(attempt);

        boolean allCorrect = letterResults.stream()
                .allMatch(r -> r.getStatus() == LetterResult.LetterStatus.CORRECT);

        game.setAttemptsUsed(attemptNumber);
        if (allCorrect) {
            game.setStatus(Game.GameStatus.WON);
            game.setFinishedAt(LocalDateTime.now());
            gameRepository.save(game);
            scoreServiceClient.recordResult(gameId, game.getPlayerId(),
                    game.getPlayerUsername(), "WON", attemptNumber, game.getSecretWord().length());
        } else if (attemptNumber >= game.getMaxAttempts()) {
            game.setStatus(Game.GameStatus.LOST);
            game.setFinishedAt(LocalDateTime.now());
            gameRepository.save(game);
            scoreServiceClient.recordResult(gameId, game.getPlayerId(),
                    game.getPlayerUsername(), "LOST", attemptNumber, game.getSecretWord().length());
        } else {
            gameRepository.save(game);
        }

        AttemptResponseDto dto = new AttemptResponseDto();
        dto.setAttemptId(attempt.getId());
        dto.setAttemptNumber(attemptNumber);
        dto.setProposedWord(proposedWord);
        dto.setResult(letterResults);
        dto.setGameStatus(game.getStatus());
        dto.setRemainingAttempts(game.getMaxAttempts() - attemptNumber);
        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            dto.setSecretWord(game.getSecretWord());
        }
        return dto;
    }

    public List<Attempt> getAttempts(Long gameId) {
        getGame(gameId); // verify exists
        return attemptRepository.findByGameIdOrderByAttemptNumber(gameId);
    }
}
