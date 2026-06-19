package fr.dauphine.miage.motus.word.repository;

import fr.dauphine.miage.motus.word.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findByValueIgnoreCase(String value);
    boolean existsByValue(String value);
    List<Word> findByLength(int length);

    @Query(value = "SELECT * FROM words WHERE (:length IS NULL OR length = :length) ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Word> findRandomWord(@Param("length") Integer length);

    long count();
}
