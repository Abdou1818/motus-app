package fr.dauphine.miage.motus.game;

import fr.dauphine.miage.motus.game.client.ScoreServiceClient;
import fr.dauphine.miage.motus.game.client.WordServiceClient;
import fr.dauphine.miage.motus.game.dto.AttemptResponseDto;
import fr.dauphine.miage.motus.game.dto.GameStartResponseDto;
import fr.dauphine.miage.motus.game.dto.LetterResult.LetterStatus;
import fr.dauphine.miage.motus.game.model.Game;
import fr.dauphine.miage.motus.game.service.GameService;
import fr.dauphine.miage.motus.game.repository.AttemptRepository;
import fr.dauphine.miage.motus.game.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @MockBean
    private WordServiceClient wordServiceClient;

    @MockBean
    private ScoreServiceClient scoreServiceClient;

    @BeforeEach
    void setup() {
        when(wordServiceClient.getRandomWord(anyInt())).thenReturn(
                Map.of("id", 1, "value", "MOTUS", "length", 5));
        when(wordServiceClient.getRandomWord(null)).thenReturn(
                Map.of("id", 1, "value", "MOTUS", "length", 5));
        when(wordServiceClient.validateWord(any())).thenReturn(true);
        doNothing().when(scoreServiceClient).recordResult(any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void startGame_returnsFirstLetterOnly() {
        GameStartResponseDto dto = gameService.startGame(1L, null);
        assertThat(dto.getGameId()).isNotNull();
        assertThat(dto.getFirstLetter()).isEqualTo("M");
        assertThat(dto.getWordLength()).isEqualTo(5);
        assertThat(dto.getStatus()).isEqualTo(Game.GameStatus.IN_PROGRESS);
    }

    @Test
    void submitAttempt_correctWord_winsGame() {
        GameStartResponseDto start = gameService.startGame(1L, null);
        AttemptResponseDto result = gameService.submitAttempt(start.getGameId(), "MOTUS");
        assertThat(result.getGameStatus()).isEqualTo(Game.GameStatus.WON);
        assertThat(result.getSecretWord()).isEqualTo("MOTUS");
        assertThat(result.getResult()).allMatch(r -> r.getStatus() == LetterStatus.CORRECT);
    }

    @Test
    void submitAttempt_wrongWord_continuesGame() {
        GameStartResponseDto start = gameService.startGame(1L, null);
        AttemptResponseDto result = gameService.submitAttempt(start.getGameId(), "MONDE");
        assertThat(result.getGameStatus()).isEqualTo(Game.GameStatus.IN_PROGRESS);
        assertThat(result.getSecretWord()).isNull();
    }

    @Test
    void submitAttempt_afterSixTries_losesGame() {
        GameStartResponseDto start = gameService.startGame(1L, null);
        Long gameId = start.getGameId();
        AttemptResponseDto last = null;
        for (int i = 0; i < 6; i++) {
            last = gameService.submitAttempt(gameId, "MONDE");
        }
        assertThat(last.getGameStatus()).isEqualTo(Game.GameStatus.LOST);
        assertThat(last.getSecretWord()).isEqualTo("MOTUS");
    }

    @Test
    void submitAttempt_gameAlreadyFinished_throwsConflict() {
        GameStartResponseDto start = gameService.startGame(1L, null);
        gameService.submitAttempt(start.getGameId(), "MOTUS");
        assertThatThrownBy(() -> gameService.submitAttempt(start.getGameId(), "MONDE"))
                .hasMessageContaining("already finished");
    }

    @Test
    void submitAttempt_correctLetterEvaluation() {
        GameStartResponseDto start = gameService.startGame(1L, null);
        // MOTUS vs MONDE: M=CORRECT, O=CORRECT, N=ABSENT, D=ABSENT, E=ABSENT
        AttemptResponseDto result = gameService.submitAttempt(start.getGameId(), "MONDE");
        assertThat(result.getResult().get(0).getStatus()).isEqualTo(LetterStatus.CORRECT); // M
        assertThat(result.getResult().get(1).getStatus()).isEqualTo(LetterStatus.CORRECT); // O
        assertThat(result.getResult().get(2).getStatus()).isEqualTo(LetterStatus.ABSENT);  // N
        assertThat(result.getResult().get(3).getStatus()).isEqualTo(LetterStatus.ABSENT);  // D
        assertThat(result.getResult().get(4).getStatus()).isEqualTo(LetterStatus.ABSENT);  // E
    }
}
