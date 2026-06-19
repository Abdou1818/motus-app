package fr.dauphine.miage.motus.word.service;

import fr.dauphine.miage.motus.word.model.Word;
import fr.dauphine.miage.motus.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    public Word getRandomWord(Integer length) {
        return wordRepository.findRandomWord(length)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No word found"));
    }

    public Word getWordById(Long id) {
        return wordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found"));
    }

    public Word addWord(Word word) {
        word.setValue(word.getValue().toUpperCase());
        word.setLength(word.getValue().length());
        if (wordRepository.findByValueIgnoreCase(word.getValue()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Word already exists");
        }
        return wordRepository.save(word);
    }

    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found");
        }
        wordRepository.deleteById(id);
    }

    public boolean validateWord(String word) {
        return wordRepository.findByValueIgnoreCase(word).isPresent();
    }

    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }
}
