package fr.dauphine.miage.motus.game.repository;

import fr.dauphine.miage.motus.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByPlayerId(Long playerId);
    List<Game> findByStatus(Game.GameStatus status);
    List<Game> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<Game> findByPlayerIdAndStatus(Long playerId, Game.GameStatus status);
    List<Game> findByCreatedAtBetweenAndStatus(LocalDateTime from, LocalDateTime to, Game.GameStatus status);
}
