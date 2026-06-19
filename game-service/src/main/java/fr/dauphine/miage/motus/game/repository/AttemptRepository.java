package fr.dauphine.miage.motus.game.repository;

import fr.dauphine.miage.motus.game.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByGameIdOrderByAttemptNumber(Long gameId);
    long countByGameId(Long gameId);
}
