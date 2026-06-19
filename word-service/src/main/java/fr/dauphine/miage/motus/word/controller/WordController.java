package fr.dauphine.miage.motus.word.controller;

import fr.dauphine.miage.motus.word.model.Word;
import fr.dauphine.miage.motus.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @GetMapping("/random")
    public ResponseEntity<Word> getRandomWord(@RequestParam(required = false) Integer length) {
        return ResponseEntity.ok(wordService.getRandomWord(length));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Word> getWord(@PathVariable Long id) {
        return ResponseEntity.ok(wordService.getWordById(id));
    }

    @PostMapping
    public ResponseEntity<Word> addWord(@RequestBody Word word) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordService.addWord(word));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateWord(@RequestBody Map<String, String> body) {
        String word = body.get("word");
        boolean exists = wordService.validateWord(word);
        return ResponseEntity.ok(Map.of("valid", exists, "exists", exists));
    }

    @GetMapping
    public ResponseEntity<?> getAllWords() {
        return ResponseEntity.ok(wordService.getAllWords());
    }
}
