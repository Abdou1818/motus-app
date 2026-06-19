package fr.dauphine.miage.motus.score.repository;

import fr.dauphine.miage.motus.score.model.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    List<GameResult> findByPlayerId(Long playerId);

    @Query("SELECT gr.playerId, gr.playerUsername, COUNT(gr) as gamesPlayed, " +
           "SUM(CASE WHEN gr.status = 'WON' THEN 1 ELSE 0 END) as gamesWon, " +
           "AVG(gr.attemptsUsed) as avgAttempts " +
           "FROM GameResult gr GROUP BY gr.playerId, gr.playerUsername " +
           "ORDER BY (SUM(CASE WHEN gr.status = 'WON' THEN 1.0 ELSE 0 END) / COUNT(gr)) DESC")
    List<Object[]> findLeaderboard();

    @Query("SELECT COUNT(gr), SUM(CASE WHEN gr.status = 'WON' THEN 1 ELSE 0 END), AVG(gr.attemptsUsed) " +
           "FROM GameResult gr WHERE gr.playerId = :playerId")
    List<Object[]> findStatsByPlayerId(@Param("playerId") Long playerId);
}
