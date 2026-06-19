package fr.dauphine.miage.motus.score.service;

import fr.dauphine.miage.motus.score.dto.LeaderboardEntryDto;
import fr.dauphine.miage.motus.score.dto.PlayerStatsDto;
import fr.dauphine.miage.motus.score.model.GameResult;
import fr.dauphine.miage.motus.score.repository.GameResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GameResultRepository gameResultRepository;

    public GameResult recordResult(GameResult result) {
        result.setPlayedAt(LocalDateTime.now());
        return gameResultRepository.save(result);
    }

    public List<GameResult> getPlayerResults(Long playerId) {
        return gameResultRepository.findByPlayerId(playerId);
    }

    public PlayerStatsDto getPlayerStats(Long playerId) {
        List<Object[]> rows = gameResultRepository.findStatsByPlayerId(playerId);
        PlayerStatsDto dto = new PlayerStatsDto();
        dto.setPlayerId(playerId);
        if (rows != null && !rows.isEmpty()) {
            Object[] stats = rows.get(0);
            if (stats != null && stats[0] != null) {
                long total = ((Number) stats[0]).longValue();
                long won = stats[1] != null ? ((Number) stats[1]).longValue() : 0L;
                double avg = stats[2] != null ? ((Number) stats[2]).doubleValue() : 0.0;
                dto.setGamesPlayed((int) total);
                dto.setGamesWon((int) won);
                dto.setWinRate(total > 0 ? (double) won / total * 100 : 0.0);
                dto.setAvgAttempts(avg);
            }
        }
        return dto;
    }

    public List<LeaderboardEntryDto> getLeaderboard() {
        List<Object[]> raw = gameResultRepository.findLeaderboard();
        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
        int rank = 0;
        for (Object[] row : raw) {
            if (rank >= 10) break;
            LeaderboardEntryDto entry = new LeaderboardEntryDto();
            entry.setPlayerId(((Number) row[0]).longValue());
            entry.setUsername((String) row[1]);
            long total = ((Number) row[2]).longValue();
            long won = ((Number) row[3]).longValue();
            double avg = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
            entry.setGamesPlayed(total);
            entry.setGamesWon(won);
            entry.setWinRate(total > 0 ? (double) won / total * 100 : 0.0);
            entry.setAvgAttempts(avg);
            leaderboard.add(entry);
            rank++;
        }
        return leaderboard;
    }

    public List<GameResult> getAllResults() {
        return gameResultRepository.findAll();
    }
}
